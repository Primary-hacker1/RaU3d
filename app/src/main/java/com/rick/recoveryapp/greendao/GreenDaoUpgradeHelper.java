package com.rick.recoveryapp.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

public class GreenDaoUpgradeHelper extends DaoMaster.OpenHelper {
    public GreenDaoUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    //这里重写onUpgrade方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, true);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, true);
            }
            //注意此处的参数StudentDao.class，很重要（一开始没注意，给坑了一下），它就是需要升级的table的Dao,
            //不填的话数据丢失，
            // 这里可以放多个Dao.class，也就是可以做到很多table的安全升级
              }, ActivitRecordDao.class);
      //  });

    }

}
