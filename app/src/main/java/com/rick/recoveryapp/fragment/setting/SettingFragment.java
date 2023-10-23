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

package com.rick.recoveryapp.fragment.setting;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.efs.sdk.base.core.util.Log;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.activity.MacDrDialog;
import com.rick.recoveryapp.adapter.BtDevAdapter;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.bluetooth.BtReceiver;
import com.rick.recoveryapp.databinding.ActivityTestBinding;
import com.rick.recoveryapp.databinding.FragmentSettingBinding;
import com.rick.recoveryapp.greendao.MacDrDao;
import com.rick.recoveryapp.greendao.entity.MacDr;
import com.rick.recoveryapp.utils.HideKeyboard;
import com.rick.recoveryapp.utils.LocalConfig;
import com.rick.recoveryapp.utils.XToastUtils;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.simplelist.MaterialSimpleListAdapter;
import com.xuexiang.xui.widget.dialog.materialdialog.simplelist.MaterialSimpleListItem;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment implements BtReceiver.Listener, BtDevAdapter.Listener {

    FragmentSettingBinding binding;
    MacDrDao macDrDao;
    public static BtReceiver mBtReceiver;
    private BtDevAdapter mBtDevAdapter = new BtDevAdapter(this);
    int i;//记录点击次数
    long time;//记录时间差
    String flag;//标记点击
    Boolean isLock = true;
    BtDataPro btDataPro;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(getLayoutInflater(), container, false);
        macDrDao = LocalConfig.daoSession.getMacDrDao();
        btDataPro = new BtDataPro();
        initClinck();
        GetMac();

        mBtReceiver = new BtReceiver(LocalConfig.SettingContext, this);//注册蓝牙广播

        return binding.getRoot();
    }

    public void initClinck() {

//        binding.settingBtnRefresh.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onClick(View v) {
//                //开始刷新蓝牙列表
//                if (isLock) {
//                    Toast.makeText(LocalConfig.SettingContext, "请先解锁按钮！", Toast.LENGTH_SHORT).show();
//                } else {
//                    binding.rvList.setLayoutManager(new LinearLayoutManager(LocalConfig.SettingContext));
//                    binding.rvList.setAdapter(mBtDevAdapter);
//                    BluetoothAdapter.getDefaultAdapter().startDiscovery();
//                }
//
//            }
//        });

        binding.settingBtnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogLoader.getInstance().showInputDialog(
                        LocalConfig.SettingContext,
                        R.drawable.icon_warning,
                        getString(R.string.tip_warning),
                        getString(R.string.content_warning),
                        new InputInfo(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                                getString(R.string.hint_please_input_password)),
                        (dialog, input) -> XToastUtils.toast(input.toString()),
                        getString(R.string.lab_continue),
                        (dialog, which) -> {
                            KeyboardUtils.hideSoftInput(dialog);
                            dialog.dismiss();
                            if (dialog instanceof MaterialDialog) {
                                String changePass = ((MaterialDialog) dialog).getInputEditText().getText().toString();
                                if (changePass.equals("654321")) {
//                                    isLock = false;
//                                    binding.settingBtnUnlock.setEnabled(false);
//                                    binding.settingTxtStact.setCenterString("");
                                    //    binding.settingTxtStact.setCenterTextColor(#299EE3);
                                    Intent in = new Intent(LocalConfig.SettingContext, MacDrDialog.class);
                                    in.putExtra("isfer", "setting");
                                    startActivity(in);

                                } else {
                                    Toast.makeText(LocalConfig.SettingContext, "密码输入错误！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        getString(R.string.lab_change),
                        (dialog, which) -> {
                            KeyboardUtils.hideSoftInput(dialog);
                            dialog.dismiss();
                        });
            }
        });

//        binding.settingBtnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //setmac();
//                if (isLock) {
//                    Toast.makeText(LocalConfig.SettingContext, "请先解锁按钮！", Toast.LENGTH_SHORT).show();
//                } else {
//                    isLock = true;
//                    binding.settingBtnUnlock.setEnabled(true);
//                    binding.settingTxtStact.setCenterString("按钮锁定中");
//                    binding.settingTxtStact.setCenterTextColor(R.color.xui_config_color_gray_7);
//                    SetMac();
//                    List<MacDr> macDrList = macDrDao.loadAll();
//                    if (macDrList.size() > 0) {
//                        for (int i = 0; i < macDrList.size(); i++) {
//                            LocalConfig.bluemac = BaseApplication.deleteCharString(macDrList.get(0).getBlueThMac());
//                            LocalConfig.ecgmac = macDrList.get(0).getEcgMac();
//                            LocalConfig.bloodmac = macDrList.get(0).getBloodMac();
//                            LocalConfig.oxygenmac = macDrList.get(0).getOxygenMac();
//                        }
//                    } else {
//                        Toast.makeText(LocalConfig.SettingContext, "蓝牙地址获取失败！", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
    }

    public void GetMac() {
        List<MacDr> macDrList = macDrDao.loadAll();
        if (macDrList.size() > 0) {
            for (int i = 0; i < macDrList.size(); i++) {

                binding.settingMacBluetooth.setCenterString(macDrList.get(0).getBlueThMac());
                binding.settingMacEcgmac.setCenterString(macDrList.get(0).getEcgMac());
                binding.settingMacBloodmac.setCenterString(macDrList.get(0).getBloodMac());
                binding.settingMacOxygenmac.setCenterString(macDrList.get(0).getOxygenMac());
            }
        } else {
            Toast.makeText(LocalConfig.SettingContext, "蓝牙地址获取失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void SetMac() {
        try {
            String blue = binding.settingMacBluetooth.getCenterString();
            String ecg = binding.settingMacEcgmac.getCenterString();
            String blood = binding.settingMacBloodmac.getCenterString();
            String oxygen = binding.settingMacOxygenmac.getCenterString();


            macDrDao.deleteAll();
            List<MacDr> macDrList = macDrDao.loadAll();
            if (macDrList.size() <= 0) {
//                    String bluethmac = "00:1B:10:F1:EE:88";
//                    String ecgmac = "D2:08:AA:BB:37:AE";
//                    String bloodmac = "A4:C1:38:44:16:0C";
//                    String oxygen = "00:A0:50:3B:CB:AC";
                MacDr macDr = new MacDr();
                macDr.setBlueThMac(blue);
                macDr.setEcgMac(ecg);
                macDr.setBloodMac(blood);
                macDr.setOxygenMac(oxygen);
                macDrDao.insert(macDr);
                Toast.makeText(LocalConfig.SettingContext, "保存成功！", Toast.LENGTH_LONG).show();
            }
            //先查询数据库是否有Mac地址记录


        } catch (Exception ex) {
            Toast.makeText(LocalConfig.SettingContext, "数据库错误" + ex.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    //
    @Override
    public void onItemClick(BluetoothDevice dev) {
        i++;
        time = System.currentTimeMillis() - time;//记录时间差
        if (i == 1) {
            flag = dev.getAddress();//第一次点击，获取当前position
            //    Toast.makeText(LocalConfig.SettingContext, "第一次点击" + flag, Toast.LENGTH_SHORT).show();
            Log.d("onItemClick", "第一次点击" + flag);
        } else {
            //第二次点击，判断是否是当前position
            if (flag == dev.getAddress()) {
                if (time < 1000) {
                    //是双击操作
                    //  Toast.makeText(LocalConfig.SettingContext, "双击了" + flag, Toast.LENGTH_SHORT).show();
                    i = 0;
                    time = 0;
                    Log.d("onItemClick", "双击了" + flag);
                    if (isLock) {
                        Toast.makeText(LocalConfig.SettingContext, "请先解锁按钮！", Toast.LENGTH_SHORT).show();
                    } else {

                        showPictureItemDialog(dev.getAddress());
                    }

                } else {
                    //不是双击操作
                    i = 0;
                    time = 0;
                    flag = dev.getAddress();
                    i++;//记录点击次数
                    time = System.currentTimeMillis() - time;//继续记录时间差
                    Log.d("onItemClick", "2次点击时间间隔大于1s" + flag);
                    // Toast.makeText(LocalConfig.SettingContext, "2次点击时间间隔大于1s" + flag, Toast.LENGTH_SHORT).show();
                }
            } else {
                //不是，初始化
                i = 0;
                time = 0;
                flag = dev.getAddress();
                i++;//记录点击次数
                time = System.currentTimeMillis() - time;//记录时间差
            }
        }
    }

    /**
     * 带图标条目的Dialog
     */
    private void showPictureItemDialog(String mac) {
        List<MaterialSimpleListItem> list = new ArrayList<>();
        list.add(new MaterialSimpleListItem.Builder(getContext())
                .content(R.string.lab_mac)
                .icon(R.drawable.mac)
                .build());
        list.add(new MaterialSimpleListItem.Builder(getContext())
                .content(R.string.lab_ecg)
                .icon(R.drawable.bpm)
                .build());
        list.add(new MaterialSimpleListItem.Builder(getContext())
                .content(R.string.lab_blood)
                .icon(R.drawable.mmhg_s)
                .build());
        list.add(new MaterialSimpleListItem.Builder(getContext())
                .content(R.string.lab_oxygen)
                .icon(R.drawable.hbo2)
                .build());
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(list)
                .setOnItemClickListener((dialog, index, item) -> ChangeTxt(item.getContent().toString(), mac));
        new MaterialDialog.Builder(getContext()).adapter(adapter, null).show();
    }

    public void ChangeTxt(String content, String mac) {
        switch (content) {
            case "主板蓝牙":
                binding.settingMacBluetooth.setCenterString(mac);
                break;

            case "心电蓝牙":
                binding.settingMacEcgmac.setCenterString(mac);
                break;

            case "血压蓝牙":
                binding.settingMacBloodmac.setCenterString(mac);
                break;

            case "血氧蓝牙":
                binding.settingMacOxygenmac.setCenterString(mac);
                break;

        }

    }

    @Override
    public void foundDev(BluetoothDevice dev) {
//        if (dev.getName() != null) {
//            mBtDevAdapter.add(dev);
//        }
        mBtDevAdapter.add(dev);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Log.d("position",position+"");
//    }

//    @Override
//    public void onItemClick(BluetoothDevice dev) {
//        Toast.makeText(LocalConfig.SettingContext, "2次点击时间间隔大于1s" + dev.getAddress(), Toast.LENGTH_SHORT).show();
//    }
}
