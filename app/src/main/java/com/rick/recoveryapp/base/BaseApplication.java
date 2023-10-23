package com.rick.recoveryapp.base;

import static com.unity3d.splash.services.core.properties.ClientProperties.getActivity;
import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.bluetooth.BluetoothChatService;
import com.rick.recoveryapp.bluetooth.BtReceiver;
import com.rick.recoveryapp.entity.LiveMessage;
import com.rick.recoveryapp.greendao.DaoMaster;
import com.rick.recoveryapp.greendao.DaoSession;
import com.rick.recoveryapp.greendao.GreenDaoContext;
import com.rick.recoveryapp.greendao.GreenDaoUpgradeHelper;
import com.rick.recoveryapp.greendao.MacDrDao;
import com.rick.recoveryapp.greendao.entity.MacDr;
import com.rick.recoveryapp.http.OKHttpUpdateHttpService;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.utils.LocalConfig;
import com.umeng.commonsdk.UMConfigure;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xui.XUI;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.proxy.IFileEncryptor;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/4/5.
 */

public class BaseApplication extends Application implements BtReceiver.Listener {

    private static String TAG = BaseApplication.class.getName();
    private static DaoSession daoSession;
    private static Context context;
    // 来自BluetoothChatService Handler的消息类型
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    static TextView mConversationView;
    public static BtReceiver mBtReceiver;

    // 来自BluetoothChatService Handler的关键名
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static String TagStr = "";
    // Intent请求代码
    private static final int REQUEST_CONNECT_DEVICE = 1;

    public static LiveMessage liveMessage = null;
    public static BluetoothChatService mConnectService = null;
    // 已连接设备的名字
    public static String mConnectedDeviceName = null;
    public static BluetoothAdapter mBluetoothAdapter = null;

    private static String target_device_name = null;
    static BtDataPro btDataPro;
    static ArrayList<String> FirstList = new ArrayList<>();
    static ArrayList<String> SecondList = new ArrayList<>();
    ArrayList<String> mPermissionList = new ArrayList<>();
    static StringBuffer dataStr = new StringBuffer();
    static MacDrDao macDrDao;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        // initDreenDao(new GreenDaoContext());
        btDataPro = new BtDataPro();
        initUpdate();
        setDatabase(new GreenDaoContext());

        //友盟SDK
        UMConfigure.preInit(this, "61b6ed7ae014255fcbaf7e77", "umeng");
        //添加注释
        UMConfigure.init(this, "61b6ed7ae014255fcbaf7e77", "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志

        Log.d("LiveEventBus", "DemoApplication.this: " + BaseApplication.this);
        LiveEventBus
                .config()
                .lifecycleObserverAlwaysActive(true);
        //蓝牙初始化
        //   initBt();
        // initPermission();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtReceiver = new BtReceiver(context, this);//注册蓝牙广播
        mConversationView = new TextView(context);
    }

    public static Context getContext() {
        return context;
    }

    private void initUpdate() {
        XUpdate.get()
                .debug(true)
                //默认设置只在wifi下检查版本更新
                .isWifiOnly(false)
                //默认设置使用get请求检查版本
                .isGet(true)
                //默认设置非自动模式，可根据具体使用配置
                .isAutoMode(false)
                //设置默认公共请求参数
                .param("versionCode", UpdateUtils.getVersionCode(this))
                .param("appKey", getPackageName())
                //设置版本更新出错的监听
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {
                    @Override
                    public void onFailure(UpdateError error) {
                        error.printStackTrace();
                        //对不同错误进行处理
                        if (error.getCode() != CHECK_NO_NEW_VERSION) {
                            ToastUtils.toast(error.toString());
                        }
                    }
                })
                //设置是否支持静默，默认是true
                .supportSilentInstall(false)
                //这个必须设置！实现网络请求功能。
                .setIUpdateHttpService(new OKHttpUpdateHttpService())
                .setIFileEncryptor(new IFileEncryptor() {
                    @Override
                    public String encryptFile(File file) {
                        return null;
                    }

                    @Override
                    public boolean isFileValid(String encrypt, File file) {
                        return true;
                    }
                })
                //这个必须初始化
                .init(this);

        XHttpSDK.init(this);   //初始化网络请求框架，必须首先执行
        //XHttpSDK.setBaseUrl("http://124.221.169.68:8088");
        XHttpSDK.setBaseUrl("http://124.221.169.68:5000");
    }

    /**
     * greendao数据库初始化
     */
    private void initDreenDao(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "GreenData.db");
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    /**
     * 蓝牙设备初始化
     */
    @SuppressLint("MissingPermission")
    private void initBt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            //直接开启蓝牙
            mBluetoothAdapter.enable();
        }
        if (mBluetoothAdapter == null) {
            return;
        }
    }

    /**
     * 配置greenDao
     */
    private void setDatabase(Context context) {

        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        GreenDaoUpgradeHelper mHelper = new GreenDaoUpgradeHelper(context, "RecoveryApp_DB", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
//        mDaoMaster = new DaoMaster(db);
//        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 获取 DaoSession
     *
     * @return
     */
    public DaoSession getDaoSession() {
        return daoSession;
    }

    // 该Handler从BluetoothChatService中获取信息
    private static final Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            LocalConfig.isControl = true;
                            liveMessage = new LiveMessage();
                            liveMessage.setIsConnt(true);
                            liveMessage.setMessage("已连接到 " + mConnectedDeviceName);
                            liveMessage.setState(mConnectedDeviceName);
                            //  LocalConfig.bluetoothstate=true;
                            LiveEventBus.get("BT_CONNECTED")
                                    .post(liveMessage);
//                            liveMessage = new LiveMessage();
//                            liveMessage.setIsConnt(true);
//                            liveMessage.setMessage("已连接到 " + mConnectedDeviceName);
//                            liveMessage.setState(mConnectedDeviceName);
//                            LocalConfig.bluetoothstate=true;
//                            LiveEventBus.get("BT_CONNECTED")
//                                    .post(liveMessage);
                            break;

                        case BluetoothChatService.STATE_CONNECTING:
                            liveMessage = new LiveMessage();
                            liveMessage.setIsConnt(false);
                            liveMessage.setMessage("");//正在连接。。。
                            liveMessage.setState("");
                            LiveEventBus.get("BT_CONNECTED")
                                    .post(liveMessage);
                            break;

                        case BluetoothChatService.STATE_LISTEN:
//                            liveMessage = new LiveMessage();
//                            liveMessage.setIsConnt(false);
//                            liveMessage.setState("蓝牙未连接");
////                            LiveEventBus.get("BT_CONNECTED")
////                                    .post(liveMessage);
//                            if (mConnectedDeviceName != null) {
//                                liveMessage.setMessage("与" + mConnectedDeviceName +
//                                        msg.getData().getString(TOAST));
//                            } else {
//                           //     liveMessage.setMessage("蓝牙连接失败！");
//                                liveMessage.setMessage("无法连接到设备");
//                            }
//                            if (mConnectService != null) {
//                                mConnectService.stop();
//                            }
                            mConnectedDeviceName = null;
//                            LiveEventBus.get("BT_CONNECTED")
//                                    .post(liveMessage);
                            break;

                        case BluetoothChatService.STATE_NONE:
                            //  Log.d("123456","lalalalallalala" );
                            break;
                    }
                    break;

                case MESSAGE_WRITE:

                    break;

                case MESSAGE_READ:
                    dataStr.setLength(0);
//                    byte[] readBuf = (byte[]) msg.obj;
                    //    SecondList = LocalConfig.Datalist;
                    Bundle data = msg.getData();
                    dataStr.append(data.getString("BTdata"));
                    String endStr = "";
                    String[] split = dataStr.toString().split(" ");
                    for (int i = 0; i < split.length; i++) {
                        endStr = split[i];
                        if (endStr.equals("A8")) {
                            if (i == 0 && split[i + 1].equals("82")) {
                                FirstList = new ArrayList<String>();
                                SecondList = new ArrayList<String>();
                                FirstList.add(endStr);
                            } else if (i + 1 == split.length && split[i - 1].equals("0A")) {
                                FirstList = new ArrayList<String>();
                                SecondList = new ArrayList<String>();
                                FirstList.add(endStr);
                            } else {
                                FirstList.add(endStr);
                            }
                        } else {
                            FirstList.add(endStr);
                        }
                    }
                    if (FirstList.size() > 4) {
                        //解析“数据长度”
                        int Datalength = Integer.valueOf(btDataPro.covert16to10(FirstList.get(3)));
                        //判断（包头,功能码，包序号，数据长度）+数据实体=总数据长度-数据包尾长度
                        if (FirstList.size() == Datalength + 8) {
                            //前22个数据为非心电数据，第22个开始才是心电数据
                            for (int i = 0; i < 22; i++) {
                                if (FirstList.size() > 0) {
                                    SecondList.add(FirstList.get(0));
                                    FirstList.remove(0);
                                } else {
                                    return;
                                }
                            }
                            //  Log.d("FirstList", FirstList.get(2));
                            btDataPro.Processing(SecondList, "82");//其他一般数据
                            btDataPro.Processing(FirstList, "83");//心电数据
                            //  Log.d("FirstList", SecondList.get(2) + " " + FirstList.size() + " " + SecondList.size());
                        }
                    } else {
                        //   Log.d("Erroy", split.length + " " + FirstList.get(0) + " " + FirstList.get(1) + "  " + FirstList.get(2));
                    }
                    break;

                case MESSAGE_DEVICE_NAME:
                    // 保存已连接设备的名称
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    break;

                case MESSAGE_TOAST:
                    liveMessage = new LiveMessage();
                    liveMessage.setIsConnt(false);
                    liveMessage.setState("蓝牙未连接");
//                    LiveEventBus.get("BT_CONNECTED")
//                            .post(liveMessage); liveMessage.setMessage("与" + mConnectedDeviceName +
//                                msg.getData().getString(TOAST));
                    if (mConnectedDeviceName != null) {

                    } else {
                        //     liveMessage.setMessage("蓝牙连接失败！");
                        liveMessage.setMessage(msg.getData().getString(TOAST));
                        //     liveMessage.setMessage("无法连接到设备");
                    }
                    if (mConnectService != null) {
                        mConnectService.stop();
                    }
                    LocalConfig.isControl = false;
                    mConnectedDeviceName = null;
                    LiveEventBus.get("BT_CONNECTED")
                            .post(liveMessage);
                    //  AutoConnect();
                    break;
            }
        }
    };

    public static void GetMac() {
        macDrDao = daoSession.getMacDrDao();
        List<MacDr> macDrList = macDrDao.loadAll();
        if (macDrList.size() > 0) {
            for (int i = 0; i < macDrList.size(); i++) {
                LocalConfig.bluemac = deleteCharString(macDrList.get(0).getBlueThMac());
                LocalConfig.ecgmac = macDrList.get(0).getEcgMac();
                LocalConfig.bloodmac = macDrList.get(0).getBloodMac();
                LocalConfig.oxygenmac = macDrList.get(0).getOxygenMac();
            }
        } else {
            Toast.makeText(context, "蓝牙地址获取失败！", Toast.LENGTH_SHORT).show();

        }
    }

    public static String deleteCharString(String sourceString) {
        String deleteString = "";
        for (int i = 0; i < sourceString.length(); i++) {

            if (0 < i && i < 11 && i % 2 == 1) {
                deleteString += sourceString.charAt(i) + ":";
            } else {
                deleteString += sourceString.charAt(i);
            }
//            if (sourceString.charAt(i) != chElemData) {
//                deleteString += sourceString.charAt(i);
//            }
        }
        return deleteString;
    }

    //自动连接
    @SuppressLint("MissingPermission")
    public static void AutoConnect() {
        mConnectService = new BluetoothChatService(mHandler);
        try {
            //        String address = data.getExtras().getString(DeviceListActivity.DEVICE_ADDRESS);
            //             我的    String address ="00:1B:10:F1:EE:68";


            //   String address = "00:1B:10:F1:EE:68";
            //   String address = "00:1B:10:F1:EE:88";
            GetMac();
            String address = LocalConfig.bluemac;
            // ZXJ_BL_006  00:1B:10:F1:EE:7E
            // 获取设备
            if (!address.equals("")) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                target_device_name = device.getName();
                if (target_device_name.equals(mConnectedDeviceName)) {
                    //  Toast.makeText(this, "已连接" + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    return;
                }
                // 提示正在连接设备
                //  Toast.makeText(this, "正在连接" + target_device_name, Toast.LENGTH_SHORT).show();
                // 连接设备
                mConnectService.connect(device);
            }
            Log.d("11223344", "地址获取失败！");

        } catch (Exception e) {
            //  Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            //java.lang.NullPointerException: Attempt to invoke virtual method 'void com.example.bluetooth.BluetoothService.connect(android.bluetooth.BluetoothDevice)' on a null object reference
            Log.d("11223344", e.getMessage());
        }
//java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.lang.String.equals(java.lang.Object)' on a null object reference
    }

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
            ActivityCompat.requestPermissions(getActivity(), mPermissionList.toArray(new String[0]), 1001);
        }
    }

    @Override
    public void foundDev(BluetoothDevice dev) {

    }
}
