package com.rick.recoveryapp.bluetooth;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.activity.AdminMainActivity;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.entity.LiveMessage;
import com.rick.recoveryapp.utils.LocalConfig;

import java.util.Timer;
import java.util.TimerTask;

public class BtKeepService extends Service {

    ConnectThread ct;
    Thread thread;
    private Timer timer;
    private TimerTask timerTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // controlBT();
        ConnectThread ct = new ConnectThread();
        Thread thread = new Thread(ct);
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private class ConnectThread implements Runnable {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            try {
                if (!LocalConfig.isControl) {
                    BTAutoConnect();
                } else {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void BTAutoConnect() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (LocalConfig.isControl) {
                        ConnectThread ct = new ConnectThread();
                        Thread thread = new Thread(ct);
                        thread.start();
                        return;
                    }
                    BaseApplication.AutoConnect();
                } catch (Exception e) {
                }
            }
        };
        timer.schedule(timerTask, 100, 20000);
    }

    //    public boolean BtConnectState(){
//        Intent intent=new Intent();
//        isControls=intent.getBooleanExtra("isControl",false);
////        LiveEventBus
////                .get("BT_CONNECTED", LiveMessage.class)
////                .observe(this, new Observer<LiveMessage>() {
////                    @Override
////                    public void onChanged(@Nullable LiveMessage msg) {
////                        try {
////                            msg.getIsConnt();
////                        } catch (Exception e) {
////                            Log.d("AdminMainActivity", e.getMessage());
////                        }
////                    }
////                });
//        return isControls;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thread != null || ct != null) {
            thread.interrupt();
            thread = null;
            ct = null;

        }
    }
}
