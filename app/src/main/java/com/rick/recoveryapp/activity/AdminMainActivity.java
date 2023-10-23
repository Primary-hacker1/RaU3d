/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.rick.recoveryapp.activity;

import static com.rick.recoveryapp.R.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;


import com.jeremyliao.liveeventbus.LiveEventBus;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.activity.u3d.U3DActivity;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.base.MyService;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.bluetooth.BluetoothChatService;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.bluetooth.BtKeepService;
import com.rick.recoveryapp.bluetooth.BtReceiver;
import com.rick.recoveryapp.chart.MyAVG;
import com.rick.recoveryapp.databinding.ActivityMainBinding;
import com.rick.recoveryapp.entity.LiveMessage;
import com.rick.recoveryapp.fragment.setting.SettingFragment;
import com.rick.recoveryapp.greendao.EcgDataDBDao;
import com.rick.recoveryapp.greendao.MacDrDao;
import com.rick.recoveryapp.greendao.entity.EcgDataDB;
import com.rick.recoveryapp.greendao.entity.MacDr;
import com.rick.recoveryapp.utils.DateUtil;
import com.rick.recoveryapp.utils.LocalConfig;
import com.rick.recoveryapp.utils.XToastUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AdminMainActivity extends XPageActivity implements ClickUtils.OnClick2ExitListener, BtReceiver.Listener {

    Context context;
    Intent intent;
    ActivityMainBinding binding;
    BtDataPro btDataPro;
    public static AdminMainActivity instance;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ActivitytTest", "开始调用onCreate方法");
        try {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            StatusBarUtils.translucent(this);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
            instance = this;

            SharedPreferences shared = getSharedPreferences("Personal", MODE_PRIVATE);
            boolean isfer = shared.getBoolean("isfer", true);
            if (isfer) {
                LoginActivity.loginActivity.finish();
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean("isfer", false);
                editor.commit();
            }
            // mBtReceiver = new BtReceiver(context, this);//注册蓝牙广播
            // binding.mainTxtBt.setCenterString("蓝牙未连接");
            btDataPro = new BtDataPro();
            if (LocalConfig.daoSession == null) {
                BaseApplication myApp = (BaseApplication) getApplication();
                LocalConfig.daoSession = myApp.getDaoSession();
            }
            initClick();
            init();
            binding.mainTxtDate.setCenterString(DateUtil.getWeekOfDate(new Date()));
            binding.mainTxtDate.setCenterBottomString(DateUtil.getNowDate());

            if (!LocalConfig.isControl) {
//                if (!BaseApplication.mBluetoothAdapter.isEnabled()) {
//                    //直接开启蓝牙
//                    BaseApplication.mBluetoothAdapter.enable();
//                } //否则创建蓝牙连接服务对象
//                else if (BaseApplication.mConnectService == null) {
//                    //  BaseApplication.mConnectService = new BluetoothService(BaseApplication.mHandler);
//                    BaseApplication.AutoConnect();
//                }
                Intent intent = new Intent(this, BtKeepService.class);
                startService(intent);
            }
            controlBT();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        try {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            context = this;
//
//            //   mBtReceiver = new BtReceiver(context, this);//注册蓝牙广播
//            // binding.mainTxtBt.setCenterString("蓝牙未连接");
//            btDataPro = new BtDataPro();
//            if (LocalConfig.daoSession == null) {
//                BaseApplication myApp = (BaseApplication) getApplication();
//                LocalConfig.daoSession = myApp.getDaoSession();
//
//            }
//            initClick();
//            init();
//
//            if (!LocalConfig.isControl) {
//
//                binding.mainTxtDate.setCenterString(DateUtil.getWeekOfDate(new Date()));
//                binding.mainTxtDate.setCenterBottomString(DateUtil.getNowDate());
////                if (!BaseApplication.mBluetoothAdapter.isEnabled()) {
////                    //直接开启蓝牙
////                    BaseApplication.mBluetoothAdapter.enable();
////                } //否则创建蓝牙连接服务对象
////                else if (BaseApplication.mConnectService == null) {
////                    //  BaseApplication.mConnectService = new BluetoothService(BaseApplication.mHandler);
////                    BaseApplication.AutoConnect();
////                }
//                Intent intents = new Intent(this, BtKeepService.class);
//                startService(intents);
//            }
//            controlBT();
//
//        } catch (Exception ex) {
//            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

    public void controlBT() {
        LiveEventBus
                .get("BT_CONNECTED", LiveMessage.class)
                .observe(this, new Observer<LiveMessage>() {
                    @Override
                    public void onChanged(@Nullable LiveMessage msg) {
                        try {
                            if (msg.getIsConnt()) {
                                Log.d("BT_CONNECTED1", LocalConfig.isControl + " 1");
                                binding.mainImgLink.setBackgroundResource(drawable.img_bt_open);
                                binding.mainImgLink.setEnabled(false);
                                Toast.makeText(AdminMainActivity.this, msg.getMessage(), Toast.LENGTH_SHORT).show();
                                btDataPro.sendBTMessage(btDataPro.CONNECT_CLOSE);
                                btDataPro.sendBTMessage(btDataPro.GetCmdCode(LocalConfig.ecgmac, LocalConfig.bloodmac, LocalConfig.oxygenmac));
                            } else {
                                Log.d("BT_CONNECTED1", LocalConfig.isControl + " 2");
                                binding.mainImgLink.setBackgroundResource(drawable.img_bt_close);
                                binding.mainImgLink.setEnabled(true);
                                if (!msg.getMessage().equals("")) {
                                    Toast.makeText(AdminMainActivity.this, msg.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.d("AdminMainActivity", e.getMessage());
                        }
                    }
                });
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        if (BaseApplication.mConnectService != null) {
            //蓝牙闲置状态
            if (BaseApplication.mConnectService.getState() == BluetoothChatService.STATE_NONE) {
                if (BaseApplication.liveMessage != null) {
                    binding.mainImgLink.setBackgroundResource(drawable.img_bt_close);
                    binding.mainImgLink.setEnabled(true);
                    //监听其他蓝牙主设备
                    BaseApplication.mConnectService.start();
                }
                //蓝牙已连接
            } else if (BaseApplication.mConnectService.getState() == BluetoothChatService.STATE_CONNECTED) {
                if (BaseApplication.liveMessage != null) {
                    binding.mainImgLink.setBackgroundResource(drawable.img_bt_open);
                    binding.mainImgLink.setEnabled(false);
                }
            }
        }
    }

    public void initClick() {
        //获取GPS现在的状态（打开或是关闭状态）
        //   boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER);

        binding.mainImgLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (BaseApplication.mConnectService != null) {
//                    BaseApplication.AutoConnect();
//                }
                BaseApplication.AutoConnect();
            }
        });

        binding.mainImgActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalConfig.isControl) {
                    LocalConfig.ModType = 0;
                    intent = new Intent(context, DialogActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "请先连接蓝牙设备！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.mainImgPassive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalConfig.isControl) {
                    LocalConfig.ModType = 1;
                    intent = new Intent(context, DialogActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "请先连接蓝牙设备！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.mainImgIntelligence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalConfig.isControl) {
                    LocalConfig.ModType = 2;
                    intent = new Intent(context, DialogActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "请先连接蓝牙设备！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.mainImgEntertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (LocalConfig.isControl) {
                        LocalConfig.ModType = 3;
                        intent = new Intent(context, DialogActivity.class);
                        startActivity(intent);
                        // finish();
                    } else {
                        Toast.makeText(context, "请先连接蓝牙设备！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ToastUtils.toast(ex.getMessage());
                }
            }
        });

        binding.mainImgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.mainImgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    public void OpenBluetooh() {
//
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            return;
//        }
//        if (!bluetoothAdapter.isEnabled()) {
//            boolean res = bluetoothAdapter.enable();
//            if (res == true) {
//                XToastUtils.success("蓝牙已开启");
//            } else {
//                XToastUtils.warning("蓝牙开启失败");
//            }
//        } else if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
//            XToastUtils.success("蓝牙已开启");
//        } else {
//            XToastUtils.warning("蓝牙开启失败");
//        }
//    }

    private void mayRequestLocation() {
        //  需要定位权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AdminMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
//        if (Build.VERSION.SDK_INT >= 23) {
//            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
//            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
//                //判断是否需要 向用户解释，为什么要申请该权限
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
//                    Toast.makeText(this, "动态请求权限", Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
//                return;
//            } else {

//            }
//        } else {
//
//        }
    }

    // 初始化方法
    public void init() {
        // 新时间
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        binding.mainTxtTime.setCenterString(sdf.format(new Date()));
        // 更新时间的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                binding.mainTxtTime.setCenterString(sdf.format(new Date()));
            }
        }
    };

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, AdminMainActivity.this);
        }
        return true;
    }

    @Override
    public void onRetry() {
        Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onExit() {
        btDataPro.sendBTMessage(btDataPro.CONNECT_CLOSE);
        if (BaseApplication.mConnectService != null) {
            BaseApplication.mConnectService.stop();
        }
        BaseApplication.mBluetoothAdapter = null;
        BaseApplication.getContext().unregisterReceiver(BaseApplication.mBtReceiver);
        Intent stopIntent = new Intent(this, BtKeepService.class);
        stopService(stopIntent);
        if (U3DActivity.u3dinstance != null) {
            U3DActivity.u3dinstance.finish();
        }

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        XUtil.exitApp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ActivitytTest", "onDestroy");
        btDataPro = null;
//        Intent stopIntent = new Intent(this, BtKeepService.class);
//        stopService(stopIntent);
    }

    @Override
    public void foundDev(BluetoothDevice dev) {

    }
}
