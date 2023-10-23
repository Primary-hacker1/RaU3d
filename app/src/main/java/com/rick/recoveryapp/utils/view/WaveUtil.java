package com.rick.recoveryapp.utils.view;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Author: gl
 * @CreateDate: 2019/11/12
 * @Description: 画曲线的工具类
 */
public class WaveUtil {
    private static Timer timer;
    private static TimerTask timerTask;
    private static int  i=0;
    /**
     * 模拟源源不断的数据源
     */
    public static void  showWaveData(final WaveShowView waveShowView){
//        final Handler handler=new Handler();
//        Runnable runnable=new Runnable(){
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                //要做的事情
//                handler.postDelayed(this, 50);
//            }
//        };
//        handler.postDelayed(runnable, 50);
       timer = new Timer();
       timerTask = new TimerTask() {
           @Override
           public void run() {
//               if(i<=CoorYList.size()-1){
//                   float coorY=  CoorYList.get(i);
//                   waveShowView.showLine(coorY);//取得是-20到20间的浮点数
//                   i++;
//               }else{
//                   i=0;
//                   float coorY=  CoorYList.get(i);
//                   waveShowView.showLine(coorY);//取得是-20到20间的浮点数
//               }
               waveShowView.showLine(new Random().nextFloat()*(20f)-10f);//取得是-20到20间的浮点数
           }
       };
       //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
       timer.schedule(timerTask,500,50);
    }

    public static String parseJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 停止绘制波形
     */
    public static void stop(){
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if(null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
