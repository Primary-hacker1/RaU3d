package com.rick.recoveryapp.chart;

import android.database.Cursor;
import android.util.Log;

import com.rick.recoveryapp.greendao.ActivitRecordDao;
import com.rick.recoveryapp.greendao.PowerAVGDao;
import com.rick.recoveryapp.greendao.RecordDetailedDao;
import com.rick.recoveryapp.greendao.entity.ActivitRecord;
import com.rick.recoveryapp.greendao.entity.PowerAVG;
import com.rick.recoveryapp.greendao.entity.RecordDetailed;
import com.rick.recoveryapp.utils.LocalConfig;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class MyAVG {

    RecordDetailedDao recordDetailedDao;
    PowerAVGDao powerAVGDao;
    PowerAVG powerAVG;
    List<RecordDetailed> RecordList;

    public void GetAvg(String RecordID) {

        recordDetailedDao = LocalConfig.daoSession.getRecordDetailedDao();
        powerAVGDao = LocalConfig.daoSession.getPowerAVGDao();

        RecordList = recordDetailedDao.queryBuilder().where(RecordDetailedDao.Properties.RecordID.eq(RecordID)).list();
        int count = RecordList.size();
        if (count > 0) {
            if (count > 10) {
                //大于10，减少10倍
                double ble = count / 10;
                //结果取整数部分
                int val = (int) Math.floor(ble);
                int fla = 0;

                for (int i = 0; i < 10; i++) {
                    long begin = RecordList.get(fla).getId();
                    long endval = begin + val;

                    if (i == 9) {
                        endval = count;
                    }

                    String sql = "select " +
                            "AVG(LEFT_LIMB)," +
                            "AVG(RIGHT_LIMB) " +
                            "FROM " +
                            "RECORD_DETAILED " +
                            "WHERE " +
                            "RECORD_ID = '" + RecordID + "' " +
                            "AND _id>='" + begin + "' " +
                            "AND _id<'" + endval + "' " +
                            "GROUP BY " +
                            "RECORD_ID;";

                    String leftAvg = "";
                    String rightAvg = "";
                    Cursor c = recordDetailedDao.getDatabase().rawQuery(sql, null);
                    if (c.moveToFirst()) {
                        leftAvg = String.format("%.2f", c.getDouble(c.getColumnIndex("AVG(LEFT_LIMB)")));
                        rightAvg = String.format("%.2f", c.getDouble(c.getColumnIndex("AVG(RIGHT_LIMB)")));
                        powerAVG = new PowerAVG();
                        powerAVG.setRecordID(RecordID);
                        powerAVG.setLeftAvg(leftAvg);
                        powerAVG.setRightAvg(rightAvg);
                        powerAVGDao.insert(powerAVG);
                    }
                    c.close();
                    fla = fla + val;
                }

            } else {
                try {
                    for (int i = 0; i < count; i++) {
                        powerAVG = new PowerAVG();
                        powerAVG.setRecordID(RecordID);
                        powerAVG.setLeftAvg(RecordList.get(i).getLeftLimb() + "");
                        powerAVG.setRightAvg(RecordList.get(i).getRightLimb() + "");
                        powerAVGDao.insert(powerAVG);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        } else {
            return;
        }
    }
}
