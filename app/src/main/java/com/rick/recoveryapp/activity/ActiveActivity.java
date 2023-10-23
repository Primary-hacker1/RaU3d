package com.rick.recoveryapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.bluetooth.BluetoothChatService;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.chart.MyAVG;
import com.rick.recoveryapp.databinding.ActivityActiviteBinding;
import com.rick.recoveryapp.databinding.ActivityU3ddataBinding;
import com.rick.recoveryapp.databinding.FragemtActiveBinding;
import com.rick.recoveryapp.entity.EcgData;
import com.rick.recoveryapp.entity.LiveMessage;
import com.rick.recoveryapp.entity.protocol.PoolMessage;
import com.rick.recoveryapp.entity.protocol.UploadData;
import com.rick.recoveryapp.greendao.ActivitRecordDao;
import com.rick.recoveryapp.greendao.EcgDataDBDao;
import com.rick.recoveryapp.greendao.RecordDetailedDao;
import com.rick.recoveryapp.greendao.entity.ActivitRecord;
import com.rick.recoveryapp.greendao.entity.RecordDetailed;
import com.rick.recoveryapp.utils.CRC16Util;
import com.rick.recoveryapp.utils.ContentPage;
import com.rick.recoveryapp.utils.LocalConfig;
import com.rick.recoveryapp.utils.MyTimeUtils;
import com.rick.recoveryapp.utils.TimeCountTool;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.tabbar.TabSegment;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActiveActivity extends XPageActivity {

    int resiDta = 1, modletype = 0;
    ArrayList<Float> EcgListData;
    static ArrayList<Float> OftenListData;
    RecordDetailedDao recordDetailedDao;
    EcgDataDBDao ecgDataDBDao;
    UploadData uploadData;
    EcgData ecgData;
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    String ContorlState = "52";
    BtDataPro btDataPro;
    String CMD_CODE = "";
    private Timer timer1;
    private TimerTask timerTask1;
    boolean isBegin = false;
    Context context;
    ActivityActiviteBinding binding;
    ActivitRecordDao activitRecordDao;
    public TimeCountTool timeCountTool = TimeCountTool.getInstance();
    String timecount = "";
    double Total_mileage, Calories;
    String Active_B_Diastole_Shrink = "0/0", Active_L_Diastole_Shrink = "0/0";
    private CountDownButtonHelper mCountDownHelper1;
    int BloodEndState = 0; // 0:初始状态  1：需要测量血压   2：血压测量完成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityActiviteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.translucent(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;

        btDataPro = new BtDataPro();
        itinClick();
        binding.activeTxtMassage.setLeftString(" 患者姓名：" + LocalConfig.userName);
        binding.activeTxtMassage.setLeftBottomString(" 患者编号：" + LocalConfig.medicalNumber + "");
        BaseApplication myApp = (BaseApplication) getApplication();
        LocalConfig.daoSession = myApp.getDaoSession();
        activitRecordDao = LocalConfig.daoSession.getActivitRecordDao();
        recordDetailedDao = LocalConfig.daoSession.getRecordDetailedDao();

        EcgListData = new ArrayList<>();
        OftenListData = new ArrayList<>();
        binding.activeWaveviewOne.resetCanavas();
        ecgDataDBDao = LocalConfig.daoSession.getEcgDataDBDao();

        try {
            initLiveData();
            btDataPro.sendBTMessage(GetCmdCode(1, "53", false));
            binding.activeTxtResistance.setCenterString("1");
            // btDataPro.sendBTMessage(btDataPro.GetCmdCode(LocalConfig.ecgmac, LocalConfig.bloodmac, LocalConfig.oxygenmac));
            PassEcg();

        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public void PassEcg() {
        timer1 = new Timer();
        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (OftenListData != null) {
                        if (OftenListData.size() > 0) {
                            float cooY = OftenListData.get(0);
                            binding.activeWaveviewOne.showLine(cooY);
                            OftenListData.remove(0);
                        } else {
                            //     binding.activeWaveviewOne.showLine(0f);
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    com.efs.sdk.base.core.util.Log.d("EcgError", e.getMessage());
                }
            }
        };
        timer1.schedule(timerTask1, 1, 10);
    }

    public void initLiveData() {
        LiveEventBus
                .get("BT_PROTOCOL", PoolMessage.class)
                .observe(this, new Observer<PoolMessage>() {
                    @Override
                    public void onChanged(@Nullable PoolMessage msg) {
                        if (msg.isState()) {
                            Log.d("BT", msg.getObjectName());
                            if (msg.getObjectName().equals(btDataPro.UPLODE_ANSWER)) {
                                UploadData uploadData = new UploadData();
                                uploadData = gson.fromJson(msg.getObjectJson(), UploadData.class);
                                //  uploadData.getECG(),uploadData.getBlood(),uploadData.getBlood_oxy()
                                if (uploadData.getECG().equals("已连接")) {
                                    binding.trainButEcg.setBackgroundResource(R.drawable.xindian_ok);
                                } else {
                                    binding.trainButEcg.setBackgroundResource(R.drawable.xindian_no);
                                }
                                if (uploadData.getBlood().equals("已连接")) {
                                    binding.trainButBp.setBackgroundResource(R.drawable.xueya_ok);
                                } else {
                                    binding.trainButBp.setBackgroundResource(R.drawable.xueya_no);
                                }
                                if (uploadData.getBlood_oxy().equals("已连接")) {
                                    binding.trainButO2.setBackgroundResource(R.drawable.o2_ok);
                                } else {
                                    binding.trainButO2.setBackgroundResource(R.drawable.o2_no);
                                }
                            }
                        } else {
                            Log.d("BT", "没有任何数据");
                        }
                    }
                });

        LiveEventBus
                .get("BT_CONNECTED", LiveMessage.class)
                .observe(this, new Observer<LiveMessage>() {
                    @Override
                    public void onChanged(@Nullable LiveMessage msg) {
                        if (!msg.getIsConnt()) {
                            //未连接
                            binding.trainButEcg.setBackgroundResource(R.drawable.xindian_no);
                            binding.trainButBp.setBackgroundResource(R.drawable.xueya_no);
                            binding.trainButO2.setBackgroundResource(R.drawable.o2_no);

                            binding.activeTxtBoxygen.setCenterString("0");
                            binding.activeTxtO2State.setCenterString("血氧仪未连接");
//                            binding.activeTxtHigh.setCenterString(LocalConfig.B_Diastole_Shrink);
//                            binding.activeTxtLow.setCenterString(LocalConfig.L_Diastole_Shrink);
                            binding.activeTxtBloodstate1.setCenterString("血压仪未连接");
                            binding.activeTxtBloodstate2.setCenterString("血压仪未连接");
//                            LocalConfig.B_Diastole_Shrink = "0/0";
//                            LocalConfig.L_Diastole_Shrink = "0/0";

                            binding.activeTxtCoory.setCenterString("--");
                            binding.activeTxtEcgstate.setCenterString("心电仪未连接");
                            OftenListData.clear();

                            int left = 0;
                            binding.progressViewLeft.setGraduatedEnabled(true);
                            binding.progressViewLeft.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) left, (float) 50)));
                            //  binding.progressViewLeft.startProgressAnimation();
                            binding.activeTxtLeft.setCenterString("0");

                            int right = 0;
                            binding.progressViewRight.setGraduatedEnabled(true);
                            binding.progressViewRight.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) right, (float) 50)));
                            //    binding.progressViewRight.startProgressAnimation();
                            binding.activeTxtRight.setCenterString("0");
                        }
                    }
                });

        LiveEventBus
                .get("BT_PROTOCOL", PoolMessage.class)
                .observe(this, new Observer<PoolMessage>() {
                    @Override
                    public void onChanged(@Nullable PoolMessage msg) {
                        com.efs.sdk.base.core.util.Log.d("test_BT_PROTOCOL", "ActiveFragemt");
                        if (msg.isState()) {
                            int mark = 0;
                            if (msg.getObjectName().equals(btDataPro.UPLODE_ANSWER)) {
                                mark = 1;
                            } else if (msg.getObjectName().equals(btDataPro.ECGDATA_ANSWER)) {
                                mark = 2;
                            } else if (msg.getObjectName().equals(btDataPro.CONTORL_ANSWER)) {
                                mark = 3;
                            }
                            DataDisplay(mark, msg.getObjectJson());
                            if (isBegin) {
                                UpdatProgress();
                            }
                        } else {
                            Toast.makeText(context, "数据异常", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void SaveRecord() {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sim = dateFormat.format(date);
        ActivitRecord activitRecord = new ActivitRecord();
        activitRecord.setRecordID(LocalConfig.UserID);
        activitRecord.setUserName(LocalConfig.userName);
        activitRecord.setUserNumber(LocalConfig.medicalNumber);
        activitRecord.setRecordTime(sim);
        activitRecord.setLongTime(timecount);
        activitRecord.setAduration("0");
        activitRecord.setPduration("0");
        activitRecord.setActivtType(LocalConfig.ModType + "");
        activitRecord.setB_Diastole_Shrink(Active_B_Diastole_Shrink);
        activitRecord.setL_Diastole_Shrink(Active_L_Diastole_Shrink);
        //使用String.format()格式化(四舍五入)
        activitRecord.setTotal_mileage(String.format("%.2f", Total_mileage));
        activitRecord.setCalories(String.format("%.2f", Calories));
        activitRecord.setSpasmCount(0 + "");
        activitRecordDao.insert(activitRecord);

        MyAVG myAVG = new MyAVG();
        myAVG.GetAvg(LocalConfig.UserID + "");

        Active_B_Diastole_Shrink = "0/0";
        Active_L_Diastole_Shrink = "0/0";
        timeCountTool.setTime(0);
    }

    //计算总里程,卡路里
    public void getCalories_mileage() {
        double time = timeCountTool.GetSecond();
        double speed = 0;
        double average_speed;
        double resistance = 0;
        double resistanceVal = 0;
        /**
         * 查询转速不为“0” 的所有记录集合。集合不为大于0时取平均值
         * */
        List<RecordDetailed> recordList = recordDetailedDao.queryBuilder().where(
                        RecordDetailedDao.Properties.Speed.notEq("0"),
                        RecordDetailedDao.Properties.RecordID.eq(LocalConfig.UserID))
                .list();
        if (recordList.size() > 0) {
            for (int i = 0; i < recordList.size(); i++) {
                speed = recordList.get(i).getSpeed() + speed;
            }
            //平均转速
            average_speed = speed / recordList.size();
        } else {
            average_speed = 0;
        }

        double perimeter = (float) (3.14 * 0.102 * 2);
        Total_mileage = average_speed * time * perimeter;//总里程
        /*********************************************/
        List<RecordDetailed> DetailedList = recordDetailedDao.queryBuilder().where(
                        RecordDetailedDao.Properties.RecordID.eq(LocalConfig.UserID),
                        RecordDetailedDao.Properties.Resistance.notEq(0))
                .list();

        if (DetailedList.size() > 0) {
            for (int i = 0; i < DetailedList.size(); i++) {
                resistance = resistance + DetailedList.get(i).getResistance();
            }
            //平均阻力
            int Avg = (int) Math.ceil(resistance / DetailedList.size());
//            String.format("%.2f", resistance);
            resistanceVal = LocalConfig.Getvalue(Avg);
        }
//        double perimeter = (float) (3.14 * 0.102 * 2);

        if (time > 0) {
//            double K_index = 30d / (400d / (Math.ceil(Total_mileage) / Math.ceil(time)));//指数K
            Calories = resistanceVal * (time / 60) * average_speed;
        } else {
            Calories = 0;
        }
    }

    public void itinClick() {

        binding.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!LocalConfig.isControl) {

                } else {
                    btDataPro.sendBTMessage(btDataPro.GetCmdCode(LocalConfig.ecgmac, LocalConfig.bloodmac, LocalConfig.oxygenmac));
                }

            }
        });

        binding.trainBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BloodEndState == 1) {
                    //取消测量运动后血压
                    BloodEndState = 2;
                } else if (BloodEndState == 0) {
                    dialogs();
                }
            }
        });

        binding.activeTitlePress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modletype = 1;
                ChangeDialog();
            }
        });

        binding.activeTitleIntelligence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modletype = 2;
                ChangeDialog();
            }
        });

        binding.activeImgBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BloodEndState == 1) {
                    Toast.makeText(context, "还未测量运动后血压！", Toast.LENGTH_SHORT).show();
                    return;
                }
                HandlerMessage();
            }
        });

        binding.activeImbtnJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (BloodEndState == 1) {
                    Toast.makeText(context, "还未测量运动后血压！", Toast.LENGTH_SHORT).show();
                    return;
                }
                resiDta = resiDta + 1;
                if (resiDta <= 12) {
                    binding.progressViewResistance.setGraduatedEnabled(true);
                    //  binding.progressViewResistance.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) resiDta, (float) 12)));
                    //    binding.progressViewResistance.startProgressAnimation();
                    binding.activeTxtResistance.setCenterString(resiDta + "");
                    //   btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", false));
                } else {
                    resiDta = 12;
                    //   btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", false));
                    return;
                }
            }
        });

        binding.activeImbtnMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BloodEndState == 1) {
                    Toast.makeText(context, "还未测量运动后血压！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                resiDta = resiDta - 1;
                if (resiDta < 1) {
                    resiDta = 1;
                    //  btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", false));
                    return;
                } else {
                    binding.progressViewResistance.setGraduatedEnabled(true);
                    // binding.progressViewResistance.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) resiDta, (float) 12)));
                    //   binding.progressViewResistance.startProgressAnimation();
                    binding.activeTxtResistance.setCenterString(resiDta + "");
                    //   btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", false));
                }
            }
        });

        binding.activeImgBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，不能测量血压！", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (uploadData != null && uploadData.getBlood().equals("已连接")) {
                        if (ContorlState.equals("00") || ContorlState.equals("52")) {
                            //  btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_BEGIN);
                            //   btDataPro.sendBTMessage(GetCmdCode(resiDta, "51", false));
                        } else if (ContorlState.equals("51")) {
                            // btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_END);
                            //   btDataPro.sendBTMessage(GetCmdCode(resiDta, "52", false));
                            ContorlState = "52";
                            binding.activeTxtBlood.setCenterString("点击开始测量血压");
                        }
                    } else {
                        Toast.makeText(context, "血压仪未连接，请检查设备", Toast.LENGTH_SHORT).show();
                    }
                    //  isBlood = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//                binding.tabSegment.setOnTabClickListener(new TabSegment.OnTabClickListener() {
//
//                    @Override
//                    public void onTabClick(int index) {
//
//                        DialogLoader.getInstance().showConfirmDialog(
//                                LocalConfig.TrainContext,
//                                getString(R.string.active_change),
//                                getString(R.string.lab_yes),
//                                (dialog, which) -> {
//                                    dialog.dismiss();
//                                    timecount = timeCountTool.stopCount();
//                                    getCalories_mileage();
//                                    timeCountTool.setTime(0);
//                                    SaveRecord();
//                                    LocalConfig.ModType = index;
//                                    Date date = new Date();
//                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//                                    String sim = dateFormat.format(date);
//                                    LocalConfig.UserID = Long.valueOf(sim).longValue();
//                                    //  binding.viewPagerTrain.setCurrentItem(LocalConfig.ModType);//设置当前标签页
//                                    //  Toast.makeText(context,"确认切换",Toast.LENGTH_SHORT).show();
//                                },
//                                getString(R.string.lab_no),
//                                (dialog, which) -> {
//                                    dialog.dismiss();
//
//                                }
//                        );
//
//                    }
//                });
    }

    public void HandlerMessage() {
        try {

            String txts = binding.activeTxtBegin.getCenterString();
            if (txts.equals("开  始")) {

                String highblood = binding.activeTxtHigh.getCenterString();
                String lowblood = binding.activeTxtLow.getCenterString();
                if (highblood.equals("0") && lowblood.equals("0")) {
                    DialogLoader.getInstance().showConfirmDialog(
                            context,
                            getString(R.string.active_blood),
                            getString(R.string.lab_ok),
                            (dialog, which) -> {
                                dialog.dismiss();
                                btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", true));
                                timeCountTool.startCount();
                                binding.activeTxtBegin.setCenterString("停  止");
                                binding.activeImgBegin.setBackground(getResources().getDrawable(R.drawable.stop));
                            },
                            getString(R.string.lab_cancel),
                            (dialog, which) -> {
                                dialog.dismiss();
                                // timeTask.start();
                            }
                    );

                } else {
                    btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", true));
                    timeCountTool.startCount();
                    binding.activeTxtBegin.setCenterString("停  止");
                    binding.activeImgBegin.setBackground(getResources().getDrawable(R.drawable.stop));
                }
            } else {
//                stop();
                btDataPro.sendBTMessage(GetCmdCode(0, "50", false));
//                timecount = timeCountTool.stopCount();
//                getCalories_mileage();
//                timeCountTool.setTime(0);
//                SaveRecord();

                //   btDataPro.sendBTMessage(btDataPro.CONNECT_CLOSE);
//                Intent in = new Intent(context, DataResultsActivity.class);
//                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(in);
//                finish();
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void ChangeDialog() {
        if (isBegin) {
            Toast.makeText(context, "运动中，请勿切换模式！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (BloodEndState == 1) {
            Toast.makeText(context, "还未测量运动后血压！", Toast.LENGTH_SHORT).show();
            return;
        }

        DialogLoader.getInstance().showConfirmDialog(
                context,
                getString(R.string.active_change),
                getString(R.string.lab_yes),
                (dialog, which) -> {
                    dialog.dismiss();
//                        btDataPro.sendBTMessage(GetCmdCode(0, "50", false));
//                        stop();
//
//                        btDataPro.sendBTMessage(btDataPro.CONNECT_CLOSE);
//                        timecount = timeCountTool.stopCount();
//                        getCalories_mileage();
//                        timeCountTool.setTime(0);
//                        SaveRecord();
//
//                        Date date = new Date();
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//                        String sim = dateFormat.format(date);
//                        LocalConfig.UserID = Long.valueOf(sim).longValue();
                    if (modletype == 1) {
                        LocalConfig.ModType = 1;
                        Intent in = new Intent(context, PassiveActivity.class);
                        // in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                    } else if (modletype == 2) {
                        LocalConfig.ModType = 2;
                        Intent in = new Intent(context, IntelligenceActivity.class);
                        //  in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                    }
//
//                    if (modletype == 1) {
//                        LocalConfig.ModType = 1;
//                        Intent in = new Intent(context, PassiveActivity.class);
//                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(in);
//                        finish();
//                    } else if (modletype == 2) {
//                        LocalConfig.ModType = 2;
//                        Intent in = new Intent(context, IntelligenceActivity.class);
//                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(in);
//                        finish();
//                    }
                },
                getString(R.string.lab_no),
                (dialog, which) -> {
                    dialog.dismiss();
                }
        );
    }

    public String GetCmdCode(int zuli, String blood_measure, boolean isBegin) {
        String cmd_head = "A88408",              //包头
                sport_mode = "01",                //运动模式
                active_direction = "20",          //运动方向
                spasms_lv = "00",                 //痉挛等级
                speed_lv = "00",                  //速度设定
                time_lv = "00",                   //设定时间
                cmd_end = "ED";                   //结尾
        String zuliHex = "0" + btDataPro.decToHex(zuli);
        String avtive_status = "10";
        if (isBegin) {
            avtive_status = "11";
        }
        String splicingStr = cmd_head + sport_mode + avtive_status + active_direction + zuliHex + spasms_lv
                + speed_lv + time_lv + blood_measure;
        String CRC16 = CRC16Util.getCRC16(splicingStr);
        CMD_CODE = splicingStr + CRC16 + cmd_end;
        com.efs.sdk.base.core.util.Log.d("GetCmdCode", "ActiveFragemt,获取指令");
        return CMD_CODE;
    }

    public void DataDisplay(int mark, String ObjectJson) {

        switch (mark) {
            case 1:
                uploadData = gson.fromJson(ObjectJson, UploadData.class);
//                String stresistance = uploadData.getSTresistance();
//                 binding.activeTxtResistance.setCenterString(stresistance);
                if (uploadData.getBlood_oxy().equals("已连接")) {
                    if (uploadData.getOxy_vaulestr().equals("手指未插入")
                            || uploadData.getOxy_vaulestr().equals("探头脱落")
                            || uploadData.getOxy_vaulestr().equals("127")) {
                        binding.activeTxtBoxygen.setCenterString("--");
                        if (uploadData.getOxy_vaulestr().equals("127")) {
                            binding.activeTxtO2State.setCenterString("检测中..");
                        } else {
                            binding.activeTxtO2State.setCenterString(uploadData.getOxy_vaulestr());
                        }
                    } else {
                        binding.activeTxtBoxygen.setCenterString(uploadData.getOxy_vaulestr());
                        binding.activeTxtO2State.setCenterString("");
                    }
                } else {
                    binding.activeTxtBoxygen.setCenterString("--");
                    binding.activeTxtO2State.setCenterString(uploadData.getBlood_oxy());
                }
                if (uploadData.getBlood().equals("已连接")) {
                    if (uploadData.getHigh().equals("255") || uploadData.getLow().equals("255")) {
                        binding.activeTxtHigh.setCenterString("0");
                        binding.activeTxtLow.setCenterString("0");
                        binding.activeTxtBloodstate1.setCenterString("测量错误");
                        binding.activeTxtBloodstate2.setCenterString("测量错误");
                    } else {
                        if (BloodEndState == 1) {
                            //运动后血压
                            Active_L_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
                            if (!Active_B_Diastole_Shrink.equals(Active_L_Diastole_Shrink)) {
                                BloodEndState = 2;
                                Toast.makeText(context, "运动后血压测量已完成！", Toast.LENGTH_SHORT).show();
                            }

                        } else if (BloodEndState == 0) {
                            //运动前血压
                            Active_B_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
                        }

                        binding.activeTxtHigh.setCenterString(uploadData.getHigh());
                        binding.activeTxtLow.setCenterString(uploadData.getLow());
                        if (!uploadData.getHigh().equals("0")) {
                            LocalConfig.BloodHight = uploadData.getHigh();
                            LocalConfig.BloodLow = uploadData.getLow();
                        }
                        binding.activeTxtBloodstate1.setCenterString("");
                        binding.activeTxtBloodstate2.setCenterString("");
                    }
                } else {
                    binding.activeTxtHigh.setCenterString(LocalConfig.BloodHight);
                    binding.activeTxtLow.setCenterString(LocalConfig.BloodLow);
                    binding.activeTxtBloodstate1.setCenterString(uploadData.getBlood());
                    binding.activeTxtBloodstate2.setCenterString(uploadData.getBlood());
                    Active_B_Diastole_Shrink = "0/0";
                    Active_L_Diastole_Shrink = "0/0";
                }

                if (uploadData.getECG().equals("已连接")) {
                    binding.activeTxtEcgstate.setCenterString("");
                } else {
                    binding.activeTxtCoory.setCenterString("--");
                    binding.activeTxtEcgstate.setCenterString(uploadData.getECG());
                }
                if (isBegin) {
                    //    int speed = Integer.parseInt(uploadData.getSpeed());
                    binding.progressViewZhuansuActicve.setGraduatedEnabled(true);
//                    binding.progressViewZhuansuActicve.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) speed, (float) 60)));
//                    binding.progressViewZhuansuActicve.startProgressAnimation();
                    binding.activeTxtZhuansu.setCenterString(uploadData.getSpeed());

                    //   int left = Integer.parseInt(uploadData.getLeft());
                    binding.progressViewLeft.setGraduatedEnabled(true);
//                    binding.progressViewLeft.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) left, (float) 50)));
//                    binding.progressViewLeft.startProgressAnimation();
                    binding.activeTxtLeft.setCenterString(uploadData.getLeft());

                    // int right = Integer.parseInt(uploadData.getRight());
                    binding.progressViewRight.setGraduatedEnabled(true);
//                    binding.progressViewRight.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) right, (float) 50)));
//                    binding.progressViewRight.startProgressAnimation();
                    binding.activeTxtRight.setCenterString(uploadData.getRight());

                    //  int resiDtas = Integer.parseInt(uploadData.getSTresistance());
                    binding.progressViewResistance.setGraduatedEnabled(true);
//                    binding.progressViewResistance.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) resiDta, (float) 12)));
//                    binding.progressViewResistance.startProgressAnimation();
                    binding.activeTxtResistance.setCenterString(uploadData.getSTresistance());
                }
                if (uploadData.getActiveState().equals("停机状态")) {
                    binding.activeTxtBegin.setCenterString("开  始");
                    binding.activeImgBegin.setBackground(getResources().getDrawable(R.drawable.begin));
                    //判断是否在运动中
                    if (isBegin) {
                        isBegin = false;
                        stop();
                        timecount = timeCountTool.stopCount();
                        getCalories_mileage();

                        //下位机停止，提示是否测量血压
                        //是：进行血压测量，判断血压仪连接状态
                        //否：直接进入结算界面
                        DialogLoader.getInstance().showConfirmDialog(
                                context,
                                getString(R.string.active_blood_end),
                                getString(R.string.lab_yes),
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    BloodEndState = 1;
                                    if (uploadData != null && uploadData.getBlood().equals("已连接")) {
                                        if (ContorlState.equals("00") || ContorlState.equals("52")) {
                                            btDataPro.sendBTMessage(GetCmdCode(resiDta, "51", false));
                                        } else if (ContorlState.equals("51")) {
                                            btDataPro.sendBTMessage(GetCmdCode(resiDta, "52", false));
                                            ContorlState = "52";
                                            binding.activeTxtBlood.setCenterString("点击开始测量血压");
                                        }
                                    } else {
                                        Toast.makeText(context, "血压仪未连接，请检查设备", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                getString(R.string.lab_no),
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    BloodEndState = 2;
                                }
                        );
//                        Intent in = new Intent(context, DataResultsActivity.class);
//                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(in);
//                        finish();
                    }

                } else if (uploadData.getActiveState().equals("运行状态")) {
                    isBegin = true;
                    timeCountTool.startCount();
                    binding.activeTxtBegin.setCenterString("停  止");
                    binding.activeImgBegin.setBackground(getResources().getDrawable(R.drawable.stop));
                }

                if (BloodEndState == 2) {
                    BloodEndState = 0;
                    SaveRecord();
                    btDataPro.sendBTMessage(btDataPro.CONNECT_CLOSE);

                    Intent in = new Intent(context, DataResultsActivity.class);
                    // in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();
                }
                break;

            case 2:
                try {
                    ecgData = gson.fromJson(ObjectJson, EcgData.class);
                    binding.activeTxtCoory.setCenterString(ecgData.getHeartrate());
                    EcgListData = ecgData.getEcgCoorY();
                    if (EcgListData == null) {
                        binding.activeTxtEcgstate.setCenterString("心电仪佩戴异常！");
                        OftenListData = new ArrayList<>();
                        //   binding.activeWaveviewOne.showLine(0f);
                        return;
                    } else {
                        // Float
                        binding.activeTxtEcgstate.setCenterString("");
                        for (int i = 0; i < EcgListData.size(); i++) {
                            Float cooY = EcgListData.get(i);
                            OftenListData.add(cooY);
                        }
                        EcgListData = null;
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                ContorlState = ObjectJson;
                if (ContorlState.equals("51")) {
                    //   binding.activeTxtBlood.setCenterString("测量中");
                } else if (ContorlState.equals("52")) {
                    //     binding.activeTxtBlood.setCenterString("点击开始测量血压");
                } else {
                    //  binding.activeTxtBlood.setCenterString("点击开始测量血压");
                }
                break;
        }
    }

    /**
     * 模拟源源不断的数据源
     */
    public void UpdatProgress() {
        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sim = dateFormat.format(date);

            RecordDetailed recordDetailed = new RecordDetailed();
            recordDetailed.setRecordID(LocalConfig.UserID);
            recordDetailed.setActivtType("主动模式");
            recordDetailed.setRecordTime(sim);
            int zhuansu = Integer.parseInt(binding.activeTxtZhuansu.getCenterString());
            recordDetailed.setSpeed(zhuansu);
            int leftlimb = Integer.parseInt(binding.activeTxtLeft.getCenterString());
            recordDetailed.setLeftLimb(leftlimb);
            int rightlimb = Integer.parseInt(binding.activeTxtRight.getCenterString());
            recordDetailed.setRightLimb(rightlimb);
            int resistance = Integer.parseInt(binding.activeTxtResistance.getCenterString());
            recordDetailed.setResistance(resistance);

            int heartRate;
            if (binding.activeTxtCoory.getCenterString().equals("--")) {
                heartRate = 0;
            } else {
                heartRate = Integer.parseInt(binding.activeTxtCoory.getCenterString());
            }
            recordDetailed.setHeartRate(heartRate);

            int Hbo2;
            if (binding.activeTxtBoxygen.getCenterString().equals("--")) {
                Hbo2 = 0;
            } else {
                Hbo2 = Integer.parseInt(binding.activeTxtBoxygen.getCenterString());
            }
            recordDetailed.setHbo2(Hbo2);

            recordDetailed.setSpasm(0);
            recordDetailedDao.insert(recordDetailed);
            //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔

        } catch (Exception e) {
            Toast.makeText(context, "数据库异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void stop() {
        OftenListData = new ArrayList<>();

        if (timer1 != null) {
            timer1.cancel();
            timer1.purge();
            timer1 = null;
        }
        if (timerTask1 != null) {
            timerTask1.cancel();
            timerTask1 = null;
        }
        binding.activeWaveviewOne.resetCanavas();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dialogs();
            return false;
        }
        return true;
    }

    public void dialogs() {
        //  timeTask.interrupt();
        DialogLoader.getInstance().showConfirmDialog(
                context,
                getString(R.string.active_return),
                getString(R.string.lab_yes),
                (dialog, which) -> {
                    dialog.dismiss();
                    if (isBegin) {
                        btDataPro.sendBTMessage(GetCmdCode(0, "50", false));
                    } else {
                        Intent in = new Intent(context, AdminMainActivity.class);
                        // in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                    }
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
        stop();
        LocalConfig.BloodHight = "0";
        LocalConfig.BloodLow = "0";
        TimeCountTool.setClean();
    }
}
