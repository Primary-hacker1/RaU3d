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

package com.rick.recoveryapp.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyTimeUtils {

    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     * @Title: getCurrentSystemTime
     * @Description: 获取当前系统时间
     */
    public static Long getCurrentSystemTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * 取出当前的日期格式yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTimeStr() {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(curCalendar.getTime());
    }

    /**
     * 将毫秒转时分秒
     *
     * @param time
     * @return
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02dh%02dm%02ds", hours, minutes, seconds) : String.format("%02dm%02ds", minutes, seconds);
    }

    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;

        Long hour = ms/ hh;
        Long minute = (ms - hour * hh) / mi;
        Long second = (ms  - hour * hh - minute * mi) / ss;


        StringBuffer sb = new StringBuffer();

        if(hour>=0&&hour<=9){
            sb.append("0"+hour+":");
        }else {
            sb.append(hour+":");
        }
        if(minute >= 0&&minute<=9) {
            sb.append("0"+minute+":");
        }else{
            sb.append(minute+":");
        }
        if(second >= 0&&second<=9) {
            sb.append("0"+second);
        }else{
            sb.append(second);
        }
        return sb.toString();
    }

    public static Long Getminute(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;

//        Long hour = ms/ hh;
//        Long minute = (ms - hour * hh) / mi;
//        Long second = (ms  - hour * hh - minute * mi) / ss;

        //Long hour = ms/ hh;
        Long minute = ms / mi;
        Long second = (ms   - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();

        if(minute >= 0&&minute<=9) {
            sb.append("00:0"+minute+":");
        }else{
            sb.append("00:"+minute+":");
        }
        if(second >= 0&&second<=9) {
            sb.append("0"+second);
        }else{
            sb.append(second);
        }
        return minute;
    }

    /**
     * 将时间戳转成格式化时间
     *
     * @param milliseconds
     */
    public static String getFormatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(new Date(milliseconds));
    }

    /**
     * 将时间戳转成格式化时间
     *
     * @param milliseconds
     */
    public static String getFormatTimeYY(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(milliseconds));
    }


    /**
     * 返回文字描述的日期
     *
     * @param date
     * @return
     */
    public static String getTimeFormatText(long date) {
        if (date == 0) {
            return "";
        }
        date = date * 1000;
        long diff = System.currentTimeMillis() - date;
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    /**
     * 将时间戳转成格式化时间
     *
     * @param milliseconds
     */
    public static String getFormatTimeHHMM(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(new Date(milliseconds));
    }


    public static String getCurrentTime(long value) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss") ;
        String time = format.format(new Date(value * 1000L));
        Log.d("xxx-------->", "转换后时间: " + time );
        return time;
    }

    /**
     * 将时间戳转成格式化时间
     *
     * @param milliseconds
     */
    public static String getFormatTime24(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
        return sdf.format(new Date(milliseconds));
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 截取Date(1418784200000-0000)的固定长度
     */
    public static String spliteTime(String dateStr) {
        String sequence = dateStr.replace("/Date(", "");
        return sequence.substring(0, 13).trim();
    }

    /**
     * 获取分秒格式化字符串
     *
     * @param duration
     * @return
     */
    public static String getFormatMiniteSecString(int duration) {
        int minutes = duration % (60 * 60) / 60;//分钟时长
        int seconds = duration % 60;//秒时长
        return String.format("%d'%d''", minutes, seconds);
    }

    /**
     * 格式化时间字符串
     * <p/>
     * 显示规则大于1天,显示天.  大于1小时,显示1=小时.   大于1分钟, 显示分钟
     * 其中,大于7天以上的均显示7天前
     *
     * @param time
     * @return
     */
    public static String getFormatTimeString(long time) {

        Long currentTime =System.currentTimeMillis() / 1000;//获得当前时间
        Long diffTime = currentTime - time;//当前时间减去创建时间,得到时间差
        long diffDay = diffTime / (24 * 3600); //得到天数
        long diffHour = diffTime % (24 * 3600) / 3600; //得到小时数
        long diffMinute = diffTime % 3600 / 60; //得到分钟数

        String result = null;

        if (diffDay >= 1) {
            if (diffDay >= 7) {
                result = "7天前";
            } else {
                result = diffDay + "天前";
            }
        } else if (diffHour >= 1) {
            result = diffHour + "小时前";
        } else if (diffMinute >= 1) {
            result = diffMinute + "分钟前";
        } else {
            result = "";
        }
        return result;
    }


}
