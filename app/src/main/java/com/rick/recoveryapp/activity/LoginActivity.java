/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.just.agentweb.action.ActionActivity.REQUEST_CODE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.rick.recoveryapp.R;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.base.MyService;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.databinding.ActivityLoginBinding;
import com.rick.recoveryapp.entity.EcgData;
import com.rick.recoveryapp.greendao.ActivitRecordDao;
import com.rick.recoveryapp.greendao.MacDrDao;
import com.rick.recoveryapp.greendao.entity.ActivitRecord;
import com.rick.recoveryapp.greendao.entity.EcgDataDB;
import com.rick.recoveryapp.greendao.EcgDataDBDao;
import com.rick.recoveryapp.greendao.entity.MacDr;
import com.rick.recoveryapp.utils.HideKeyboard;
import com.rick.recoveryapp.utils.LocalConfig;
import com.rick.recoveryapp.utils.XToastUtils;
import com.rick.recoveryapp.utils.view.WaveUtil;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;
import com.xuexiang.xutil.XUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 登录页面
 *
 * @author xuexiang
 * @since 2019-11-17 22:21
 */
public class LoginActivity extends XPageActivity {

    String user, password;
    private boolean isHideFirst = true;  // 输入框密码是否是隐藏的，默认为true
    Context context;
    ActivityLoginBinding binding;
    private List<String> mPermissionList = new ArrayList<>();
    private static final int REQUEST_COARSE_LOCATION = 0;
    // MiniLoadingDialog mMiniLoadingDialog;
    LocationManager locationManager;
    EcgDataDBDao ecgDataDao;
    MacDrDao macDrDao;
    ActivitRecordDao activitRecordDao;
    int uid = 0;
    public static LoginActivity loginActivity;
    private SharedPreferences sharedPreferences;

    // BluetoothAdapter mBluetoothAdapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = this;
            //  mMiniLoadingDialog = WidgetUtils.getMiniLoadingDialog(context);
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            loginActivity = this;
            //设置界面全屏
            StatusBarUtils.translucent(this);
            uid = getPackageUid(context, getProcessName());
            /**
             * 初始化数据库
             */
            BaseApplication myApp = (BaseApplication) getApplication();
            LocalConfig.daoSession = myApp.getDaoSession();
            ecgDataDao = LocalConfig.daoSession.getEcgDataDBDao();
            macDrDao = LocalConfig.daoSession.getMacDrDao();
            activitRecordDao = LocalConfig.daoSession.getActivitRecordDao();
            sharedPreferences = getSharedPreferences("Personal", MODE_PRIVATE);
            AgainInto();
            SetMac();
            initClick();
//            initPermission();
//
//            //  检查蓝牙开关
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//            if (adapter == null) {
//                // Toast.makeText(context, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
//                finish();
//                return;
//            } else {
//                if (!adapter.isEnabled()) {
//                    //直接开启蓝牙
//                    adapter.enable();
//                    // Toast.makeText(context, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
//                }
//            }
            openBlueTooth();

            //     initPermission();
            // checkPermisson();
        } catch (Exception e) {
            Log.d("1234567890", e.getMessage());
        }
    }

    private void AgainInto() {
        //如果获取为空就返回默认值
        boolean ck = sharedPreferences.getBoolean("ck_password", false);

        //如果是记住密码
        if (ck) {
            String name = sharedPreferences.getString("name", "");
            String password = sharedPreferences.getString("password", "");
            binding.loginEtxtUser.setText(name);
            binding.loginEtxtPassword.setText(password);
            //记住密码打上√
            binding.savePassword.setChecked(true);
        }
    }


    //    @Override
//    public void onStart() {
//        super.onStart();
//        // 查看请求打开蓝牙
//        if (!BaseApplication.mBluetoothAdapter.isEnabled()) {
////            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//            //直接开启蓝牙
//            BaseApplication.mBluetoothAdapter.enable();
//        } //否则创建蓝牙连接服务对象
//        else if (BaseApplication.mConnectService == null) {
//            BaseApplication.mConnectService = new BtService(BaseApplication.mHandler);
//            BaseApplication.AutoConnect(BaseApplication.mConnectService);
//            LiveEventBus
//                    .get("BT_CONNECTED", LiveMessage.class)
//                    .observe(this, new Observer<LiveMessage>() {
//                        @Override
//                        public void onChanged(@Nullable LiveMessage msg) {
//                            if (msg != null) {
//                                Toast.makeText(LoginActivity.this, msg.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//
    @SuppressLint("MissingPermission")
    private void openBlueTooth() {

        if (bluePermission()) {
            BaseApplication.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            return;
        }

        if (!BaseApplication.mBluetoothAdapter.isEnabled()) {// 判断是否打开蓝牙
            //弹出对话框提示用户是后打开
            //  Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(intent, SEARCH_CODE);
            // 不做提示，强行打开
            BaseApplication.mBluetoothAdapter.enable();
        }
    }

    //Android12蓝牙权限申请
    private boolean bluePermission() {
        //compileSdkVersion项目中编译SDK版本大于30申请以下权限可使用
        //Manifest.permission.BLUETOOTH_SCAN、Manifest.permission.BLUETOOTH_ADVERTISE、Manifest.permission.BLUETOOTH_CONNECT
        //若小于30可以直接使用权限对应的字符串
        if (Build.VERSION.SDK_INT > 30) {
            if (ContextCompat.checkSelfPermission(this,
                    "android.permission.BLUETOOTH_SCAN")
                    != PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,
                    "android.permission.BLUETOOTH_ADVERTISE")
                    != PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,
                    "android.permission.BLUETOOTH_CONNECT")
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        "android.permission.BLUETOOTH_SCAN",
                        "android.permission.BLUETOOTH_ADVERTISE",
                        "android.permission.BLUETOOTH_CONNECT"}, 1);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前进程名称
     *
     * @return
     */
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取已安装应用的 uid，-1 表示未安装此应用或程序异常
    public static int getPackageUid(Context context, String packageName) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                //   Logger.d(applicationInfo.uid);
                return applicationInfo.uid;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public void initClick() {
        binding.loginBtnLogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                user = binding.loginEtxtUser.getText().toString();
                password = binding.loginEtxtPassword.getText().toString();
                if (user.equals("") || password.equals("")) {
                    Toast.makeText(context, "请输入正确的登录账号或密码", Toast.LENGTH_LONG).show();
                    // XToastUtils.warning("请输入正确的登录账号或密码");
                    binding.loginEtxtUser.setText("");
                    binding.loginEtxtPassword.setText("");
                } else {
                    if (BaseApplication.mBluetoothAdapter != null) {// 判断是否打开蓝牙
                        // 不做提示，强行打开
                        if (!BaseApplication.mBluetoothAdapter.isEnabled()) {
                            BaseApplication.mBluetoothAdapter.enable();
                        }
                        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER);
                        if (!gpsEnabled) {
                            OpenGPS();
                        } else {
                            LoginJudgment();
                        }
                    } else {
                        Toast.makeText(context, "将进行设备连接，请开启平板蓝牙开关", Toast.LENGTH_LONG).show();
                        //  LoginJudgment();
                    }
                }
            }
        });

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHideFirst == true) {
                    binding.imageView.setImageResource(R.drawable.eyeb);
                    //密文
                    HideReturnsTransformationMethod method1 = HideReturnsTransformationMethod.getInstance();
                    binding.loginEtxtPassword.setTransformationMethod(method1);
                    isHideFirst = false;
                } else {
                    binding.imageView.setImageResource(R.drawable.eyeg);
                    //密文
                    TransformationMethod method = PasswordTransformationMethod.getInstance();
                    binding.loginEtxtPassword.setTransformationMethod(method);
                    isHideFirst = true;
                }
                // 光标的位置
                int index = binding.loginEtxtPassword.getText().toString().length();
                binding.loginEtxtPassword.setSelection(index);
            }
        });

        binding.loginTxtBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogLoader.getInstance().showConfirmDialog(
                        context,
                        getString(R.string.login_break),
                        getString(R.string.lab_yes),
                        (dialog, which) -> {
                            dialog.dismiss();
                            XUtil.exitApp();
                        },
                        getString(R.string.lab_no),
                        (dialog, which) -> {
                            dialog.dismiss();
                        }
                );
            }
        });

    }

    public void OpenGPS() {

        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER) != true) {
            Toast.makeText(context, "GPS未开启,请手动开启", Toast.LENGTH_SHORT).show();
            Intent callGPSSettingIntent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class EcgDataAsyn extends AsyncTask<String, Integer, float[]> {

        protected void onPreExecute() {
            super.onPreExecute();
            //mMiniLoadingDialog.show();
            //  dialog.show();
        }

        protected void onPostExecute(float[] values) {
            try {
                //   mMiniLoadingDialog.dismiss();
                LoginJudgment();
                // dialog.dismiss();
                //checkPermisson();
                // Toast.makeText(LoginActivity.this, "存储完成！", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        }

        @Override
        protected float[] doInBackground(String... params) {
            //开启事务，提高数据存储效率
//            dbManager.open();
//            dbManager.db.beginTransaction();
            try {
                String result = WaveUtil.parseJson(context, "ecgData.json");
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                //   java.lang.IllegalStateException: Not a JSON Array: null
                Gson gson = new Gson();
                for (JsonElement obj : jsonArray) {
                    EcgData ecgData = gson.fromJson(obj, EcgData.class);
                    Float coorY = ecgData.getCoorY();
                    EcgDataDB ecgDataDB = new EcgDataDB();
                    ecgDataDB.setCooY(coorY);
                    ecgDataDao.insertInTx(ecgDataDB);
                }
                List<EcgDataDB> ecgDataDBList;
                ecgDataDBList = ecgDataDao.loadAll();
                if (ecgDataDBList.size() > 0) {
                    LocalConfig.ecgDataDBList = ecgDataDBList;
                    LoginJudgment();
                }
            } catch (Exception e) {
                Log.d("out", e.getMessage());
            }
//            float[] ecgdata = new float[jsonArray.size()];
//
//            String sql = "delete from tb_EcgData";
//            // dbManager.exeSqlA(sql);
//
//            int i = 0;
//            //循环遍历获取
//            try {
//                for (JsonElement obj : jsonArray) {
//                    EcgData ecgData = gson.fromJson(obj, EcgData.class);
//                    Float coorY = ecgData.getCoorY();
//
//                    sql = "insert into tb_EcgData (ID,CoorY)values('" + i + "','" + coorY + "')";
//                    // dbManager.exeSqlA(sql);
//                    ecgdata[i] = coorY;
//                    i++;
//                }
//                //  dbManager.db.setTransactionSuccessful();
//
//            } catch (Exception ex) {
//                Log.d("tag", ex.getMessage());
//            } finally {
////                dbManager.db.endTransaction();
////                dbManager.closeDB();
//            }
            return null;
        }
    }

    // 动态申请权限
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 版本大于等于 Android12 时
            // 只包括蓝牙这部分的权限，其余的需要什么权限自己添加
            mPermissionList.add(Manifest.permission.BLUETOOTH_SCAN);
            mPermissionList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            mPermissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            // Android 版本小于 Android12 及以下版本
            mPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            mPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (mPermissionList.size() > 0) {
            //    ActivityCompat.requestPermissions(this, mPermissionList.toArray(new String[0]), 1001);
            ActivityCompat.requestPermissions(this, mPermissionList.toArray(new String[0]), 10000);

        }
    }

    public void checkPermisson() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkCallPhonePermission != PERMISSION_GRANTED) {
                //判断是否需要 向用户解释，为什么要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                    Toast.makeText(this, "动态请求权限", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                return;
            } else {
            }
        } else {
        }
    }

    //系统方法,从requestPermissions()方法回调结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //确保是我们的请求
        if (requestCode == REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                //  Toast.makeText(this, "权限被授予", Toast.LENGTH_SHORT).show();
                LocalConfig.falg = true;
//                if (!isGrantExternalRW(this)) {
//                    return;
//                }
            } else if (grantResults[0] != PERMISSION_GRANTED) {
                LocalConfig.falg = false;
                Toast.makeText(context, "权限开启失败，无法连接到蓝牙设备！", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //  用户登录接口
    public void LoginJudgment() {
        boolean isfer = sharedPreferences.getBoolean("isfer", true);
        if (isfer) {
            if (user.equals("admin") && password.equals("123456")) {
                deleteTime();
                //  GetMac();
                if (binding.savePassword.isChecked()) {
                    //把用户名和密码保存在SharedPreferences中
                    sharedPreferences.edit().putBoolean("ck_password", true).apply();
                } else {//没有勾选
                    sharedPreferences.edit().putBoolean("ck_password", false).apply();
                }
                sharedPreferences.edit().putString("name", user).apply();
                sharedPreferences.edit().putString("password", password).apply();
                //第一次进入跳转 进入设备蓝牙绑定界面
                Intent in = new Intent(context, MacDrDialog.class);
                in.putExtra("isfer", "Y");
                startActivity(in);

            } else {
                Toast.makeText(context, "请输入正确的登录账号或密码", Toast.LENGTH_SHORT).show();
                binding.loginEtxtUser.setText("");
                binding.loginEtxtPassword.setText("");
                // return;
            }
        } else {
            String Shname = sharedPreferences.getString("name", "");
            String Shpassword = sharedPreferences.getString("password", "");
            if (user.equals(Shname) && password.equals(Shpassword)) {
                deleteTime();
                //  GetMac();
                if (binding.savePassword.isChecked()) {
                    //把用户名和密码保存在SharedPreferences中
                    sharedPreferences.edit().putString("name", user).apply();
                    sharedPreferences.edit().putString("password", password).apply();
                    sharedPreferences.edit().putBoolean("ck_password", true).apply();
                } else {//没有勾选
                    sharedPreferences.edit().putBoolean("ck_password", false).apply();
                }
                //第二次进入跳转 进入主界面
                Intent in = new Intent(context, AdminMainActivity.class);
                startActivity(in);
                finish();
            } else {
                Toast.makeText(context, "请输入正确的登录账号或密码", Toast.LENGTH_SHORT).show();
                binding.loginEtxtUser.setText("");
                binding.loginEtxtPassword.setText("");
            }
        }


    }

    public void SetMac() {
        try {
            //先查询数据库是否有Mac地址记录
            List<MacDr> macDrList = macDrDao.loadAll();
            if (macDrList.size() <= 0) {
//                String bluethmac = "001B10F1EE68";
//                String ecgmac = "D208AABB37AE";
//                String bloodmac = "A4C13844160C";
//                String oxygen = "00A0503BCBAC";

//                String bluethmac = "001B10F04B5E";
//                String ecgmac = "D208AABB37AE";
//                String bloodmac = "A4C13844160C";
//                String oxygen = "00A0503BCBAC";

                // My E76B581B5164  A4C138402A4D 00A0503D0264
                String bluethmac = "001B10F1EE6B";
                String ecgmac = "E76B581B5164";
                String bloodmac = "A4C138402A4D";
                String oxygen = "00A0503D0264";
                MacDr macDr = new MacDr();
                macDr.setBlueThMac(bluethmac);
                macDr.setEcgMac(ecgmac);
                macDr.setBloodMac(bloodmac);
                macDr.setOxygenMac(oxygen);
                macDrDao.insert(macDr);
            }
        } catch (Exception ex) {
            Toast.makeText(context, "数据库错误" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetMac() {
        List<MacDr> macDrList = macDrDao.loadAll();
        if (macDrList.size() > 0) {
            for (int i = 0; i < macDrList.size(); i++) {
                LocalConfig.bluemac = macDrList.get(0).getBlueThMac();
                LocalConfig.ecgmac = macDrList.get(0).getEcgMac();
                LocalConfig.bloodmac = macDrList.get(0).getBloodMac();
                LocalConfig.oxygenmac = macDrList.get(0).getOxygenMac();
            }
        } else {
            Toast.makeText(context, "蓝牙地址获取失败！", Toast.LENGTH_SHORT).show();
        }
    }

    //自动删除2个月前的数据
    public void deleteTime() {
        try {
            //查询表中所有数据
            List<ActivitRecord> Rlist = activitRecordDao.loadAll();

            //循环比较时长
            for (ActivitRecord Alist : Rlist) {

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                Date curDate = new Date(System.currentTimeMillis());
                String crentime = formatter.format(curDate);

                long RecordID = Alist.getID();
                String RecordTime = Alist.getRecordTime();
                long difference = dateDiff(RecordTime, crentime);
                //设置删除的过期时长
                if (difference >= 7) {
                    activitRecordDao.deleteByKey(RecordID);
//                    List<RecordDetailed> recordDetailed = recordDetailedDao.queryBuilder().
//                            where(RecordDetailedDao.Properties.RecordID.eq(RecordID)).build().list();
//                    recordDetailedDao.deleteInTx(recordDetailed);
                }
            }
            //  recordDetailedDao.queryBuilder().where(RecordDetailedDao.Properties.Id.eq(user.getId())).list();

//            String sql = "select PACKINGSLIPID,UpTime from WMS_PACKING where UPstatic='Y'";
//            Cursor cursor = dbManager.exeSql(sql);
//            if (cursor != null && cursor.moveToFirst()) {
//                //已上传数据的上传时间和pn号=
//                do {
//                    String uptime = cursor.getString(cursor.getColumnIndex("UpTime"));
//                    String packid = cursor.getString(cursor.getColumnIndex("PACKINGSLIPID"));
//                    //获取当前时间
//
//                    //计算时间差
//                    long difference = dateDiff(uptime, crentime);
//                    if (difference > 60) {//删除时间差大于60天的数据
//                        dbManager.open();
//                        sql = "delete from WMS_PACKING where PACKINGSLIPID='" + packid + "'";
//                        dbManager.exeSqlA(sql);
//
//                        sql = "delete from WMS_PACKINGLINE where PACKINGSLIPID='" + packid + "'";
//                        dbManager.exeSqlA(sql);
//
//                        sql = "delete from WMS_RECORDING where PACKINGSLIPID='" + packid + "'";
//                        dbManager.exeSqlA(sql);
//                    }
//                } while (cursor.moveToNext());
//            }
//            //  }
//            cursor.close();
        } catch (Exception e) {
            //   toast.setText(e.getMessage());
            //  org.greenrobot.greendao.DaoException: com.rick.recoveryapp.greendao.ActivitRecordDao@513f3c2 (ACTIVIT_RECORD) does not have a single-column primary key
            //   android.database.sqlite.SQLiteException: no such column: T._id (code 1 SQLITE_ERROR): , while compiling: SELECT T."_id",T."USER_NAME",T."USER_NUMBER",T."RECORD_TIME" FROM "ACTIVIT_RECORD" T
            Log.d("1", e.getMessage());
        } finally {
            // dbManager.closeDB();
        }
    }

    //计算两日期的时间间隔
    public long dateDiff(String startTime, String endTime) {
        // 按照传入的格式生成一个simpledateformate对象
        //  SimpleDateFormat sd = new SimpleDateFormat(format);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long diff;
        long day = 0;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            if (day >= 1) {
                return day;
            } else {
                if (day == 0) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
//            toast.setText(e.getMessage());
//            toast.show();
        }
        return 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (HideKeyboard.isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (uid > 0) {
            boolean rstA = isAppRunning(context, getProcessName());
            boolean rstB = isProcessRunning(context, uid);
            if (rstA || rstB) {
                //指定包名的程序正在运行中
                Log.d("LoginActivity", "指定包名的程序正在运行中");
            } else {
                //指定包名的程序未在运行中
                Log.d("LoginActivity", "指定包名的程序未在运行中");
            }
        } else {
            //应用未安装
        }
        // 停止蓝牙通信连接服务
//        if (BaseApplication.mConnectService  != null)
//            BaseApplication.mConnectService .stop();
    }

    /**
     * 方法描述：判断某一应用是否正在运行
     * Created by cafeting on 2017/2/4.
     *
     * @param context     上下文
     * @param packageName 应用的包名
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某一 uid 的程序是否有正在运行的进程，即是否存活
     * Created by cafeting on 2017/2/4.
     *
     * @param context 上下文
     * @param uid     已安装应用的 uid
     * @return true 表示正在运行，false 表示没有运行
     */
    public static boolean isProcessRunning(Context context, int uid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
        if (runningServiceInfos.size() > 0) {
            for (ActivityManager.RunningServiceInfo appProcess : runningServiceInfos) {
                if (uid == appProcess.uid) {
                    return true;
                }
            }
        }
        return false;
    }

}
