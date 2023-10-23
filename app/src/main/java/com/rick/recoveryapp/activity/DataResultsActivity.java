package com.rick.recoveryapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.github.mikephil.charting.data.Entry;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.activity.u3d.U3DActivity;
import com.rick.recoveryapp.activity.u3d.U3DFactory;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.chart.ChartStyle;
import com.rick.recoveryapp.databinding.ActivityDataresultsBinding;
import com.rick.recoveryapp.greendao.ActivitRecordDao;
import com.rick.recoveryapp.greendao.PowerAVGDao;
import com.rick.recoveryapp.greendao.RecordDetailedDao;
import com.rick.recoveryapp.greendao.entity.ActivitRecord;
import com.rick.recoveryapp.greendao.entity.PowerAVG;
import com.rick.recoveryapp.greendao.entity.RecordDetailed;
import com.rick.recoveryapp.utils.LocalConfig;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataResultsActivity extends XPageActivity {

    Context context;
    ActivityDataresultsBinding binding;
    ActivitRecordDao activitRecordDao;
    RecordDetailedDao recordDetailedDao;
    PowerAVGDao powerAVGDao;
    List<ActivitRecord> RecordList;
    List<RecordDetailed> DetailedList;
    String RecordID;
    BtDataPro btDataPro;
    double resistance = 0, speed = 0, spasm = 0;
    ActivitRecord activitRecord;
    ChartStyle chartStyle;
    ArrayList<Entry> left = new ArrayList<Entry>();
    ArrayList<Entry> right = new ArrayList<Entry>();
    ArrayList<String> ReTime = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityDataresultsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            StatusBarUtils.translucent(this);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;
            SuperMain();

        } catch (Exception ex) {
            Log.d("DataResultsActivity", ex.getMessage());
        }
    }

    public void SuperMain() {
        btDataPro = new BtDataPro();
        chartStyle = new ChartStyle(binding.chart1);
        initClick();
        Intent integer = getIntent();
        //20230410135957
        RecordID = integer.getStringExtra("RecordID");
        if (RecordID == null) {
            RecordID = LocalConfig.UserID + "";
            // RecordID="20230410135957";
            // RecordID = "1";
        }

        getDetailed();
        activitRecordDao = LocalConfig.daoSession.getActivitRecordDao();
        RecordList = activitRecordDao.queryBuilder().where(ActivitRecordDao.Properties.ID.eq(RecordID)).list();
        if (RecordList.size() > 0) {
            activitRecord = new ActivitRecord();
            activitRecord.setActivtType(RecordList.get(0).getActivtType());
            activitRecord.setRecordID(RecordList.get(0).getID());
            activitRecord.setTotal_mileage(RecordList.get(0).getTotal_mileage());
            activitRecord.setRecordTime(RecordList.get(0).getRecordTime());
            activitRecord.setCalories(RecordList.get(0).getCalories());
            activitRecord.setB_Diastole_Shrink(RecordList.get(0).getB_Diastole_Shrink());
            activitRecord.setL_Diastole_Shrink(RecordList.get(0).getL_Diastole_Shrink());
            activitRecord.setLongTime(RecordList.get(0).getLongTime());
            activitRecord.setAduration(RecordList.get(0).getAduration());
            activitRecord.setPduration(RecordList.get(0).getPduration());
            activitRecord.setUserNumber(RecordList.get(0).getUserNumber());
            activitRecord.setUserName(RecordList.get(0).getUserName());
            activitRecord.setRemark(RecordList.get(0).getRemark());
            activitRecord.setSpasmCount(RecordList.get(0).getSpasmCount());
            initView(activitRecord);
        }

        //   20221102151754
        chartStyle.initChartStyle();
        powerAVGDao = LocalConfig.daoSession.getPowerAVGDao();
        List<PowerAVG> avgList = powerAVGDao.queryBuilder().where(PowerAVGDao.Properties.RecordID.eq(RecordID)).list();
        if (avgList.size() > 0) {
            for (int i = 0; i < avgList.size(); i++) {
                left.add(new Entry(i, Float.parseFloat(avgList.get(i).getLeftAvg() + "")));
                right.add(new Entry(i, Float.parseFloat(avgList.get(i).getRightAvg() + "")));
                ReTime.add(i + "");
            }
        }
        chartStyle.setData(left, right, ReTime);

    }

    public void getDetailed() {
        recordDetailedDao = LocalConfig.daoSession.getRecordDetailedDao();
        /*********************************************/
        DetailedList = recordDetailedDao.queryBuilder().where(RecordDetailedDao.Properties.RecordID.eq(RecordID),
                        RecordDetailedDao.Properties.Resistance.notEq(0)
                ).orderDesc(RecordDetailedDao.Properties.RecordTime)
                .list();

        if (DetailedList.size() > 0) {
            resistance = DetailedList.get(0).getResistance();
            //平均阻力

            //  resistance = resistance / DetailedList.size();
            //   activitRecord.setTotal_mileage(String.format("%.2f", resistance));
        }
        binding.dataTxtResistance.setCenterString(resistance + "");

        DetailedList = null;
        /*********************************************/
        DetailedList = recordDetailedDao.queryBuilder().where(RecordDetailedDao.Properties.RecordID.eq(RecordID),
                RecordDetailedDao.Properties.Speed.notEq(0)
        ).orderDesc(RecordDetailedDao.Properties.RecordTime).list();
        if (DetailedList.size() > 0) {

            speed = DetailedList.get(0).getSpeed();
            //平均转速
            // speed = speed / DetailedList.size();
        }
        binding.dataTxtSpeed.setCenterString(speed + "");

        DetailedList = null;
        /*********************************************/
        DetailedList = recordDetailedDao.queryBuilder().where(RecordDetailedDao.Properties.RecordID.eq(RecordID),
                RecordDetailedDao.Properties.Spasm.notEq(0)
        ).orderDesc(RecordDetailedDao.Properties.RecordTime).list();

        if (DetailedList.size() > 0) {
            spasm = DetailedList.get(0).getSpasm();

            //平均痉挛强度
            //  spasm = spasm / DetailedList.size();
        }
        //   binding.dataTxtSpasm.setCenterString((String.format("%.2f", Math.ceil(spasm))));
        binding.dataTxtSpasm.setCenterString(spasm + "");

        DetailedList = null;
        /*********************************************/
        DetailedList = recordDetailedDao.queryBuilder().where(RecordDetailedDao.Properties.RecordID.eq(RecordID),
                RecordDetailedDao.Properties.Hbo2.notEq(0)).orderAsc(RecordDetailedDao.Properties.Hbo2).list();
        if (DetailedList.size() > 0) {
            //血氧高低
            binding.dataTxtO2.setCenterString("    " + DetailedList.get(0).getHbo2());
            binding.dataTxtO2.setCenterBottomString("    " + DetailedList.get(DetailedList.size() - 1).getHbo2() + "");
        } else {
            binding.dataTxtO2.setCenterString("    0");
            binding.dataTxtO2.setCenterBottomString("    0");
        }

        DetailedList = null;
        /*********************************************/
        DetailedList = recordDetailedDao.queryBuilder().where(RecordDetailedDao.Properties.RecordID.eq(RecordID),
                RecordDetailedDao.Properties.HeartRate.notEq(0)).orderAsc(RecordDetailedDao.Properties.HeartRate).list();
        if (DetailedList.size() > 0) {
            //血氧高低
            binding.dataTxtHeartRate.setCenterString(DetailedList.get(0).getHeartRate() + "");
            binding.dataTxtHeartRate.setCenterBottomString(DetailedList.get(DetailedList.size() - 1).getHeartRate() + "");
        } else {
            binding.dataTxtHeartRate.setCenterString("0");
            binding.dataTxtHeartRate.setCenterBottomString("0");
        }
    }

    public void initView(ActivitRecord activitRecord) {
        binding.logger.setContentText(activitRecord.getRemark());
        binding.dataTxtName.setCenterString(activitRecord.getUserName());
        binding.dataTxtNumber.setCenterTopString("编号:" + activitRecord.getID());
        binding.dataTxtNumber.setCenterString("病历号：" + activitRecord.getUserNumber());
        binding.dataTxtResultstime.setCenterString(activitRecord.getRecordTime());
        binding.dataTxtHowtime.setCenterString(activitRecord.getLongTime());
        binding.dataTxtAduration.setCenterString(activitRecord.getAduration());
        binding.dataTxtPduration.setCenterString(activitRecord.getPduration());
        binding.dataTxtMileage.setCenterString(activitRecord.getTotal_mileage());
        binding.dataTxtBlooddata.setCenterString(activitRecord.getB_Diastole_Shrink());
        binding.dataTxtBlooddata.setCenterBottomString(activitRecord.getL_Diastole_Shrink());
        binding.dataTxtCalories.setCenterString(activitRecord.getCalories());
        binding.dataTxtSpasmcount.setCenterString(activitRecord.getSpasmCount());

        LocalConfig.userName = activitRecord.getUserName();
        LocalConfig.medicalNumber = activitRecord.getUserNumber();

        switch (activitRecord.getActivtType()) {

            case "0":
                binding.dataTxtActiviteType.setCenterString("主动模式");
                binding.dataTxtAduration.setVisibility(View.INVISIBLE);
                binding.dataTxtPduration.setVisibility(View.INVISIBLE);
                binding.dataTxtSpasmcount.setVisibility(View.GONE);
                binding.dataTxtCount.setVisibility(View.GONE);
                binding.dataTxtSpasm.setVisibility(View.GONE);
                binding.dataTxtJingluan.setVisibility(View.GONE);
                break;

            case "1":
                binding.dataTxtActiviteType.setCenterString("被动模式");
                binding.dataTxtResistance.setVisibility(View.GONE);
                binding.dataTxtZuli.setVisibility(View.GONE);
                binding.dataTxtAduration.setVisibility(View.INVISIBLE);
                binding.dataTxtPduration.setVisibility(View.INVISIBLE);
                binding.dataLayoutChart.setVisibility(View.INVISIBLE);
                break;

            case "2":
                binding.dataTxtActiviteType.setCenterString("智能模式");
                break;

            case "3":
                binding.dataTxtActiviteType.setCenterString("情景模式");
                break;
        }
    }

    public void initClick() {

        binding.dataBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (LocalConfig.Model.equals("U3D")) {
                        btDataPro.sendBTMessage(btDataPro.CONNECT_CLOSE);
                        finish();
                    } else {
                        String remarks = binding.logger.getContentText();
                        if (remarks.equals("")) {
                            dialogLoader();
                        } else {
                            activitRecord.setRemark(remarks);
                            activitRecordDao.update(activitRecord);

                            Intent in = new Intent(context, AdminMainActivity.class);
                            // in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(in);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    Log.d("Data_db", e.getMessage());
                }
            }
        });

        binding.dataBtnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (LocalConfig.Model.equals("U3D")) {
                    finish();
                } else {


                    Intent in = new Intent(context, AdminMainActivity.class);
                    //   in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();
                }
            }
        });
    }

    public void dialogLoader() {
        DialogLoader.getInstance().showConfirmDialog(
                context,
                getString(R.string.data_return),
                getString(R.string.lab_yes),
                (dialog, which) -> {
                    dialog.dismiss();
                    Intent in = new Intent(context, AdminMainActivity.class);
                    //  in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();
                },
                getString(R.string.lab_no),
                (dialog, which) -> {
                    dialog.dismiss();
                }
        );

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalConfig.Model = "";
        LocalConfig.UserID = 0;
    }
}
