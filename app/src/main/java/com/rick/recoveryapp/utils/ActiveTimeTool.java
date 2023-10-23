package com.rick.recoveryapp.utils;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class ActiveTimeTool {

    private static ActiveTimeTool mInstance = new ActiveTimeTool();
    private int time;
    private Timer timer;
    private TimerTask timerTask;
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private int day = 0;

    private ActiveTimeTool() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.e("huihui", "Construct " + time);
                    time++;
                }
            };
        }
    }

    public static ActiveTimeTool getInstance() {
        if (mInstance == null) {
            mInstance = new ActiveTimeTool();
        }
        return mInstance;
    }

    /**
     * 开始计时,可以重复调用，之前的时间不会被清零
     * 登录不成功不会统计
     */
    public void startCount() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.e("huihui", "StartCount " + time);
                    time++;
                }
            };
        }
        try {
            timer.schedule(timerTask, 100, 1000);
        } catch (Exception e) {
            Log.e("huihui", "MusicPlayTimeCountTool startCount Exception  " + e.getMessage());
        }
    }

    /**
     * 计时停止并返回当前总时间
     */
    public String stopCount() {
        try {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            if (timerTask != null) {
                timerTask.cancel();
                timerTask = null;
            }
        } catch (Exception e) {
            Log.e("huihui", "MusicPlayTimeCountTool stopCount Exception  " + e.getMessage());
        }

        if (time < 60) {
            second = time;
        }
        if (time > 59) {
            minute = time / 60;
            second = time % 60;
        }
        if (minute > 59) {
            hour = minute / 60;
            minute = minute % 60;
        }
        if (hour > 23) {
            day = hour / 24;
            hour = hour % 24;
        }
        return hour + "时" + minute + "分" + second + "秒";
    }

    public String SurplusTi(int Surtime) {
        int second1 = 0;
        int minute1 = 0;
        int hour1 = 0;
        if (Surtime < 60) {
            second1 = Surtime;
        }
        if (Surtime > 59) {
            minute1 = Surtime / 60;
            second1 = Surtime % 60;
        }
        if (minute1 > 59) {
            hour1 = minute1 / 60;
            minute1 = minute1 % 60;
        }
        if (hour1 > 23) {
            day = hour1 / 24;
            hour1 = hour1 % 24;
        }
        return hour1 + "时" + minute1 + "分" + second1 + "秒";
    }

    public double GetSecond() {
        double secondFlo = Double.valueOf(time) / 60;
        return secondFlo;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public static void setClean() {

        mInstance = null;
    }


}
