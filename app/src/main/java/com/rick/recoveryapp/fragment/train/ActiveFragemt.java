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
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.lifecycle.Observer;
//
//import com.efs.sdk.base.core.util.Log;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.jeremyliao.liveeventbus.LiveEventBus;
//import com.rick.recoveryapp.activity.ActiveActivity;
//import com.rick.recoveryapp.base.LazyFragment;
//import com.rick.recoveryapp.bluetooth.BtDataPro;
//import com.rick.recoveryapp.databinding.FragemtActiveBinding;
//import com.rick.recoveryapp.entity.EcgData;
//import com.rick.recoveryapp.entity.LiveMessage;
//import com.rick.recoveryapp.entity.protocol.PoolMessage;
//import com.rick.recoveryapp.entity.protocol.UploadData;
//import com.rick.recoveryapp.greendao.EcgDataDBDao;
//import com.rick.recoveryapp.greendao.RecordDetailedDao;
//import com.rick.recoveryapp.greendao.entity.RecordDetailed;
//import com.rick.recoveryapp.utils.CRC16Util;
//import com.rick.recoveryapp.utils.LocalConfig;
//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//public class ActiveFragemt extends LazyFragment {
//
//    int resiDta = 1;
//    ArrayList<Float> EcgListData;
//    static ArrayList<Float> OftenListData;
//    FragemtActiveBinding binding;
//    RecordDetailedDao recordDetailedDao;
//    EcgDataDBDao ecgDataDBDao;
//    UploadData uploadData;
//    EcgData ecgData;
//    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
//    String ContorlState = "52";
//    BtDataPro btDataPro;
//    String CMD_CODE = "";
//    static DataThread dataThread;
//    static Thread Athread;
//    static Timer timer1;
//    static TimerTask timerTask1;
//    Float repeat = 0f;
//
//    boolean isBegin = false;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (rootView == null) {
//            binding = FragemtActiveBinding.inflate(getLayoutInflater(), container, false);
//            rootView = binding.getRoot();
//        }
//        return rootView;
//    }

//    @Override
//    protected void onFragmentVisibleChange(boolean isVisible) {
//        super.onFragmentVisibleChange(isVisible);
//        //根据isVisible做后续操作
//        if (isVisible) {
//            Log.d("onFragmentVisibleChange", "ActiveFragemt,可见，加载数据");
//            EcgListData = new ArrayList<>();
//            btDataPro = new BtDataPro();
//            OftenListData = new ArrayList<>();
//            itinClick();
//            LocalConfig.ActiviteType = "主动模式";
//            binding.activeWaveviewOne.resetCanavas();
//            recordDetailedDao = LocalConfig.daoSession.getRecordDetailedDao();
//            ecgDataDBDao = LocalConfig.daoSession.getEcgDataDBDao();
//            btDataPro.sendBTMessage(GetCmdCode(1, "53", false));
//            dataThread = new DataThread();
//            Athread = new Thread(dataThread);
//            Athread.start();
//            PassEcg();
//        } else {
//            Log.d("onFragmentVisibleChange", "ActiveFragemt,不可见");
//            stop();
//        }
//    }
//
//    public String GetCmdCode(int zuli, String blood_measure, boolean isBegin) {
//        String cmd_head = "A88408",              //包头
//                sport_mode = "01",                //运动模式
//                active_direction = "20",          //运动方向
//                spasms_lv = "00",                 //痉挛等级
//                speed_lv = "00",                  //速度设定
//                time_lv = "00",                   //设定时间
//                cmd_end = "ED";                   //结尾
//        String zuliHex = "0" + btDataPro.decToHex(zuli);
//        String avtive_status = "10";
//        if (isBegin) {
//            avtive_status = "11";
//        }
//        String splicingStr = cmd_head + sport_mode + avtive_status + active_direction + zuliHex + spasms_lv
//                + speed_lv + time_lv + blood_measure;
//        String CRC16 = CRC16Util.getCRC16(splicingStr);
//        CMD_CODE = splicingStr + CRC16 + cmd_end;
//        Log.d("GetCmdCode", "ActiveFragemt,获取指令");
//        return CMD_CODE;
//    }
//
//    public void initLiveData() {
//
//        LiveEventBus
//                .get("BT_PROTOCOL", PoolMessage.class)
//                .observe(this, new Observer<PoolMessage>() {
//                    @Override
//                    public void onChanged(@Nullable PoolMessage msg) {
//                        Log.d("test_BT_PROTOCOL", "ActiveFragemt");
//                        if (msg.isState()) {
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
//                            binding.activeTxtBoxygen.setCenterString("0");
//                            binding.activeTxtO2State.setCenterString("血氧仪未连接");
////                            binding.activeTxtHigh.setCenterString(LocalConfig.B_Diastole_Shrink);
////                            binding.activeTxtLow.setCenterString(LocalConfig.L_Diastole_Shrink);
//                            binding.activeTxtBloodstate1.setCenterString("血压仪未连接");
//                            binding.activeTxtBloodstate2.setCenterString("血压仪未连接");
//                            LocalConfig.B_Diastole_Shrink = "0/0";
//                            LocalConfig.L_Diastole_Shrink = "0/0";
//
//                            binding.activeTxtCoory.setCenterString("0");
//                            binding.activeTxtEcgstate.setCenterString("心电仪未连接");
//                            OftenListData.clear();
//
//                            int left = 0;
//                            binding.progressViewLeft.setGraduatedEnabled(true);
//                            binding.progressViewLeft.setEndProgress(Float.parseFloat(GetProgress((float) left, (float) 50)));
//                            binding.progressViewLeft.startProgressAnimation();
//                            binding.activeTxtLeft.setCenterString("0");
//
//                            int right = 0;
//                            binding.progressViewRight.setGraduatedEnabled(true);
//                            binding.progressViewRight.setEndProgress(Float.parseFloat(GetProgress((float) right, (float) 50)));
//                            binding.progressViewRight.startProgressAnimation();
//                            binding.activeTxtRight.setCenterString("0");
//                        }
//                    }
//                });
//    }
//
////    Handler mHandler = new Handler() {
////
////        //handleMessage为处理消息的方法
////        public void handleMessage(Message msg) {
////            super.handleMessage(msg);
////            Bundle bundle = msg.getData();
////            float cooY = bundle.getFloat("cooY");
////            switch (msg.what) {
////                case 0:
////                    // DataDisplay(msg.what, datas);
////                    //   SendData();
////                    binding.activeWaveviewOne.showLine(0f);
////                    break;
////
////                case 1:
////                    binding.activeWaveviewOne.showLine(cooY);
////                    //   SendData();
////                    break;
////
//////                case 2:
//////                    DataDisplay(msg.what, datas);
//////                  //  SendData();
//////                    break;
//////
//////                case 3:
//////                    DataDisplay(msg.what, datas);
//////                  //  SendData();
//////                    break;
////
////            }
////        }
////    };
//
//    public void PassEcg() {
//        timer1 = new Timer();
//        timerTask1 = new TimerTask() {
//            @Override
//            public void run() {
//                try{
//                    if (OftenListData != null) {
//                        if (OftenListData.size() > 0) {
//                            float cooY = OftenListData.get(0);
//                            binding.activeWaveviewOne.showLine(cooY);
//                            OftenListData.remove(0);
//                        } else {
//                            //     binding.activeWaveviewOne.showLine(0f);
//                            return;
//                        }
//                    } else {
//                        return;
//                    }
//                }catch (Exception e){
//                    Log.d("EcgError",e.getMessage());
//                }
//
//            }
//        };
//        timer1.schedule(timerTask1, 1, 10);
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
//                        binding.activeTxtBoxygen.setCenterString("--");
//                        if (uploadData.getOxy_vaulestr().equals("127")) {
//                            binding.activeTxtO2State.setCenterString("检测中..");
//                        } else {
//                            binding.activeTxtO2State.setCenterString(uploadData.getOxy_vaulestr());
//                        }
//
//                    } else {
//                        binding.activeTxtBoxygen.setCenterString(uploadData.getOxy_vaulestr());
//                        binding.activeTxtO2State.setCenterString("");
//                    }
//                } else {
//                    binding.activeTxtBoxygen.setCenterString("--");
//                    binding.activeTxtO2State.setCenterString(uploadData.getBlood_oxy());
//                }
//                if (uploadData.getBlood().equals("已连接")) {
//                    if (uploadData.getHigh().equals("255") || uploadData.getLow().equals("255")) {
//                        binding.activeTxtHigh.setCenterString("0");
//                        binding.activeTxtLow.setCenterString("0");
//                        binding.activeTxtBloodstate1.setCenterString("测量错误");
//                        binding.activeTxtBloodstate2.setCenterString("测量错误");
//                    } else {
//                        if (LocalConfig.B_Diastole_Shrink.equals("0/0")) {
//                            LocalConfig.B_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
//                        } else {
//                            LocalConfig.L_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
//                        }
//                        binding.activeTxtHigh.setCenterString(uploadData.getHigh());
//                        binding.activeTxtLow.setCenterString(uploadData.getLow());
//                        if (!uploadData.getHigh().equals("0")) {
//                            LocalConfig.BloodHight = uploadData.getHigh();
//                            LocalConfig.BloodLow = uploadData.getLow();
//                        }
//
//                        binding.activeTxtBloodstate1.setCenterString("");
//                        binding.activeTxtBloodstate2.setCenterString("");
//                    }
//                } else {
////                    if (LocalConfig.B_Diastole_Shrink.equals("0/0")) {
//////                        binding.activeTxtHigh.setCenterString("--");
//////                        binding.activeTxtLow.setCenterString("--");
////                        binding.activeTxtHigh.setCenterString(LocalConfig.BloodHight);
////                        binding.activeTxtLow.setCenterString(LocalConfig.BloodLow);
////                    }
//                    binding.activeTxtHigh.setCenterString(LocalConfig.BloodHight);
//                    binding.activeTxtLow.setCenterString(LocalConfig.BloodLow);
//                    binding.activeTxtBloodstate1.setCenterString(uploadData.getBlood());
//                    binding.activeTxtBloodstate2.setCenterString(uploadData.getBlood());
//                    LocalConfig.B_Diastole_Shrink = "0/0";
//                    LocalConfig.L_Diastole_Shrink = "0/0";
//                }
//
//                if (uploadData.getECG().equals("已连接")) {
//                    binding.activeTxtEcgstate.setCenterString("");
//                } else {
//                    binding.activeTxtCoory.setCenterString("--");
//                    binding.activeTxtEcgstate.setCenterString(uploadData.getECG());
//                }
//                int speed = Integer.parseInt(uploadData.getSpeed());
//                binding.progressViewZhuansuActicve.setGraduatedEnabled(true);
//                binding.progressViewZhuansuActicve.setEndProgress(Float.parseFloat(GetProgress((float) speed, (float) 60)));
//                binding.progressViewZhuansuActicve.startProgressAnimation();
//                binding.activeTxtZhuansu.setCenterString(uploadData.getSpeed());
//
//                int left = Integer.parseInt(uploadData.getLeft());
//                binding.progressViewLeft.setGraduatedEnabled(true);
//                binding.progressViewLeft.setEndProgress(Float.parseFloat(GetProgress((float) left, (float) 50)));
//                binding.progressViewLeft.startProgressAnimation();
//                binding.activeTxtLeft.setCenterString(uploadData.getLeft());
//
//                int right = Integer.parseInt(uploadData.getRight());
//                binding.progressViewRight.setGraduatedEnabled(true);
//                binding.progressViewRight.setEndProgress(Float.parseFloat(GetProgress((float) right, (float) 50)));
//                binding.progressViewRight.startProgressAnimation();
//                binding.activeTxtRight.setCenterString(uploadData.getRight());
//
//                if (!uploadData.getSpasmState().equals("未知")) {
//                    LocalConfig.spasmCount = uploadData.getSpasmState();
//                }
//
//                break;
//
//            case 2:
//                try {
//                    ecgData = gson.fromJson(ObjectJson, EcgData.class);
//                    binding.activeTxtCoory.setCenterString(ecgData.getHeartrate());
//                    EcgListData = ecgData.getEcgCoorY();
//                    if (EcgListData == null) {
//                        binding.activeTxtEcgstate.setCenterString("心电仪佩戴异常！");
//                        OftenListData = new ArrayList<>();
//                        //   binding.activeWaveviewOne.showLine(0f);
//                        return;
//                    } else {
//                        // Float
//                        binding.activeTxtEcgstate.setCenterString("");
//                        if (repeat != EcgListData.get(0)) {
//                            repeat = EcgListData.get(0);
//                        } else {
//                            EcgListData = null;
//                            return;
//                        }
//                        for (int i = 0; i < EcgListData.size(); i++) {
//                            Float cooY = EcgListData.get(i);
//                            OftenListData.add(cooY);
////
////                            Message msg = Message.obtain();
////                            msg.what = 1;//Message类有属性字段arg1、arg2、what...
////                            Bundle bundle = new Bundle();
////                            bundle.putFloat("cooY", cooY);
////                            msg.setData(bundle);
////                            mHandler.sendMessage(msg);//sendMessage()用来传送Message类的值到mHandler
//                            //    binding.activeWaveviewOne.showLine(cooY);
//                            // OftenListData.add(cooY);
//                        }
//                        EcgListData = null;
////                        Message msg = Message.obtain();
////                        msg.what = mark;//Message类有属性字段arg1、arg2、what...
////                        Bundle bundle = new Bundle();
////                        bundle.putString("data", OftenListData);
////                        msg.setData(bundle);
////                        mHandler.sendMessage(msg);//sendMessage()用来传送Message类的值到mHandler
//
////                        for (int i = 0; i < OftenListData.size(); i++) {
////                            Float cooY = OftenListData.get(i);
////                            binding.activeWaveviewOne.showLine(cooY);
////                        }
////                        OftenListData.clear();
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(LocalConfig.TrainContext, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//            case 3:
//                ContorlState = ObjectJson;
//                if (ContorlState.equals("51")) {
//                    //   binding.activeTxtBlood.setCenterString("测量中");
//                } else if (ContorlState.equals("52")) {
//                    //     binding.activeTxtBlood.setCenterString("点击开始测量血压");
//                } else {
//                    //  binding.activeTxtBlood.setCenterString("点击开始测量血压");
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
//            recordDetailed.setActivtType("主动模式");
//            recordDetailed.setRecordTime(sim);
//            int zhuansu = Integer.parseInt(binding.activeTxtZhuansu.getCenterString());
//            recordDetailed.setSpeed(zhuansu);
//            int leftlimb = Integer.parseInt(binding.activeTxtLeft.getCenterString());
//            recordDetailed.setLeftLimb(leftlimb);
//            int rightlimb = Integer.parseInt(binding.activeTxtRight.getCenterString());
//            recordDetailed.setRightLimb(rightlimb);
//            int resistance = Integer.parseInt(binding.activeTxtResistance.getCenterString());
//            recordDetailed.setResistance(resistance);
//
//            int heartRate;
//            if (binding.activeTxtCoory.getCenterString().equals("--")) {
//                heartRate = 0;
//            } else {
//                heartRate = Integer.parseInt(binding.activeTxtCoory.getCenterString());
//            }
//            recordDetailed.setHeartRate(heartRate);
//
//            int Hbo2;
//            if (binding.activeTxtBoxygen.getCenterString().equals("--")) {
//                Hbo2 = 0;
//            } else {
//                Hbo2 = Integer.parseInt(binding.activeTxtBoxygen.getCenterString());
//            }
//            recordDetailed.setHbo2(Hbo2);
//
//            recordDetailed.setSpasm(0);
////            recordDetailed.setSpasmCount(0);
//            recordDetailedDao.insert(recordDetailed);
//            //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
//
//        } catch (Exception e) {
//            Toast.makeText(LocalConfig.TrainContext, "数据库异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    public void itinClick() {
//
//        resiDta = 1;
//        binding.progressViewResistance.setGraduatedEnabled(true);
//        binding.progressViewResistance.setEndProgress(Float.parseFloat(GetProgress((float) resiDta, (float) 12)));
//        binding.progressViewResistance.startProgressAnimation();
//        binding.activeTxtResistance.setCenterString(resiDta + "");
//
//        int left = 0;
//        binding.progressViewLeft.setGraduatedEnabled(true);
//        binding.progressViewLeft.setEndProgress(Float.parseFloat(GetProgress((float) left, (float) 50)));
//        binding.progressViewLeft.startProgressAnimation();
//        binding.activeTxtLeft.setCenterString("0");
//
//        int right = 0;
//        binding.progressViewRight.setGraduatedEnabled(true);
//        binding.progressViewRight.setEndProgress(Float.parseFloat(GetProgress((float) right, (float) 50)));
//        binding.progressViewRight.startProgressAnimation();
//        binding.activeTxtRight.setCenterString("0");
//
//        binding.activeImgBegin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HandlerMessage();
//            }
//        });
//
//        binding.activeImbtnJia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resiDta = resiDta + 1;
//                if (resiDta <= 12) {
//                    binding.progressViewResistance.setGraduatedEnabled(true);
//                    binding.progressViewResistance.setEndProgress(Float.parseFloat(GetProgress((float) resiDta, (float) 12)));
//                    binding.progressViewResistance.startProgressAnimation();
//                    binding.activeTxtResistance.setCenterString(resiDta + "");
//                    btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", isBegin));
//                } else {
//                    resiDta = 12;
//                    btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", isBegin));
//                    return;
//                }
//            }
//        });
//
//        binding.activeImbtnMove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resiDta = resiDta - 1;
//                if (resiDta < 1) {
//                    resiDta = 1;
//                    btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", isBegin));
//                    return;
//                } else {
//                    binding.progressViewResistance.setGraduatedEnabled(true);
//                    binding.progressViewResistance.setEndProgress(Float.parseFloat(GetProgress((float) resiDta, (float) 12)));
//                    binding.progressViewResistance.startProgressAnimation();
//                    binding.activeTxtResistance.setCenterString(resiDta + "");
//                    btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", isBegin));
//                }
//            }
//        });
//
//        binding.activeImgBlood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    if (uploadData != null && uploadData.getBlood().equals("已连接")) {
//                        if (ContorlState.equals("00") || ContorlState.equals("52")) {
//                            //  btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_BEGIN);
//                            btDataPro.sendBTMessage(GetCmdCode(resiDta, "51", isBegin));
//                        } else if (ContorlState.equals("51")) {
//                            // btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_END);
//                            btDataPro.sendBTMessage(GetCmdCode(resiDta, "52", isBegin));
//                            ContorlState = "52";
//                            binding.activeTxtBlood.setCenterString("点击开始测量血压");
//                        }
//                    } else {
//                        Toast.makeText(LocalConfig.TrainContext, "血压仪未连接，请检查设备", Toast.LENGTH_SHORT).show();
//                    }
//                    //  isBlood = true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public void HandlerMessage() {
//        try {
//            String txts = binding.activeTxtBegin.getCenterString();
//
//            if (txts.equals("开始")) {
//                isBegin = true;
//                btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", isBegin));
//                ActiveActivity.timeCountTool.startCount();
//                binding.activeTxtBegin.setCenterString("暂停");
//            } else {
//                isBegin = false;
//                btDataPro.sendBTMessage(GetCmdCode(resiDta, "50", isBegin));
//                ActiveActivity.timeCountTool.stopCount();
//                binding.activeTxtBegin.setCenterString("开始");
//            }
//        } catch (Exception e) {
//            Toast.makeText(LocalConfig.TrainContext, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private class DataThread implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                initLiveData();
//                LocalConfig.ActiviteType = "主动模式";
//                Log.d("TrainContext", "线程启动");
//                //    Toast.makeText(LocalConfig.TrainContext,"线程启动",Toast.LENGTH_SHORT).show();
//            } catch (Exception e) {
//                Log.d("DataThread", e.getMessage());
//            }
//        }
//    }
//
//    public void stop() {
//        if (dataThread != null || Athread != null) {
//            dataThread = null;
//            Athread = null;
//            // Toast.makeText(LocalConfig.TrainContext,"线程为null",Toast.LENGTH_SHORT).show();
//        }
//        OftenListData = new ArrayList<>();
//
//        LocalConfig.B_Diastole_Shrink = "0/0";
//
//        LocalConfig.L_Diastole_Shrink = "0/0";
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
//        LocalConfig.spasmCount = "0";
//        binding.activeWaveviewOne.resetCanavas();
//        if (btDataPro != null) {
//            btDataPro.sendBTMessage(GetCmdCode(1, "50", false));
//        }
//
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        stop();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
////        nowTime = 300000;
//        //  binding.activeTxtDowntimer.setCenterString(MyTimeUtils.formatTime(nowTime));
//    }
//}
