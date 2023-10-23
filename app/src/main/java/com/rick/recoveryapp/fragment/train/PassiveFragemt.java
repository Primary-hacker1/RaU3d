//package com.rick.recoveryapp.fragment.train;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.lifecycle.Observer;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.jeremyliao.liveeventbus.LiveEventBus;
//import com.rick.recoveryapp.activity.ActiveActivity;
//import com.rick.recoveryapp.base.LazyFragment;
//import com.rick.recoveryapp.bluetooth.BtDataPro;
//import com.rick.recoveryapp.databinding.FragemtPassiveBinding;
//import com.rick.recoveryapp.entity.EcgData;
//import com.rick.recoveryapp.entity.LiveMessage;
//import com.rick.recoveryapp.entity.protocol.PoolMessage;
//import com.rick.recoveryapp.entity.protocol.UploadData;
//import com.rick.recoveryapp.greendao.RecordDetailedDao;
//import com.rick.recoveryapp.greendao.entity.RecordDetailed;
//import com.rick.recoveryapp.utils.CRC16Util;
//import com.rick.recoveryapp.utils.LocalConfig;
//import com.rick.recoveryapp.utils.MyTimeUtils;
//import com.rick.recoveryapp.utils.PeterTimeCountRefresh;
//
//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class PassiveFragemt extends LazyFragment {
//
//    static PeterTimeCountRefresh downTimer;
//    static long nowTime = 300000;
//    int zhuansu = 0, spasmData = 1;
//    static FragemtPassiveBinding binding;
//    RecordDetailedDao recordDetailedDao;
//    static BtDataPro btDataPro;
//    UploadData uploadData;
//    ArrayList<Float> EcgListData;
//    static ArrayList<Float> OftenListData;
//    EcgData ecgData;
//    String ContorlState = "";
//    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//    DataThread dataThread;
//    Thread Pthread;
//    Timer timer1;
//    TimerTask timerTask1;
//    String CMD_CODE = "";
//    Long activeTime = 0L;
//    boolean isBlood = false;
//    boolean isBegin = false;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        if (rootView == null) {
//            binding = FragemtPassiveBinding.inflate(getLayoutInflater(), container, false);
//            rootView = binding.getRoot();
//        }
//        return rootView;
//    }
//
//    @Override
//    protected void onFragmentVisibleChange(boolean isVisible) {
//        super.onFragmentVisibleChange(isVisible);
//        //根据isVisible做后续操作
//        if (isVisible) {
//            android.util.Log.d("onFragmentVisibleChange", "PassiveFragemt,可见，加载数据");
//            recordDetailedDao = LocalConfig.daoSession.getRecordDetailedDao();
//            btDataPro = new BtDataPro();
//            OftenListData = new ArrayList<>();
//            nowTime = 300000;
//            activeTime = MyTimeUtils.Getminute(nowTime);
//            binding.passiveWaveviewOne.resetCanavas();
//            itinClick();
//            btDataPro.sendBTMessage(GetCmdCode("53", false, 1, 1, activeTime));
//            dataThread = new DataThread();
//            Pthread = new Thread(dataThread);
//            Pthread.start();
//            PassEcg();
//        } else {
//            android.util.Log.d("onFragmentVisibleChange", "PassiveFragemt,不可见");
//            stop();
//        }
//    }
//
//    private class DataThread implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                initHandler();
//                LocalConfig.ActiviteType = "被动模式";
//
//            } catch (Exception e) {
//                Log.d("DataThread", e.getMessage());
//            }
//        }
//    }
//
//    public String GetCmdCode(String blood_measure, boolean isBegin, int spasms_lv, int speed_lv, Long time_lv) {
//        String cmd_head = "A88408",              //包头
//                sport_mode = "02",                //运动模式
//                active_direction = "21",          //运动方向
//                //设定时间
//                cmd_end = "ED";                   //结尾
//        String zuliHex = "00";
//        String spasmsHex = "0" + btDataPro.decToHex(spasms_lv);
//        String speedHex = "";
//        if (speed_lv > 16) {
//            speedHex = btDataPro.decToHex(speed_lv);
//        } else {
//            speedHex = "0" + btDataPro.decToHex(speed_lv);
//        }
//
//        String timeHex = "";
//        if (time_lv > 16) {
//            timeHex = btDataPro.decToHex(Math.toIntExact(time_lv));
//        } else {
//            timeHex = "0" + btDataPro.decToHex(Math.toIntExact(time_lv));
//        }
//
//        String avtive_status = "10";
//        if (isBegin) {
//            avtive_status = "11";
//        }
//        String splicingStr = cmd_head + sport_mode + avtive_status + active_direction + zuliHex + spasmsHex
//                + speedHex + timeHex + blood_measure;
//        String CRC16 = CRC16Util.getCRC16(splicingStr);
//        CMD_CODE = splicingStr + CRC16 + cmd_end;
//        Log.d("GetCmdCode", "PassiveFragemt,获取指令");
//        return CMD_CODE;
//    }
//
//    public void initHandler() {
//
//        LiveEventBus
//                .get("BT_PROTOCOL", PoolMessage.class)
//                .observe(this, new Observer<PoolMessage>() {
//                    @Override
//                    public void onChanged(@Nullable PoolMessage msg) {
//                        Log.d("test_BT_PROTOCOL", "PassiveFragemt");
//                        if (msg.isState()) {
//                            //  Log.d("Bluetoolth", msg.getObjectName());
//                            int mark = 0;
//                            if (msg.getObjectName().equals(btDataPro.UPLODE_ANSWER)) {
//                                mark = 1;
//                            } else if (msg.getObjectName().equals(btDataPro.ECGDATA_ANSWER)) {
//                                mark = 2;
//                            } else if (msg.getObjectName().equals(btDataPro.CONTORL_ANSWER)) {
//                                mark = 3;
//                            }
//                            DataDisplay(mark, msg.getObjectJson());
//                            UpdatProgress();
//                        } else {
//                            //  Toast.makeText(LocalConfig.TrainContext, "数据异常", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//        LiveEventBus
//                .get("BT_CONNECTED", LiveMessage.class)
//                .observe(this, new Observer<LiveMessage>() {
//                    @Override
//                    public void onChanged(@Nullable LiveMessage msg) {
//                        if (!msg.getIsConnt()) {
//                            //未连接
//                            binding.passiveTxtO2State.setCenterString("血氧仪未连接");
//                            binding.passiveTxtBoxygen.setCenterString("0");
//
//                            binding.passiveTxtBloodstate1.setCenterString("血压仪未连接");
//                            binding.passiveTxtBloodstate2.setCenterString("血压仪未连接");
//
//                            LocalConfig.B_Diastole_Shrink = "0/0";
//                            LocalConfig.L_Diastole_Shrink = "0/0";
//
//                            binding.passiveTxtCoory.setCenterString("0");
//                            binding.passiveTxtEcgstate.setCenterString("心电仪未连接");
//                            OftenListData.clear();
//                        }
//                    }
//                });
//    }
//
//    public void PassEcg() {
//        timer1 = new Timer();
//        timerTask1 = new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    if (OftenListData != null) {
//                        if (OftenListData.size() > 0) {
//                            Float cooY = OftenListData.get(0);
//                            binding.passiveWaveviewOne.showLine(cooY);
//                            OftenListData.remove(0);
//                        } else {
//                            return;
//                        }
//                    } else {
//                        return;
//                    }
//                } catch (Exception e) {
//                    com.efs.sdk.base.core.util.Log.d("EcgError", e.getMessage());
//                }
//            }
//        };
//        timer1.schedule(timerTask1, 10, 10);
//    }
//
//    public void DataDisplay(int mark, String ObjectJson) {
//
//        switch (mark) {
//            case 1:
//                uploadData = gson.fromJson(ObjectJson, UploadData.class);
//                if (uploadData.getBlood_oxy().equals("已连接")) {
//                    if (uploadData.getOxy_vaulestr().equals("手指未插入")
//                            || uploadData.getOxy_vaulestr().equals("探头脱落")
//                            || uploadData.getOxy_vaulestr().equals("127")) {
//                        binding.passiveTxtBoxygen.setCenterString("--");
//                        if (uploadData.getOxy_vaulestr().equals("127")) {
//                            binding.passiveTxtO2State.setCenterString("检测中..");
//                        } else {
//                            binding.passiveTxtO2State.setCenterString(uploadData.getOxy_vaulestr());
//                        }
//                    } else {
//                        binding.passiveTxtBoxygen.setCenterString(uploadData.getOxy_vaulestr());
//                        binding.passiveTxtO2State.setCenterString("");
//                    }
//                } else {
//                    binding.passiveTxtBoxygen.setCenterString("--");
//                    binding.passiveTxtO2State.setCenterString(uploadData.getBlood_oxy());
//                }
//                if (uploadData.getBlood().equals("已连接")) {
//                    if (uploadData.getHigh().equals("255") || uploadData.getLow().equals("255")) {
//                        binding.passiveTxtHigh.setCenterString("0");
//                        binding.passiveTxtLow.setCenterString("0");
//                        binding.passiveTxtBloodstate1.setCenterString("测量错误");
//                        binding.passiveTxtBloodstate2.setCenterString("测量错误");
//                    } else {
//                        if (LocalConfig.B_Diastole_Shrink.equals("0/0")) {
//                            LocalConfig.B_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
//                        } else {
//                            LocalConfig.L_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
//                        }
//                        binding.passiveTxtHigh.setCenterString(uploadData.getHigh());
//                        binding.passiveTxtLow.setCenterString(uploadData.getLow());
//                        if (!uploadData.getHigh().equals("0")) {
//                            LocalConfig.BloodHight = uploadData.getHigh();
//                            LocalConfig.BloodLow = uploadData.getLow();
//                        }
//                        binding.passiveTxtBloodstate1.setCenterString("");
//                        binding.passiveTxtBloodstate2.setCenterString("");
//                    }
//                } else {
////                    if (LocalConfig.B_Diastole_Shrink.equals("0/0")) {
////                        binding.passiveTxtHigh.setCenterString("--");
////                        binding.passiveTxtLow.setCenterString("--");
////                    }
//                    binding.passiveTxtHigh.setCenterString(LocalConfig.BloodHight);
//                    binding.passiveTxtLow.setCenterString(LocalConfig.BloodLow);
//                    binding.passiveTxtBloodstate1.setCenterString(uploadData.getBlood());
//                    binding.passiveTxtBloodstate2.setCenterString(uploadData.getBlood());
//                }
//
//                if (uploadData.getECG().equals("已连接")) {
//                    binding.passiveTxtEcgstate.setCenterString("");
//                } else {
//                    binding.passiveTxtCoory.setCenterString("--");
//                    binding.passiveTxtEcgstate.setCenterString(uploadData.getECG());
//                }
//                //   binding.passiveTxtZhuansu.setCenterString(uploadData.getSpeed());
////                binding.passiveTxtLeft.setCenterString(uploadData.getLeft());
////                binding.passiveTxtRight.setCenterString(uploadData.getRight());
//
//                if (!uploadData.getSpasmState().equals("未知")) {
//                    LocalConfig.spasmCount = uploadData.getSpasmState();
//                }
//                break;
//
//            case 2:
//                try {
//                    EcgListData = new ArrayList<>();
//                    ecgData = gson.fromJson(ObjectJson, EcgData.class);
//                    binding.passiveTxtCoory.setCenterString(ecgData.getHeartrate());
//                    EcgListData = ecgData.getEcgCoorY();
//                    if (EcgListData == null) {
//                        binding.passiveTxtEcgstate.setCenterString("心电仪佩戴异常！");
//                        binding.passiveWaveviewOne.showLine(0f);
////                        binding.passiveWaveviewTwo.showLine(0f);
////                        binding.passiveWaveviewThree.showLine(0f);
//                        return;
//                    } else {
//                        for (int i = 0; i < EcgListData.size(); i++) {
//                            Float cooY = EcgListData.get(i);
//                            OftenListData.add(cooY);
//                        }
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(LocalConfig.TrainContext, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.d("ArrayList", e.getMessage());
//                }
//                break;
//
//            case 3:
//                ContorlState = ObjectJson;
//                if (ContorlState.equals("51")) {
//                    //  binding.passiveTxtBlood.setCenterString("测量中");
//                } else if (ContorlState.equals("52")) {
//                    //  binding.passiveTxtBlood.setCenterString("点击开始测量血压");
//                }
//                break;
//
//        }
//    }
//
//    /**
//     * 模拟源源不断的数据源
//     */
//    public void UpdatProgress() {
//        try {
//
//            Date date = new Date();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String sim = dateFormat.format(date);
//
//            RecordDetailed recordDetailed = new RecordDetailed();
//            recordDetailed.setRecordID(LocalConfig.UserID);
//            recordDetailed.setActivtType("被动模式");
//            recordDetailed.setRecordTime(sim);
//            //  int zhuansu = Integer.parseInt(binding.passiveTxtZhuansu.getCenterString());
//            recordDetailed.setSpeed(zhuansu);
//            // int leftlimb = Integer.parseInt(binding.passiveTxtLeft.getCenterString());
//            recordDetailed.setLeftLimb(0);
//            //    int rightlimb = Integer.parseInt(binding.passiveTxtRight.getCenterString());
//            recordDetailed.setRightLimb(0);
////            int resistance = Integer.parseInt(binding.passiveTxtResistance.getCenterString());
//            recordDetailed.setResistance(0);
//
//            int heartRate;
//            if (binding.passiveTxtCoory.getCenterString().equals("--")) {
//                heartRate = 0;
//            } else {
//                heartRate = Integer.parseInt(binding.passiveTxtCoory.getCenterString());
//            }
//            recordDetailed.setHeartRate(heartRate);
//
//            int Hbo2;
//            if (binding.passiveTxtBoxygen.getCenterString().equals("--")) {
//                Hbo2 = 0;
//            } else {
//                Hbo2 = Integer.parseInt(binding.passiveTxtBoxygen.getCenterString());
//            }
//            recordDetailed.setHbo2(Hbo2);
//
//            recordDetailed.setSpasm(spasmData);
//            // recordDetailed.setSpasmCount(0);
//            recordDetailedDao.insert(recordDetailed);
//            //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
//        } catch (Exception e) {
//            Toast.makeText(LocalConfig.TrainContext, "数据库异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    //获取百分比
//    public String GetProgress(float EndProgress, float AllProgress) {
//        String Progress = null;
//        try {
//            // 创建一个数值格式化对象
//            NumberFormat numberFormat = NumberFormat.getInstance();
//            // 设置精确到小数点后2位
//            numberFormat.setMaximumFractionDigits(0);
//            Progress = numberFormat.format(EndProgress / AllProgress * 100);
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        return Progress;
//    }
//
//    public void itinClick() {
//
//        zhuansu = 0;
//        spasmData = 1;
//        binding.progressViewZhuansuPassive.setGraduatedEnabled(true);
//        binding.progressViewZhuansuPassive.setEndProgress(Float.parseFloat(GetProgress((float) zhuansu, (float) 60)));
//        binding.progressViewZhuansuPassive.startProgressAnimation();
//        binding.passiveTxtZhuansu.setCenterString(zhuansu + "");
//
//        binding.progressViewSpasm.setGraduatedEnabled(true);
//        binding.progressViewSpasm.setEndProgress(Float.parseFloat(GetProgress((float) spasmData, (float) 12)));
//        binding.progressViewSpasm.startProgressAnimation();
//        binding.passiveTxtSpasm.setCenterString(spasmData + "");
//
//        binding.passiveImgBegin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HandlerMessage();
//            }
//        });
//
//        binding.passiveZhuansuJia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                zhuansu = zhuansu + 5;
//                if (zhuansu <= 60) {
//                    binding.progressViewZhuansuPassive.setGraduatedEnabled(true);
//                    binding.progressViewZhuansuPassive.setEndProgress(Float.parseFloat(GetProgress((float) zhuansu, (float) 60)));
//                    binding.progressViewZhuansuPassive.startProgressAnimation();
//                    binding.passiveTxtZhuansu.setCenterString(zhuansu + "");
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                } else {
//                    zhuansu = 60;
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                    return;
//                }
//            }
//        });
//
//        binding.passiveZhuansuJian.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                zhuansu = zhuansu - 5;
//                if (zhuansu < 0) {
//                    zhuansu = 0;
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                    return;
//                } else {
//                    binding.progressViewZhuansuPassive.setGraduatedEnabled(true);
//                    binding.progressViewZhuansuPassive.setEndProgress(Float.parseFloat(GetProgress((float) zhuansu, (float) 60)));
//                    binding.progressViewZhuansuPassive.startProgressAnimation();
//                    binding.passiveTxtZhuansu.setCenterString(zhuansu + "");
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                }
//            }
//        });
//
//        binding.passiveSpasmJia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                spasmData = spasmData + 1;
//                if (spasmData <= 12) {
//                    binding.progressViewSpasm.setGraduatedEnabled(true);
//                    binding.progressViewSpasm.setEndProgress(Float.parseFloat(GetProgress((float) spasmData, (float) 12)));
//                    binding.progressViewSpasm.startProgressAnimation();
//                    binding.passiveTxtSpasm.setCenterString(spasmData + "");
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                } else {
//                    spasmData = 12;
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                    return;
//                }
//            }
//        });
//
//        binding.passiveSpasmJian.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                spasmData = spasmData - 1;
//                if (spasmData < 1) {
//                    spasmData = 1;
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                    return;
//                } else {
//                    binding.progressViewSpasm.setGraduatedEnabled(true);
//                    binding.progressViewSpasm.setEndProgress(Float.parseFloat(GetProgress((float) spasmData, (float) 12)));
//                    binding.progressViewSpasm.startProgressAnimation();
//                    binding.passiveTxtSpasm.setCenterString(spasmData + "");
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                }
//            }
//        });
//
//        binding.passiveTimeJia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nowTime = nowTime + 300000;
//                String text = MyTimeUtils.formatTime(nowTime);
//                if (nowTime <= 3600000) {
//                    binding.passiveTxtDowntimer.setCenterString(text);
//                    activeTime = MyTimeUtils.Getminute(nowTime);
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                } else {
//                    nowTime = 3600000;
//                    text = MyTimeUtils.formatTime(nowTime);
//                    binding.passiveTxtDowntimer.setCenterString(text);
//                    activeTime = MyTimeUtils.Getminute(nowTime);
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                    return;
//                }
//            }
//        });
//
//        binding.passiveTimeJian.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nowTime = nowTime - 300000;
//                String text1 = MyTimeUtils.formatTime(nowTime);
//                if (nowTime >= 300000) {
//                    binding.passiveTxtDowntimer.setCenterString(text1);
//                    activeTime = MyTimeUtils.Getminute(nowTime);
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                } else {
//                    nowTime = 300000;
//                    text1 = MyTimeUtils.formatTime(nowTime);
//                    binding.passiveTxtDowntimer.setCenterString(text1);
//                    activeTime = MyTimeUtils.Getminute(nowTime);
//                    btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//                    return;
//                }
//            }
//        });
//
//        binding.passiveImgBlood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    if (uploadData != null && uploadData.getBlood().equals("已连接")) {
//                        if (ContorlState.equals("00") || ContorlState.equals("52")) {
//                            // Thread.sleep(500);
//                            btDataPro.sendBTMessage(GetCmdCode("51", isBegin, spasmData, zhuansu, activeTime));
//                            // btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_BEGIN);
//                        } else if (ContorlState.equals("51")) {
//                            // Thread.sleep(500);
//                            btDataPro.sendBTMessage(GetCmdCode("52", isBegin, spasmData, zhuansu, activeTime));
//                            // btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_END);
//                            ContorlState = "52";
//                            binding.passiveTxtBlood.setCenterString("点击开始测量血压");
//                        }
//                    } else {
//                        Toast.makeText(LocalConfig.TrainContext, "血压仪未连接，请检查设备", Toast.LENGTH_SHORT).show();
//                    }
//                    isBlood = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public void HandlerMessage() {
//        String txts = binding.passiveTxtBegin.getCenterString();
//        if (txts.equals("开始") && nowTime != 0) {
//            isBegin = true;
//            binding.passiveTimeJia.setEnabled(false);
//            binding.passiveTimeJian.setEnabled(false);
//
//            binding.passiveTimeJia.setVisibility(View.INVISIBLE);
//            binding.passiveTimeJian.setVisibility(View.INVISIBLE);
//
//            binding.passiveTxtBegin.setCenterString("暂停");
//            initCountDownTimer(nowTime);
//         //   ActiveActivity.timeCountTool.startCount();
//            activeTime = MyTimeUtils.Getminute(nowTime);
//            btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//
//        } else {
//            isBegin = false;
//          //  ActiveActivity.timeCountTool.stopCount();
//            binding.passiveTxtBegin.setCenterString("开始");
//            binding.passiveTimeJia.setEnabled(true);
//            binding.passiveTimeJian.setEnabled(true);
//            binding.passiveTimeJia.setVisibility(View.VISIBLE);
//            binding.passiveTimeJian.setVisibility(View.VISIBLE);
//            if (downTimer != null && nowTime != 0) {
//                downTimer.cancel();
//                downTimer = null;
//            }
//            btDataPro.sendBTMessage(GetCmdCode("50", isBegin, spasmData, zhuansu, activeTime));
//        }
//    }
//
//    public void stop() {
//
//        if (dataThread != null || Pthread != null) {
//            dataThread = null;
//            Pthread = null;
//        }
//
//        if (downTimer != null && nowTime != 0) {
//            downTimer.cancel();
//            downTimer = null;
//        }
//
//        if (timer1 != null) {
//            timer1.cancel();
//            timer1.purge();
//            timer1 = null;
//        }
//
//        if (timerTask1 != null) {
//            timerTask1.cancel();
//            timerTask1 = null;
//        }
//
//        OftenListData = new ArrayList<>();
//        binding.passiveWaveviewOne.resetCanavas();
//        LocalConfig.B_Diastole_Shrink = "0/0";
//        LocalConfig.L_Diastole_Shrink = "0/0";
//        LocalConfig.spasmCount = "0";
//        binding.passiveTxtBegin.setCenterString("开始");
//        binding.passiveTimeJia.setEnabled(true);
//        binding.passiveTimeJian.setEnabled(true);
//        binding.passiveTimeJia.setVisibility(View.VISIBLE);
//        binding.passiveTimeJian.setVisibility(View.VISIBLE);
//
//        nowTime = 300000;
//        binding.passiveTxtDowntimer.setCenterString(MyTimeUtils.formatTime(nowTime));
//        if (btDataPro != null) {
//            btDataPro.sendBTMessage(GetCmdCode("50", false, 5, 1, 5L));
//        }
//
//    }
//
//    public void initCountDownTimer(long millisInFuture) {
//        downTimer = new PeterTimeCountRefresh(millisInFuture, 1000);
//        downTimer.setOnTimerProgressListener(new PeterTimeCountRefresh.OnTimerProgressListener() {
//            @Override
//            public void onTimerProgress(long timeLong) {
//                nowTime = timeLong;
//                String text = MyTimeUtils.formatTime(timeLong);
//                binding.passiveTxtDowntimer.setCenterString(text);
//            }
//        });
//
//        //时间结束回调
//        downTimer.setOnTimerFinishListener(new PeterTimeCountRefresh.OnTimerFinishListener() {
//            @Override
//            public void onTimerFinish() {
//            //    ActiveActivity.timeCountTool.stopCount();
//                binding.passiveTxtDowntimer.setCenterString("00:00:00");
//                nowTime = 300000;
//                stop();
//                binding.passiveTxtBegin.setCenterString("开始");
//                binding.passiveTimeJia.setEnabled(true);
//                binding.passiveTimeJian.setEnabled(true);
//                binding.passiveTimeJia.setVisibility(View.VISIBLE);
//                binding.passiveTimeJian.setVisibility(View.VISIBLE);
//            }
//        });
//        downTimer.start();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        nowTime = 300000;
//        binding.passiveTxtDowntimer.setCenterString(MyTimeUtils.formatTime(nowTime));
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        stop();
//    }
//
//}
