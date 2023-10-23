///*
// * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// */
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
//import com.rick.recoveryapp.databinding.FragemtIntelligenceBinding;
//import com.rick.recoveryapp.entity.EcgData;
//import com.rick.recoveryapp.entity.LiveMessage;
//import com.rick.recoveryapp.entity.protocol.PoolMessage;
//import com.rick.recoveryapp.entity.protocol.UploadData;
//import com.rick.recoveryapp.greendao.RecordDetailedDao;
//import com.rick.recoveryapp.greendao.entity.RecordDetailed;
//import com.rick.recoveryapp.utils.CRC16Util;
//import com.rick.recoveryapp.utils.LocalConfig;
//import com.rick.recoveryapp.utils.PeterTimeCountRefresh;
//
//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class IntelligenceFragemt extends LazyFragment {
//
//    static PeterTimeCountRefresh downTimer;
//    ArrayList<Float> EcgListData;
//    int zhuansuData = 0, resistance = 1, spasm = 1;
//    FragemtIntelligenceBinding binding;
//    RecordDetailedDao recordDetailedDao;
//    UploadData uploadData;
//    EcgData ecgData;
//    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//    String ContorlState = "";
//    BtDataPro btDataPro;
//    String CMD_CODE = "";
//    static DataThread dataThread;
//    static Thread Ithread;
//    static ArrayList<Float> OftenListData;
//    static Timer timer1;
//    static TimerTask timerTask1;
//    Boolean isBlood = false;
//    boolean isBegin = false;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (rootView == null) {
//            binding = FragemtIntelligenceBinding.inflate(getLayoutInflater(), container, false);
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
//            Log.d("onFragmentVisibleChange", "IntelligenceFragemt,可见，加载数据");
//            itinClick();
//            recordDetailedDao = LocalConfig.daoSession.getRecordDetailedDao();
//            btDataPro = new BtDataPro();
//            OftenListData = new ArrayList<>();
//            binding.IntelligenceWaveviewOne.resetCanavas();
//            btDataPro.sendBTMessage(GetCmdCode(resistance, "53", false, zhuansuData, spasm));
//            dataThread = new DataThread();
//            Ithread = new Thread(dataThread);
//            Ithread.start();
//            PassEcg();
//        } else {
//            Log.d("onFragmentVisibleChange", "IntelligenceFragemt,不可见");
//            stop();
//        }
//    }
//
//    private class DataThread implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                initLiveData();
//                LocalConfig.ActiviteType = "智能模式";
//            } catch (Exception e) {
//                Log.d("DataThread", e.getMessage());
//            }
//        }
//    }
//
//    public String GetCmdCode(int zuli, String blood_measure, boolean isBegin, int speed_lv, int spasms_lv) {
//        String cmd_head = "A88408",              //包头
//                sport_mode = "00",                //运动模式
//                active_direction = "20",          //运动方向
//                // spasms_lv = "00",                 //痉挛等级
//                //    speed_lv = "00",                  //速度设定
//                time_lv = "00",                   //设定时间
//                cmd_end = "ED";                   //结尾
//        String zuliHex = "0" + btDataPro.decToHex(zuli);
//        String spasmsHex = "0" + btDataPro.decToHex(spasms_lv);
//        String speedHex = "";
//        if (speed_lv > 16) {
//            speedHex = btDataPro.decToHex(speed_lv);
//        } else {
//            speedHex = "0" + btDataPro.decToHex(speed_lv);
//        }
//
//        String avtive_status = "10";
//        if (isBegin) {
//            avtive_status = "11";
//        }
//
//        String splicingStr = cmd_head + sport_mode + avtive_status + active_direction + zuliHex + spasmsHex
//                + speedHex + time_lv + blood_measure;
//        String CRC16 = CRC16Util.getCRC16(splicingStr);
//        CMD_CODE = splicingStr + CRC16 + cmd_end;
//        return CMD_CODE;
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
//                            binding.IntelligenceWaveviewOne.showLine(cooY);
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
//
//            }
//        };
//        timer1.schedule(timerTask1, 10, 10);
//    }
//
//    public void initLiveData() {
//
//        LiveEventBus
//                .get("BT_PROTOCOL", PoolMessage.class)
//                .observe(this, new Observer<PoolMessage>() {
//                    @Override
//                    public void onChanged(@Nullable PoolMessage msg) {
//                        Log.d("test_BT_PROTOCOL", "IntelligenceFragemt");
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
//                            Toast.makeText(LocalConfig.TrainContext, "数据异常", Toast.LENGTH_SHORT).show();
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
//
//                            binding.inteTxtBoxygen.setCenterString("0");
//                            binding.inteTxtO2State.setCenterString("血氧仪未连接");
////                            binding.inteTxtHigh.setCenterString(LocalConfig.B_Diastole_Shrink);
////                            binding.inteTxtLow.setCenterString(LocalConfig.L_Diastole_Shrink);
//                            binding.inteTxtBloodstate1.setCenterString("血压仪未连接");
//                            binding.inteTxtBloodstate1.setCenterString("血压仪未连接");
//
//                            LocalConfig.B_Diastole_Shrink = "0/0";
//                            LocalConfig.L_Diastole_Shrink = "0/0";
//
//                            int left = 0;
//                            binding.progressViewLeft.setGraduatedEnabled(true);
//                            binding.progressViewLeft.setEndProgress(Float.parseFloat(GetProgress((float) left, (float) 50)));
//                            binding.progressViewLeft.startProgressAnimation();
//                            binding.intelligenceTxtLeft.setCenterString("0");
//
//                            int right = 0;
//                            binding.progressViewRight.setGraduatedEnabled(true);
//                            binding.progressViewRight.setEndProgress(Float.parseFloat(GetProgress((float) right, (float) 50)));
//                            binding.progressViewRight.startProgressAnimation();
//                            binding.intelligenceTxtLeft.setCenterString("0");
//
//                            binding.inteTxtCoory.setCenterString("0");
//                            binding.inteTxtEcgstate.setCenterString("心电仪未连接");
//                            OftenListData.clear();
//                        }
//                    }
//                });
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
//                        binding.inteTxtBoxygen.setCenterString("--");
//                        if (uploadData.getOxy_vaulestr().equals("127")) {
//                            binding.inteTxtO2State.setCenterString("检测中..");
//                        } else {
//                            binding.inteTxtO2State.setCenterString(uploadData.getOxy_vaulestr());
//                        }
//                    } else {
//                        binding.inteTxtBoxygen.setCenterString(uploadData.getOxy_vaulestr());
//                        binding.inteTxtO2State.setCenterString("");
//                    }
//                } else {
//                    binding.inteTxtBoxygen.setCenterString("--");
//                    binding.inteTxtO2State.setCenterString(uploadData.getBlood_oxy());
//                }
//                if (uploadData.getBlood().equals("已连接")) {
//
//                    if (uploadData.getHigh().equals("255") || uploadData.getLow().equals("255")) {
//                        binding.inteTxtHigh.setCenterString("0");
//                        binding.inteTxtLow.setCenterString("0");
//                        binding.inteTxtBloodstate1.setCenterString("测量错误");
//                        binding.inteTxtBloodstate2.setCenterString("测量错误");
//                    } else {
//
//                        if (LocalConfig.B_Diastole_Shrink.equals("0/0")) {
//                            LocalConfig.B_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
//                        } else {
//                            LocalConfig.L_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
//                        }
//
//                        binding.inteTxtHigh.setCenterString(uploadData.getHigh());
//                        binding.inteTxtLow.setCenterString(uploadData.getLow());
//                        if (!uploadData.getHigh().equals("0")) {
//                            LocalConfig.BloodHight = uploadData.getHigh();
//                            LocalConfig.BloodLow = uploadData.getLow();
//                        }
//                        binding.inteTxtBloodstate1.setCenterString("");
//                        binding.inteTxtBloodstate2.setCenterString("");
//                    }
//                } else {
////                    if (LocalConfig.B_Diastole_Shrink.equals("0/0")) {
////                        binding.inteTxtHigh.setCenterString("--");
////                        binding.inteTxtLow.setCenterString("--");
////                    }
//                    binding.inteTxtHigh.setCenterString(LocalConfig.BloodHight);
//                    binding.inteTxtLow.setCenterString(LocalConfig.BloodLow);
//                    binding.inteTxtBloodstate1.setCenterString(uploadData.getBlood());
//                    binding.inteTxtBloodstate2.setCenterString(uploadData.getBlood());
//                }
//
//                if (uploadData.getECG().equals("已连接")) {
//                    binding.inteTxtEcgstate.setCenterString("");
//                } else {
//                    binding.inteTxtCoory.setCenterString("--");
//                    binding.inteTxtEcgstate.setCenterString(uploadData.getECG());
//                }
//                int left = Integer.parseInt(uploadData.getLeft());
//                binding.progressViewLeft.setGraduatedEnabled(true);
//                binding.progressViewLeft.setEndProgress(Float.parseFloat(GetProgress((float) left, (float) 50)));
//                //  binding.progressViewLeft.startProgressAnimation();
//                binding.intelligenceTxtLeft.setCenterString(left + "");
//
//                int right = Integer.parseInt(uploadData.getRight());
//                binding.progressViewRight.setGraduatedEnabled(true);
//                binding.progressViewRight.setEndProgress(Float.parseFloat(GetProgress((float) right, (float) 50)));
//                // binding.progressViewRight.startProgressAnimation();
//                binding.intelligenceTxtRight.setCenterString(right + "");
//
//                if (!uploadData.getSpasmState().equals("未知")) {
//                    LocalConfig.spasmCount = uploadData.getSpasmState();
//                }
//                break;
//
//            case 2:
//                try {
//                    EcgListData = null;
//                    ecgData = gson.fromJson(ObjectJson, EcgData.class);
//                    binding.inteTxtCoory.setCenterString(ecgData.getHeartrate());
//                    EcgListData = ecgData.getEcgCoorY();
//                    if (EcgListData == null) {
//                        binding.inteTxtEcgstate.setCenterString("心电仪佩戴异常！");
//                        binding.IntelligenceWaveviewOne.showLine(0f);
////                        binding.IntelligenceWaveviewTwo.showLine(0f);
////                        binding.IntelligenceWaveviewThree.showLine(0f);
//                    } else {
//                        // Float
//                        for (int i = 0; i < EcgListData.size(); i++) {
//                            Float cooY = EcgListData.get(i);
//                            OftenListData.add(cooY);
//                        }
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(LocalConfig.TrainContext, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//            case 3:
//                ContorlState = ObjectJson;
//                if (ContorlState.equals("51")) {
//                    //  binding.inteTxtBlood.setCenterString("测量中");
//                } else if (ContorlState.equals("52")) {
//                    //  binding.inteTxtBlood.setCenterString("点击开始测量血压");
//                }
//                break;
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
//            recordDetailed.setActivtType("智能模式");
//            recordDetailed.setRecordTime(sim);
//            int zhuansu = Integer.parseInt(binding.intelligenceTxtZhuansu.getCenterString());
//            recordDetailed.setSpeed(zhuansu);
//            int leftlimb = Integer.parseInt(binding.intelligenceTxtLeft.getCenterString());
//            recordDetailed.setLeftLimb(leftlimb);
//            int rightlimb = Integer.parseInt(binding.intelligenceTxtRight.getCenterString());
//            recordDetailed.setRightLimb(rightlimb);
//            int resistance = Integer.parseInt(binding.intelligenceTxtResistance.getCenterString());
//            recordDetailed.setResistance(resistance);
//
//            int heartRate;
//            if (binding.inteTxtCoory.getCenterString().equals("--")) {
//                heartRate = 0;
//            } else {
//                heartRate = Integer.parseInt(binding.inteTxtCoory.getCenterString());
//            }
//            recordDetailed.setHeartRate(heartRate);
//
//            int Hbo2;
//            if (binding.inteTxtBoxygen.getCenterString().equals("--")) {
//                Hbo2 = 0;
//            } else {
//                Hbo2 = Integer.parseInt(binding.inteTxtBoxygen.getCenterString());
//            }
//            recordDetailed.setHbo2(Hbo2);
//
//            recordDetailed.setSpasm(spasm);
//            //  recordDetailed.setSpasmCount(0);
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
//        zhuansuData = 0;
//        resistance = 1;
//        spasm = 1;
//        binding.progressViewZhuansuIntelligence.setGraduatedEnabled(true);
//        binding.progressViewZhuansuIntelligence.setEndProgress(Float.parseFloat(GetProgress((float) zhuansuData, (float) 60)));
//        binding.progressViewZhuansuIntelligence.startProgressAnimation();
//        binding.intelligenceTxtZhuansu.setCenterString(zhuansuData + "");
//
//        binding.progressViewResistance.setGraduatedEnabled(true);
//        binding.progressViewResistance.setEndProgress(Float.parseFloat(GetProgress((float) resistance, (float) 12)));
//        binding.progressViewResistance.startProgressAnimation();
//        binding.intelligenceTxtResistance.setCenterString(resistance + "");
//
//        binding.progressViewSpasm.setGraduatedEnabled(true);
//        binding.progressViewSpasm.setEndProgress(Float.parseFloat(GetProgress((float) spasm, (float) 12)));
//        binding.progressViewSpasm.startProgressAnimation();
//        binding.intelligenceTxtSpasm.setCenterString(spasm + "");
//
//        binding.intelligenceImgBegin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HandlerMessage();
//            }
//        });
//
//        binding.inteImgBlood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    if (uploadData != null && uploadData.getBlood().equals("已连接")) {
//                        if (ContorlState.equals("00") || ContorlState.equals("52")) {
//                            btDataPro.sendBTMessage(GetCmdCode(resistance, "51", isBegin, zhuansuData, spasm));
//                            btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_BEGIN);
//                        } else if (ContorlState.equals("51")) {
//                            btDataPro.sendBTMessage(GetCmdCode(resistance, "52", isBegin, zhuansuData, spasm));
//                            ContorlState = "52";
//                            binding.inteTxtBlood.setCenterString("点击开始测量血压");
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
//
//        binding.inteJiaZhuansu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                zhuansuData = zhuansuData + 5;
//                if (zhuansuData <= 60) {
//                    binding.progressViewZhuansuIntelligence.setGraduatedEnabled(true);
//                    binding.progressViewZhuansuIntelligence.setEndProgress(Float.parseFloat(GetProgress((float) zhuansuData, (float) 60)));
//                    binding.progressViewZhuansuIntelligence.startProgressAnimation();
//                    binding.intelligenceTxtZhuansu.setCenterString(zhuansuData + "");
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                } else {
//                    zhuansuData = 60;
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                    return;
//                }
//            }
//        });
//
//        binding.inteJianZhuansu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                zhuansuData = zhuansuData - 5;
//                if (zhuansuData < 0) {
//                    zhuansuData = 0;
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                    return;
//                } else {
//                    binding.progressViewZhuansuIntelligence.setGraduatedEnabled(true);
//                    binding.progressViewZhuansuIntelligence.setEndProgress(Float.parseFloat(GetProgress((float) zhuansuData, (float) 60)));
//                    binding.progressViewZhuansuIntelligence.startProgressAnimation();
//                    binding.intelligenceTxtZhuansu.setCenterString(zhuansuData + "");
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                }
//            }
//        });
//
//        binding.inteJiaResistance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resistance = resistance + 1;
//                if (resistance <= 12) {
//                    binding.progressViewResistance.setGraduatedEnabled(true);
//                    binding.progressViewResistance.setEndProgress(Float.parseFloat(GetProgress((float) resistance, (float) 12)));
//                    binding.progressViewResistance.startProgressAnimation();
//                    binding.intelligenceTxtResistance.setCenterString(resistance + "");
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                } else {
//                    resistance = 12;
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                    return;
//                }
//            }
//        });
//
//        binding.inteJianResistance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resistance = resistance - 1;
//                if (resistance < 1) {
//                    resistance = 1;
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                    return;
//                } else {
//                    binding.progressViewResistance.setGraduatedEnabled(true);
//                    binding.progressViewResistance.setEndProgress(Float.parseFloat(GetProgress((float) resistance, (float) 12)));
//                    binding.progressViewResistance.startProgressAnimation();
//                    binding.intelligenceTxtResistance.setCenterString(resistance + "");
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                }
//            }
//        });
//
//        binding.inteJiaSpasm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                spasm = spasm + 1;
//                if (spasm <= 12) {
//                    binding.progressViewSpasm.setGraduatedEnabled(true);
//                    binding.progressViewSpasm.setEndProgress(Float.parseFloat(GetProgress((float) spasm, (float) 12)));
//                    binding.progressViewSpasm.startProgressAnimation();
//                    binding.intelligenceTxtSpasm.setCenterString(spasm + "");
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                } else {
//                    spasm = 12;
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                    return;
//                }
//            }
//        });
//
//        binding.inteJianSpasm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                spasm = spasm - 1;
//                if (spasm < 1) {
//                    spasm = 1;
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                    return;
//                } else {
//                    binding.progressViewSpasm.setGraduatedEnabled(true);
//                    binding.progressViewSpasm.setEndProgress(Float.parseFloat(GetProgress((float) spasm, (float) 12)));
//                    binding.progressViewSpasm.startProgressAnimation();
//                    binding.intelligenceTxtSpasm.setCenterString(spasm + "");
//                    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//                }
//            }
//        });
//    }
//
//    public void HandlerMessage() {
//        String txts = binding.intelligenceTxtBegin.getCenterString();
//
//        if (txts.equals("开始")) {
//            isBegin = true;
//            btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//            binding.intelligenceTxtBegin.setCenterString("暂停");
//            //   initCountDownTimer(nowTime);
//            //   ActiveActivity.timeCountTool.startCount();
//
//        } else {
//            isBegin = false;
//            btDataPro.sendBTMessage(GetCmdCode(resistance, "50", isBegin, zhuansuData, spasm));
//            //   ActiveActivity.timeCountTool.stopCount();
//            binding.intelligenceTxtBegin.setCenterString("开始");
//            if (downTimer != null) {
//                downTimer.cancel();
//                downTimer = null;
//            }
//        }
//    }
//
//    public void stop() {
//
//        if (dataThread != null || Ithread != null) {
//            dataThread = null;
//            Ithread = null;
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
//        OftenListData = new ArrayList<>();
//        LocalConfig.B_Diastole_Shrink = "0/0";
//
//        LocalConfig.L_Diastole_Shrink = "0/0";
//
//        LocalConfig.spasmCount = "0";
//
//        binding.IntelligenceWaveviewOne.resetCanavas();
//        if (btDataPro != null) {
//            btDataPro.sendBTMessage(GetCmdCode(resistance, "50", false, zhuansuData, spasm));
//        }
//
//
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        stop();
//    }
//}
