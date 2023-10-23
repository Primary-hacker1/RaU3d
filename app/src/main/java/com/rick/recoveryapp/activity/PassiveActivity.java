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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.chart.MyAVG;
import com.rick.recoveryapp.databinding.ActivityActiviteBinding;
import com.rick.recoveryapp.databinding.ActivityPassiveBinding;
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
import com.rick.recoveryapp.utils.LocalConfig;
import com.rick.recoveryapp.utils.MyTimeUtils;
import com.rick.recoveryapp.utils.PeterTimeCountRefresh;
import com.rick.recoveryapp.utils.TimeCountTool;
import com.rick.recoveryapp.utils.XToastUtils;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PassiveActivity extends XPageActivity {

    int modleType = 0;
    ArrayList<Float> EcgListData;
    static ArrayList<Float> OftenListData;
    ArrayList<Integer> countList;
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
    ActivitRecordDao activitRecordDao;
    public TimeCountTool timeCountTool = TimeCountTool.getInstance();
    String timecount = "";
    double Total_mileage, Calories;
    ActivityPassiveBinding binding;

    static PeterTimeCountRefresh downTimer;
    static long nowTime = 300000;
    int zhuansu = 5, spasmData = 1;
    Long activeTime = 0L;
    boolean isOk = false;
    String Passive_B_Diastole_Shrink = "0/0", Passive_L_Diastole_Shrink = "0/0";
    int spasmCount = 0;
    int BloodEndState = 0; // 0:初始状态  1：需要测量血压   2：血压测量完成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPassiveBinding.inflate(getLayoutInflater());
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
        nowTime = 300000;
        activeTime = MyTimeUtils.Getminute(nowTime);
        String text1 = MyTimeUtils.formatTime(nowTime);
        binding.passiveTxtDowntimer.setCenterString(text1);
        EcgListData = new ArrayList<>();
        OftenListData = new ArrayList<>();
        countList = new ArrayList<>();
        binding.passiveWaveviewOne.resetCanavas();

        ecgDataDBDao = LocalConfig.daoSession.getEcgDataDBDao();

        try {
            initLiveData();

            binding.passiveWaveviewOne.resetCanavas();
            btDataPro.sendBTMessage(GetCmdCode("53", false, 5, 5, activeTime));
            binding.passiveTxtZhuansu.setCenterString(zhuansu + "");
            binding.passiveTxtSpasm.setCenterString(spasmData + "");
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
                            binding.passiveWaveviewOne.showLine(cooY);
                            OftenListData.remove(0);
                        } else {
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

                            //未连接
                            binding.passiveTxtO2State.setCenterString("血氧仪未连接");
                            binding.passiveTxtBoxygen.setCenterString("0");

                            binding.passiveTxtBloodstate1.setCenterString("血压仪未连接");
                            binding.passiveTxtBloodstate2.setCenterString("血压仪未连接");

//                            LocalConfig.B_Diastole_Shrink = "0/0";
//                            LocalConfig.L_Diastole_Shrink = "0/0";

                            binding.passiveTxtCoory.setCenterString("0");
                            binding.passiveTxtEcgstate.setCenterString("心电仪未连接");
                            OftenListData.clear();
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
        activitRecord.setB_Diastole_Shrink(Passive_B_Diastole_Shrink);
        activitRecord.setL_Diastole_Shrink(Passive_L_Diastole_Shrink);
        //使用String.format()格式化(四舍五入)
        activitRecord.setTotal_mileage(String.format("%.2f", Total_mileage));
        activitRecord.setCalories(String.format("%.2f", Calories));
        activitRecord.setSpasmCount(spasmCount + "");
        activitRecordDao.insert(activitRecord);
        MyAVG myAVG = new MyAVG();
        myAVG.GetAvg(LocalConfig.UserID + "");
        Passive_B_Diastole_Shrink = "0/0";
        Passive_L_Diastole_Shrink = "0/0";
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
//        List<RecordDetailed> DetailedList = recordDetailedDao.queryBuilder().where(
//                        RecordDetailedDao.Properties.RecordID.eq(LocalConfig.UserID),
//                        RecordDetailedDao.Properties.Resistance.notEq(0))
//                .list();
//
//        if (DetailedList.size() > 0) {
//            for (int i = 0; i < DetailedList.size(); i++) {
//                resistance = resistance + DetailedList.get(i).getResistance();
//            }
//            //平均阻力
//            int Avg = (int) Math.ceil(resistance / DetailedList.size());
////            String.format("%.2f", resistance);
//            resistanceVal = LocalConfig.Getvalue(Avg);
//        }
//        double perimeter = (float) (3.14 * 0.102 * 2);

        if (time > 0) {
//            double K_index = 30d / (400d / (Math.ceil(Total_mileage) / Math.ceil(time)));//指数K
            Calories = 5 * (time / 60) * average_speed;
        } else {
            Calories = 0;
        }
    }

    public void itinClick() {

        binding.btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDataPro.sendBTMessage(btDataPro.GetCmdCode(LocalConfig.ecgmac, LocalConfig.bloodmac, LocalConfig.oxygenmac));
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

        binding.passiveTitleActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modleType = 1;
                if (BloodEndState == 1) {
                    Toast.makeText(context, "还未测量运动后血压！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChangeDialog();
            }
        });

        binding.passiveTitleIntelligence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modleType = 2;
                if (BloodEndState == 1) {
                    Toast.makeText(context, "还未测量运动后血压！", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChangeDialog();
            }
        });

        binding.passiveImgBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BloodEndState == 1) {
                    Toast.makeText(context, "还未测量运动后血压！", Toast.LENGTH_SHORT).show();
                    return;
                }
                HandlerMessage();
            }
        });

        binding.passiveZhuansuJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (zhuansu + 1 <= 60) {
                    if (zhuansu + 1 >= 30) {
                        /**
                         * @author  matan
                         * 转速30以上皆有提示。
                         * */
//                        if (isOk) {
//                            zhuansu = zhuansu + 1;
//                            binding.progressViewZhuansuPassive.setGraduatedEnabled(true);
////                            binding.progressViewZhuansuPassive.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) zhuansu, (float) 60)));
////                            binding.progressViewZhuansuPassive.startProgressAnimation();
//                            binding.passiveTxtZhuansu.setCenterString(zhuansu + "");
//                        } else {
                            DialogLoader.getInstance().showConfirmDialog(
                                    context,
                                    getString(R.string.tip_permission),
                                    getString(R.string.lab_ok),
                                    (dialog, which) -> {
                                        dialog.dismiss();
//                                        isOk = true;
                                        zhuansu = zhuansu + 1;
                                        binding.progressViewZhuansuPassive.setGraduatedEnabled(true);
//                                        binding.progressViewZhuansuPassive.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) zhuansu, (float) 60)));
//                                        binding.progressViewZhuansuPassive.startProgressAnimation();
                                        binding.passiveTxtZhuansu.setCenterString(zhuansu + "");
                                        //  btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                                    },
                                    getString(R.string.lab_cancel),
                                    (dialog, which) -> {
                                        dialog.dismiss();
                                        isOk = false;
                                    }
                            );
//                        }
                    } else {
                        zhuansu = zhuansu + 1;
                        binding.progressViewZhuansuPassive.setGraduatedEnabled(true);
//                        binding.progressViewZhuansuPassive.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) zhuansu, (float) 60)));
//                        binding.progressViewZhuansuPassive.startProgressAnimation();
                        binding.passiveTxtZhuansu.setCenterString(zhuansu + "");
                        //  btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                    }
                } else {
                    zhuansu = 60;
                    //   btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                    return;
                }
            }
        });

        binding.passiveZhuansuJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (zhuansu <= 5){
                    Toast.makeText(context, "转速不能小于5！", Toast.LENGTH_SHORT).show();
                    return;
                }
                zhuansu = zhuansu - 1;
                if (zhuansu < 0) {
                    zhuansu = 0;
                    //  btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                    return;
                } else {
                    binding.progressViewZhuansuPassive.setGraduatedEnabled(true);
//                    binding.progressViewZhuansuPassive.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) zhuansu, (float) 60)));
//                    binding.progressViewZhuansuPassive.startProgressAnimation();
                    binding.passiveTxtZhuansu.setCenterString(zhuansu + "");
                    //   btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                }
            }
        });

        binding.passiveSpasmJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                spasmData = spasmData + 1;
                if (spasmData <= 12) {
                    binding.progressViewSpasm.setGraduatedEnabled(true);
//                    binding.progressViewSpasm.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) spasmData, (float) 12)));
//                    binding.progressViewSpasm.startProgressAnimation();
                    binding.passiveTxtSpasm.setCenterString(spasmData + "");
                    //    btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                } else {
                    spasmData = 12;
                    //    btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                    return;
                }
            }
        });

        binding.passiveSpasmJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                spasmData = spasmData - 1;
                if (spasmData < 1) {
                    spasmData = 1;
                    //   btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                    return;
                } else {
                    binding.progressViewSpasm.setGraduatedEnabled(true);
//                    binding.progressViewSpasm.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) spasmData, (float) 12)));
//                    binding.progressViewSpasm.startProgressAnimation();
                    binding.passiveTxtSpasm.setCenterString(spasmData + "");
                    //   btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                }
            }
        });

        binding.passiveTimeJia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowTime = nowTime + 300000;
                String text = MyTimeUtils.formatTime(nowTime);
                if (nowTime <= 3600000) {
                    binding.passiveTxtDowntimer.setCenterString(text);
                    activeTime = MyTimeUtils.Getminute(nowTime);
                    btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                } else {
                    nowTime = 3600000;
                    text = MyTimeUtils.formatTime(nowTime);
                    binding.passiveTxtDowntimer.setCenterString(text);
                    activeTime = MyTimeUtils.Getminute(nowTime);
                    btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                    return;
                }
            }
        });

        binding.passiveTimeJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowTime = nowTime - 300000;
                String text1 = MyTimeUtils.formatTime(nowTime);
                if (nowTime >= 300000) {
                    binding.passiveTxtDowntimer.setCenterString(text1);
                    activeTime = MyTimeUtils.Getminute(nowTime);
                    btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                } else {
                    nowTime = 300000;
                    text1 = MyTimeUtils.formatTime(nowTime);
                    binding.passiveTxtDowntimer.setCenterString(text1);
                    activeTime = MyTimeUtils.Getminute(nowTime);
                    btDataPro.sendBTMessage(GetCmdCode("50", false, spasmData, zhuansu, activeTime));
                    return;
                }
            }
        });

        binding.passiveImgBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，不能测量血压！", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (uploadData != null && uploadData.getBlood().equals("已连接")) {
                        if (ContorlState.equals("00") || ContorlState.equals("52")) {
                            // Thread.sleep(500);
                            btDataPro.sendBTMessage(GetCmdCode("51", false, spasmData, zhuansu, activeTime));
                            // btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_BEGIN);
                        } else if (ContorlState.equals("51")) {
                            // Thread.sleep(500);
                            btDataPro.sendBTMessage(GetCmdCode("52", false, spasmData, zhuansu, activeTime));
                            // btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_END);
                            ContorlState = "52";
                            binding.passiveTxtBlood.setCenterString("点击开始测量血压");
                        }
                    } else {
                        Toast.makeText(context, "血压仪未连接，请检查设备", Toast.LENGTH_SHORT).show();
                    }
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
        String txts = binding.passiveTxtBegin.getCenterString();
        if (txts.equals("开  始") && nowTime != 0) {

            String highblood = binding.passiveTxtHigh.getCenterString();
            String lowblood = binding.passiveTxtLow.getCenterString();
            if (highblood.equals("0") && lowblood.equals("0")) {
                DialogLoader.getInstance().showConfirmDialog(
                        context,
                        getString(R.string.active_blood),
                        getString(R.string.lab_ok),
                        (dialog, which) -> {
                            dialog.dismiss();
                            binding.passiveTimeJia.setEnabled(false);
                            binding.passiveTimeJian.setEnabled(false);

                            binding.passiveTimeJia.setVisibility(View.INVISIBLE);
                            binding.passiveTimeJian.setVisibility(View.INVISIBLE);
                            binding.passiveImgBegin.setBackground(getResources().getDrawable(R.drawable.stop));

                            binding.passiveTxtBegin.setCenterString("停  止");
                            initCountDownTimer(nowTime);
                            timeCountTool.startCount();
                            activeTime = MyTimeUtils.Getminute(nowTime);
                            btDataPro.sendBTMessage(GetCmdCode("50", true, spasmData, zhuansu, activeTime));
                        },
                        getString(R.string.lab_cancel),
                        (dialog, which) -> {
                            dialog.dismiss();
                            // timeTask.start();
                        }
                );
            } else {
                binding.passiveTimeJia.setEnabled(false);
                binding.passiveTimeJian.setEnabled(false);

                binding.passiveTimeJia.setVisibility(View.INVISIBLE);
                binding.passiveTimeJian.setVisibility(View.INVISIBLE);
                binding.passiveImgBegin.setBackground(getResources().getDrawable(R.drawable.stop));

                binding.passiveTxtBegin.setCenterString("停  止");
                initCountDownTimer(nowTime);
                timeCountTool.startCount();
                activeTime = MyTimeUtils.Getminute(nowTime);
                btDataPro.sendBTMessage(GetCmdCode("50", true, spasmData, zhuansu, activeTime));

            }


        } else {
            //    stop();
            btDataPro.sendBTMessage(GetCmdCode("50", false, 5, 1, 0L));
        }
    }

    public void initCountDownTimer(long millisInFuture) {
        downTimer = new PeterTimeCountRefresh(millisInFuture, 1000);
        downTimer.setOnTimerProgressListener(new PeterTimeCountRefresh.OnTimerProgressListener() {
            @Override
            public void onTimerProgress(long timeLong) {
                nowTime = timeLong;
                String text = MyTimeUtils.formatTime(timeLong);
                binding.passiveTxtDowntimer.setCenterString(text);
            }
        });

        //时间结束回调
        downTimer.setOnTimerFinishListener(new PeterTimeCountRefresh.OnTimerFinishListener() {
            @Override
            public void onTimerFinish() {
                //    ActiveActivity.timeCountTool.stopCount();
                binding.passiveTxtDowntimer.setCenterString("00:00:00");
                nowTime = 300000;
                stop();
                binding.passiveTxtBegin.setCenterString("开  始");
                binding.passiveTimeJia.setEnabled(true);
                binding.passiveTimeJian.setEnabled(true);
                binding.passiveTimeJia.setVisibility(View.VISIBLE);
                binding.passiveTimeJian.setVisibility(View.VISIBLE);
            }
        });
        downTimer.start();
    }

    public void ChangeDialog() {

        if (isBegin) {
            Toast.makeText(context, "运动中，请勿切换模式！", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogLoader.getInstance().showConfirmDialog(
                context,
                getString(R.string.active_change),
                getString(R.string.lab_yes),
                (dialog, which) -> {
                    dialog.dismiss();
                    if (modleType == 1) {
                        LocalConfig.ModType = 0;
                        Intent in = new Intent(context, ActiveActivity.class);
                        //  in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                    } else if (modleType == 2) {
                        LocalConfig.ModType = 2;
                        Intent in = new Intent(context, IntelligenceActivity.class);
                        //   in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                    }
//                    if (modleType == 1) {
//                        LocalConfig.ModType = 0;
//                        Intent in = new Intent(context, ActiveActivity.class);
//                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(in);
//                        finish();
//                    } else if (modleType == 2) {
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

    public String GetCmdCode(String blood_measure, boolean isBegin, int spasms_lv, int speed_lv, Long time_lv) {
        String cmd_head = "A88408",              //包头
                sport_mode = "02",                //运动模式
                active_direction = "21",          //运动方向
                //设定时间
                cmd_end = "ED";                   //结尾
        String zuliHex = "00";
        String spasmsHex = "0" + btDataPro.decToHex(spasms_lv);
        String speedHex = "";
        if (speed_lv >= 16) {
            speedHex = btDataPro.decToHex(speed_lv);
        } else {
            speedHex = "0" + btDataPro.decToHex(speed_lv);
        }

        String timeHex = "";
        if (time_lv >= 16) {
            timeHex = btDataPro.decToHex(Math.toIntExact(time_lv));
        } else {
            timeHex = "0" + btDataPro.decToHex(Math.toIntExact(time_lv));
        }

        String avtive_status = "10";
        if (isBegin) {
            avtive_status = "11";
        }
        String splicingStr = cmd_head + sport_mode + avtive_status + active_direction + zuliHex + spasmsHex
                + speedHex + timeHex + blood_measure;
        String CRC16 = CRC16Util.getCRC16(splicingStr);
        CMD_CODE = splicingStr + CRC16 + cmd_end;
        Log.d("GetCmdCode", "PassiveFragemt,获取指令");
        return CMD_CODE;
    }

    public void DataDisplay(int mark, String ObjectJson) {

        switch (mark) {
            case 1:
                uploadData = gson.fromJson(ObjectJson, UploadData.class);

                if (isBegin) {
                    spasmData = Integer.parseInt(uploadData.getSTspasm());
                    binding.progressViewSpasm.setGraduatedEnabled(true);
//                    binding.progressViewSpasm.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) spasmData, (float) 12)));
//                    binding.progressViewSpasm.startProgressAnimation();
                    binding.passiveTxtSpasm.setCenterString(uploadData.getSTspasm());

                    zhuansu = Integer.parseInt(uploadData.getSTspeed());
                    binding.progressViewZhuansuPassive.setGraduatedEnabled(true);
//                    binding.progressViewZhuansuPassive.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) zhuansu, (float) 60)));
//                    binding.progressViewZhuansuPassive.startProgressAnimation();
                    binding.passiveTxtZhuansu.setCenterString(uploadData.getSTspeed());
                }

                if (uploadData.getBlood_oxy().equals("已连接")) {
                    if (uploadData.getOxy_vaulestr().equals("手指未插入")
                            || uploadData.getOxy_vaulestr().equals("探头脱落")
                            || uploadData.getOxy_vaulestr().equals("127")) {
                        binding.passiveTxtBoxygen.setCenterString("--");
                        if (uploadData.getOxy_vaulestr().equals("127")) {
                            binding.passiveTxtO2State.setCenterString("检测中..");
                        } else {
                            binding.passiveTxtO2State.setCenterString(uploadData.getOxy_vaulestr());
                        }
                    } else {
                        binding.passiveTxtBoxygen.setCenterString(uploadData.getOxy_vaulestr());
                        binding.passiveTxtO2State.setCenterString("");
                    }
                } else {
                    binding.passiveTxtBoxygen.setCenterString("--");
                    binding.passiveTxtO2State.setCenterString(uploadData.getBlood_oxy());
                }
                if (uploadData.getBlood().equals("已连接")) {
                    if (uploadData.getHigh().equals("255") || uploadData.getLow().equals("255")) {
                        binding.passiveTxtHigh.setCenterString("0");
                        binding.passiveTxtLow.setCenterString("0");
                        binding.passiveTxtBloodstate1.setCenterString("测量错误");
                        binding.passiveTxtBloodstate2.setCenterString("测量错误");
                    } else {
                        if (Passive_B_Diastole_Shrink.equals("0/0")) {
                            Passive_B_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
                        } else {
                            Passive_L_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
                        }
                        binding.passiveTxtHigh.setCenterString(uploadData.getHigh());
                        binding.passiveTxtLow.setCenterString(uploadData.getLow());
                        if (!uploadData.getHigh().equals("0")) {
                            LocalConfig.BloodHight = uploadData.getHigh();
                            LocalConfig.BloodLow = uploadData.getLow();
                        }
                        binding.passiveTxtBloodstate1.setCenterString("");
                        binding.passiveTxtBloodstate2.setCenterString("");
                    }
                } else {

                    binding.passiveTxtHigh.setCenterString(LocalConfig.BloodHight);
                    binding.passiveTxtLow.setCenterString(LocalConfig.BloodLow);
                    binding.passiveTxtBloodstate1.setCenterString(uploadData.getBlood());
                    binding.passiveTxtBloodstate2.setCenterString(uploadData.getBlood());
                }

                if (uploadData.getECG().equals("已连接")) {
                    binding.passiveTxtEcgstate.setCenterString("");
                } else {
                    binding.passiveTxtCoory.setCenterString("--");
                    binding.passiveTxtEcgstate.setCenterString(uploadData.getECG());
                }

                if (uploadData.getActiveState().equals("停机状态")) {

                    if (isBegin) {
                        isBegin = false;
                        stop();
                        binding.passiveImgBegin.setBackground(getResources().getDrawable(R.drawable.begin));
                        binding.passiveTxtBegin.setCenterString("开  始");
                        nowTime = Integer.parseInt(uploadData.getSTtime()) * 60 * 1000L;
                        activeTime = MyTimeUtils.Getminute(nowTime);
                        String text1 = MyTimeUtils.formatTime(nowTime);
                        binding.passiveTxtDowntimer.setCenterString(text1);
                        timecount = timeCountTool.stopCount();
                        getCalories_mileage();
                        if (uploadData.getSpasmState() != -1) {
                            if (spasmCount == 5) {
                                DialogLoader.getInstance().showConfirmDialog(
                                        context,
                                        getString(R.string.active_out),
                                        getString(R.string.lab_yes),
                                        (dialog, which) -> {
                                            dialog.dismiss();
                                            BloodEndState = 1;
                                            if (uploadData != null && uploadData.getBlood().equals("已连接")) {
                                                if (ContorlState.equals("00") || ContorlState.equals("52")) {
                                                    btDataPro.sendBTMessage(GetCmdCode("51", false, spasmData, zhuansu, activeTime));
                                                } else if (ContorlState.equals("51")) {
                                                    btDataPro.sendBTMessage(GetCmdCode("52", false, spasmData, zhuansu, activeTime));
                                                    ContorlState = "52";
                                                    binding.passiveTxtBlood.setCenterString("点击开始测量血压");
                                                }
                                            } else {
                                                Toast.makeText(context, "血压仪未连接，请检查设备", Toast.LENGTH_SHORT).show();
                                            }
                                        },
                                        getString(R.string.lab_no),
                                        (dialog, which) -> {
                                            dialog.dismiss();
                                        }
                                );
                            } else {
                                DialogLoader.getInstance().showConfirmDialog(
                                        context,
                                        getString(R.string.active_blood_end),
                                        getString(R.string.lab_yes),
                                        (dialog, which) -> {
                                            dialog.dismiss();
                                            BloodEndState = 1;
                                            if (uploadData != null && uploadData.getBlood().equals("已连接")) {
                                                if (ContorlState.equals("00") || ContorlState.equals("52")) {
                                                    btDataPro.sendBTMessage(GetCmdCode("51", false, spasmData, zhuansu, activeTime));
                                                } else if (ContorlState.equals("51")) {
                                                    btDataPro.sendBTMessage(GetCmdCode("52", false, spasmData, zhuansu, activeTime));
                                                    ContorlState = "52";
                                                    binding.passiveTxtBlood.setCenterString("点击开始测量血压");
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
                            }
                        }
                    }

                } else if (uploadData.getActiveState().equals("运行状态")) {
                    isBegin = true;
                    binding.passiveTimeJia.setEnabled(false);
                    binding.passiveTimeJian.setEnabled(false);

                    binding.passiveTimeJia.setVisibility(View.INVISIBLE);
                    binding.passiveTimeJian.setVisibility(View.INVISIBLE);
                    binding.passiveImgBegin.setBackground(getResources().getDrawable(R.drawable.stop));
                    binding.passiveTxtBegin.setCenterString("停  止");
                    if (downTimer == null) {
                        initCountDownTimer(nowTime);
                        timeCountTool.startCount();
                        activeTime = MyTimeUtils.Getminute(nowTime);
                    }
                }

                if (uploadData.getSpasmState() != -1) {

                    if (uploadData.getSpasmState() > 0) {
                        spasmCount = uploadData.getSpasmState();
                    }
                }

                if (BloodEndState == 2) {
                    BloodEndState = 0;
                    SaveRecord();
                    btDataPro.sendBTMessage(btDataPro.CONNECT_CLOSE);

                    Intent in = new Intent(context, DataResultsActivity.class);
                    //  in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();
                }
                break;

            case 2:
                try {
                    EcgListData = new ArrayList<>();
                    ecgData = gson.fromJson(ObjectJson, EcgData.class);
                    binding.passiveTxtCoory.setCenterString(ecgData.getHeartrate());
                    EcgListData = ecgData.getEcgCoorY();
                    if (EcgListData == null) {
                        binding.passiveTxtEcgstate.setCenterString("心电仪佩戴异常！");
                        //  binding.passiveWaveviewOne.showLine(0f);
                        return;
                    } else {
                        for (int i = 0; i < EcgListData.size(); i++) {
                            Float cooY = EcgListData.get(i);
                            OftenListData.add(cooY);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ArrayList", e.getMessage());
                }
                break;

            case 3:
                ContorlState = ObjectJson;
                if (ContorlState.equals("51")) {
                    //  binding.passiveTxtBlood.setCenterString("测量中");
                } else if (ContorlState.equals("52")) {
                    //  binding.passiveTxtBlood.setCenterString("点击开始测量血压");
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
            recordDetailed.setActivtType("被动模式");
            recordDetailed.setRecordTime(sim);
            //  int zhuansu = Integer.parseInt(binding.passiveTxtZhuansu.getCenterString());
            recordDetailed.setSpeed(zhuansu);
            // int leftlimb = Integer.parseInt(binding.passiveTxtLeft.getCenterString());
            recordDetailed.setLeftLimb(0);
            //    int rightlimb = Integer.parseInt(binding.passiveTxtRight.getCenterString());
            recordDetailed.setRightLimb(0);
//            int resistance = Integer.parseInt(binding.passiveTxtResistance.getCenterString());
            recordDetailed.setResistance(0);

            int heartRate;
            if (binding.passiveTxtCoory.getCenterString().equals("--")) {
                heartRate = 0;
            } else {
                heartRate = Integer.parseInt(binding.passiveTxtCoory.getCenterString());
            }
            recordDetailed.setHeartRate(heartRate);

            int Hbo2;
            if (binding.passiveTxtBoxygen.getCenterString().equals("--")) {
                Hbo2 = 0;
            } else {
                Hbo2 = Integer.parseInt(binding.passiveTxtBoxygen.getCenterString());
            }
            recordDetailed.setHbo2(Hbo2);

            recordDetailed.setSpasm(spasmData);
            recordDetailedDao.insert(recordDetailed);
            //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
        } catch (Exception e) {
            Toast.makeText(context, "数据库异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                        btDataPro.sendBTMessage(GetCmdCode("50", false, 5, 1, 0L));
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

    public void stop() {
        if (downTimer != null && nowTime != 0) {
            downTimer.cancel();
            downTimer = null;
        }
        if (timer1 != null) {
            timer1.cancel();
            timer1.purge();
            timer1 = null;
        }
        if (timerTask1 != null) {
            timerTask1.cancel();
            timerTask1 = null;
        }
        OftenListData = new ArrayList<>();
        binding.passiveWaveviewOne.resetCanavas();
        binding.passiveTxtBegin.setCenterString("开  始");
        binding.passiveTimeJia.setEnabled(true);
        binding.passiveTimeJian.setEnabled(true);
        binding.passiveTimeJia.setVisibility(View.VISIBLE);
        binding.passiveTimeJian.setVisibility(View.VISIBLE);
        nowTime = 300000;
        binding.passiveTxtDowntimer.setCenterString(MyTimeUtils.formatTime(nowTime));
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
