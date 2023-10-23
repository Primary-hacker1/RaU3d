package com.rick.recoveryapp.bluetooth;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.rick.recoveryapp.base.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class KeepLifeService  extends Service {

    private static final String TAG="KeepLifeService";

    private String mPackName;
    private ActivityManager mActivityManager;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        mActivityManager =(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        String process=getProcessName();
        mPackName =getPackageName();

        boolean isRun=isRunningProcess(mActivityManager,mPackName);
      //  Log.i(TAG, String.format("onCreate: %s %s pid=%d uid=%d isRun=%s", mPackName,process, Process.myPid(), Process.myUid(),isRun));

        if(!isRun){
            if (BaseApplication.mConnectService != null)
            BaseApplication.mConnectService.stop();
            BaseApplication.mBluetoothAdapter.enable();
//            Intent intent = getPackageManager().getLaunchIntentForPackage(mPackName);
//            if(intent!=null){
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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


    /**
     * 进程是否存活
     * @return
     */
    public static boolean isRunningProcess(ActivityManager manager,String processName) {
        if(manager==null)
            return false;
        List<ActivityManager.RunningAppProcessInfo> runnings = manager.getRunningAppProcesses();
        if (runnings != null) {
            for (ActivityManager.RunningAppProcessInfo info : runnings) {
                if(TextUtils.equals(info.processName,processName)){
                    return true;
                }
            }
        }
        return false;
    }


}
