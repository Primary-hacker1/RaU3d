package com.rick.recoveryapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ListDataSave {

    //    private static SharedPreferences preferences;
//    private static SharedPreferences.Editor editor;
    /**
     * 保存在手机里的SP文件名
     */
    public static final String FILE_NAME = "my_sp";
//    public ListDataSave(Context mContext, String preferenceName) {
//        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
//        editor = preferences.edit();
//    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public static <T> void setDataList(Context context, String tag, List<T> datalist) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.apply();

    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public static <T> List<T> getDataList(Context context, String tag) {
        List<T> datalist = new ArrayList<T>();
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;

    }


}
