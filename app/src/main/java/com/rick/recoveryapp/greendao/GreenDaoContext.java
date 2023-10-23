package com.rick.recoveryapp.greendao;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.rick.recoveryapp.base.BaseApplication;

import java.io.File;
import java.io.IOException;

public class GreenDaoContext extends ContextWrapper {

    private Context mContext;

    public GreenDaoContext() {
        super(BaseApplication.getContext());
        this.mContext = BaseApplication.getContext();
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象对象
     *
     * @param name
     */
    @Override
    public File getDatabasePath(String name) {
        // 判断是否存在sd卡
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if (!sdExist) {// 如果不存在,
            Log.e("test", "SD卡不存在，请加载SD卡");
            return null;
        } else {// 如果存在
            // 获取sd卡路径
            String dbDir = mContext.getExternalFilesDir(null).getAbsolutePath() + "/DB";

            //  dbDir += "/Android";// 数据库所在目录
            String dbPath = dbDir + "/" + name;// 数据库路径
//            Log.e("test", "getDatabasePath: ------数据库路径= " + dbPath );
///storage/emulated/0/Android/laidianbao.db
            // 判断目录是否存在，不存在则创建该目录
            File dirFile = new File(dbDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
//                Log.e("test", "getDatabasePath: --------创建了文件夹" );
            }
            // 数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            // 判断文件是否存在，不存在则创建该文件
            File dbFile = new File(dbPath);
            Log.e("test", "getDatabasePath: db文件已经存在了吗？" + dbFile.exists());
            if (!dbFile.exists()) {
                try {
//                    Log.e("test", "getDatabasePath: ---------开始创建db文件" );
                    isFileCreateSuccess = dbFile.createNewFile();// 创建文件
//                    Log.e("test", "getDatabasePath: --------创建了文件，创建成功吗？" + isFileCreateSuccess);
                } catch (IOException e) {
//                    Log.e("test", "getDatabasePath: -----------创建失败  捕捉到了一个sb异常啊，是啥？" + e.toString());
                    e.printStackTrace();
                }
            } else {
                isFileCreateSuccess = true;
            }
            // 返回数据库文件对象
//            Log.e("test", "getDatabasePath: --------文件是否创建成功？" + isFileCreateSuccess);
            if (isFileCreateSuccess) {
                return dbFile;
            } else {
//                Log.e("test", "getDatabasePath: ---------name ="+name );
                return super.getDatabasePath(name);
            }
        }
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     *
     * @param name
     * @param mode
     * @param factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     * @see ContextWrapper#openOrCreateDatabase(String,
     * int,
     * SQLiteDatabase.CursorFactory,
     * DatabaseErrorHandler)
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }
}
