package com.rick.recoveryapp.base;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.rick.recoveryapp.utils.LocalConfig;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

     Timer timer1;
     TimerTask timerTask1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!BaseApplication.mBluetoothAdapter.isEnabled()) {
            //直接开启蓝牙
            BaseApplication.mBluetoothAdapter.enable();
        } //否则创建蓝牙连接服务对象
        else if (BaseApplication.mConnectService == null) {
            //  BaseApplication.mConnectService = new BluetoothService(BaseApplication.mHandler);
            BaseApplication.AutoConnect();
        }
        ControlTask();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer1!=null||timerTask1!=null){
            timer1.cancel();
            timer1.purge();
            timerTask1.cancel();
            timer1 = null;
            timerTask1 = null;
        }
    }

    public void ControlTask() {

        timer1 = new Timer();
        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                if (LocalConfig.isControl) {
                    if (timer1 != null || timerTask1 != null) {
                        timer1.cancel();
                        timer1.purge();
                        timerTask1.cancel();
                        timer1 = null;
                        timerTask1 = null;
                        return;
                    }

                }else{
                    BaseApplication.AutoConnect();
                }
            }
            };
            timer1.schedule(timerTask1,10000,10000);
        }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
