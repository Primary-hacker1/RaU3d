package com.rick.recoveryapp.utils;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rick.recoveryapp.entity.protocol.PoolMessage;
import com.rick.recoveryapp.greendao.DaoSession;
import com.rick.recoveryapp.greendao.entity.EcgDataDB;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class LocalConfig {

    public static String webAddress = "192.168.2.103:5000";

    public static String bluemac;
    public static String ecgmac;
    public static String bloodmac;
    public static String oxygenmac;
    // public static  String CONTROL_CODE="A8 84 0A 01 11 20 33 48 3B 12 50 BF 43 ED";
    public static int version = 0;
    public static boolean isCheck = false;           //是否记住账号
    public static String Model = "";               //设备型号
    public static long UserID = 0;               //设备型号
    public static String str_sn = "";                  //授权码
    public static String versionName = null;
    public static String userName = null;                 //用户账号
    public static String medicalNumber = null;
    public static String sectionID = null;                  //CorpID组织机构号
    public static ArrayList<String> ArrayType = null;
    public static String BasicsDetailRID2 = null;
    public static Boolean falg = true;
    public static Context TrainContext = null;
    public static Context SettingContext = null;
    public static BluetoothGatt bluetoothGatt = null;
    public static ArrayList<Float> CoorYList = null;
    public static List<EcgDataDB> ecgDataDBList = null;
    public static ArrayList<Float> YList = null;
    public static Float[] EcgData = null;
    public static int ModType = 0;
    public static String BasicsMainID = null;
    public static String BasicsDetailPID = null;
    public static String ip = null;
    public static int sex = -1;
    public static DaoSession daoSession = null;
    public static StringBuffer DataStr = null;
    public static ArrayList<String> Datalist = new ArrayList<>();
    public static boolean isControl = false;
    public static String B_Diastole_Shrink = "0/0";
    public static String L_Diastole_Shrink = "0/0";
    public static String ActiviteType = "";
//    public  static boolean bluetoothstate=false;

    public static String BloodHight = "0";
    public static String BloodLow = "0";

    public static String spasmCount = "0";

    public static PoolMessage poolMessage = new PoolMessage();
    public static PoolMessage poolMessage1 = new PoolMessage();
    public static String FristFault = null;                      //
    public static String SecondFault = null;                       //
    public static String ThirtFault = null;                     //
    public static String FourthFault = null;                   //

    public static String Barcode = "";                      //机器编号
    public static String PDAName = null;                  //机器名称
    public static String PDASN = null;                    //PDASN序列号
    public static String PDAVersion = null;                //程序当前版本号

    public static String BasicsDetailRID = null;                    //总单号
    public static String ReasonName = null;                    //分单号

    public static String ManagementName = null;                        //数量
    public static String ManagementID = null;                 //库位S
    //修改前库位
    public static boolean IsReLogin = false;                 //是否重新登录

    // 网络连接判断
    public static boolean netWorkCheck(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    public static int Getvalue(int falg) {
        int val = 0;
        switch (falg) {
            case 1:
                val = 8;
                break;
            case 2:
                val = 10;
                break;
            case 3:
                val = 12;
                break;
            case 4:
                val = 14;
                break;
            case 5:
                val = 16;
                break;
            case 6:
                val = 18;
                break;
                case 7:
                val = 20;
                break;
            case 8:
                val = 22;
                break;
            case 9:
                val = 24;
                break;
            case 10:
                val = 26;
                break;
            case 11:
                val = 28;
                break;
            case 12:
                val = 30;
                break;
        }
        return val;
    }




    //获取百分比
    public static String GetProgress(float EndProgress, float AllProgress) {
        String Progress = null;
        try {
            // 创建一个数值格式化对象
            NumberFormat numberFormat = NumberFormat.getInstance();
            // 设置精确到小数点后2位
            numberFormat.setMaximumFractionDigits(0);
            Progress = numberFormat.format(EndProgress / AllProgress * 100);
        } catch (Exception e) {
            e.getMessage();
        }
        return Progress;
    }

}
