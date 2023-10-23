package com.rick.recoveryapp.activity.u3d;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.recoveryapp.R;
import com.rick.recoveryapp.activity.AdminMainActivity;
import com.rick.recoveryapp.activity.DataResultsActivity;
import com.rick.recoveryapp.activity.SelectRolesActivity;
import com.rick.recoveryapp.activity.TestActivity;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.bluetooth.BtKeepService;
import com.rick.recoveryapp.chart.MyAVG;
import com.rick.recoveryapp.entity.ConnectData;
import com.rick.recoveryapp.entity.EcgData;
import com.rick.recoveryapp.entity.Ondata;
import com.rick.recoveryapp.entity.protocol.PoolMessage;
import com.rick.recoveryapp.entity.protocol.UploadData;
import com.rick.recoveryapp.greendao.ActivitRecordDao;
import com.rick.recoveryapp.greendao.RecordDetailedDao;
import com.rick.recoveryapp.greendao.entity.ActivitRecord;
import com.rick.recoveryapp.greendao.entity.RecordDetailed;
import com.rick.recoveryapp.utils.ActiveTimeTool;
import com.rick.recoveryapp.utils.CRC16Util;
import com.rick.recoveryapp.utils.DateUtil;
import com.rick.recoveryapp.utils.LocalConfig;
import com.rick.recoveryapp.utils.TimeCountTool;
import com.rick.recoveryapp.utils.view.WaveShowView;
import com.unity3d.player.UnityPlayer;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.button.shadowbutton.ShadowButton;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.progress.CircleProgressView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class U3DActivity extends UnityPlayerActivity {

    Context context;
    UploadData uploadData;
    Ondata ondata;
    ArrayList<Float> EcgListData;
    EcgData ecgData;
    RecordDetailedDao recordDetailedDao;
    ActivitRecordDao activitRecordDao;
    String ContorlState = "52";
    LinearLayout u3d_linear_data, unity_linlayout;
    WaveShowView u3d_waveview_one;
    private int zhuansu = 5, zuozhi = 1, youzhi = 0, shuzhangya = 0, shousuoya = 0, xueyang = 0,
            resistance = 1, jingluan = 1, xinlv = 0;
    int tempZhuansu = 0;
    // 已连接设备的名字
    static ArrayList<Float> OftenListData;
    static Timer timer1, timer2;
    static TimerTask timerTask1, timerTask2;
    Boolean isBlood = false;
    boolean isBegin = false;
    double Total_mileage, Calories;
    public TimeCountTool timeCountTool;
    public ActiveTimeTool activeTimeTool;
    boolean isOk = false;
    public static U3DActivity u3dinstance;

    CircleProgressView u3d_progress_zhuansu, u3d_progress_zhuli, u3d_progress_spasm, u3d_progress_left, u3d_progress_right;
    SuperTextView u3d_txt_zhuansu, u3d_txt_spasm, u3d_txt_left, u3d_txt_right, u3d_txt_resistance, u3d_txt_high, u3d_txt_low,
            u3d_txt_coory, u3d_txt_boxygen, u3d_txt_bloodstate2, u3d_txt_bloodstate1, u3d_txt_ecgstate, u3d_txt_o2State, u3d_txt_begin;
    ShadowButton u3d_jia_zhuansu, u3d_jian_zhuansu, u3d_jia_resistance,
            u3d_jian_resistance, u3d_jia_spasm, u3d_jian_spasm, u3d_img_blood, u3d_img_begin, u3d_btn_return;

    String u3d_B_Diastole_Shrink = "0/0", u3d_L_Diastole_Shrink = "0/0";
    int spasmCount = 0;
    String timecount = "";
    int BloodEndState = 0; // 0:初始状态  1：需要测量血压   2：血压测量完成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unity);
        unity_linlayout = findViewById(R.id.unity_linlayout);
        unity_linlayout.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        context = this;
        u3dinstance = this;

        if (LocalConfig.daoSession == null) {
            BaseApplication myApp = (BaseApplication) getApplication();
            LocalConfig.daoSession = myApp.getDaoSession();
        }
        activitRecordDao = LocalConfig.daoSession.getActivitRecordDao();
        recordDetailedDao = LocalConfig.daoSession.getRecordDetailedDao();

        u3d_waveview_one = findViewById(R.id.u3d_waveview_one);
        u3d_linear_data = findViewById(R.id.u3d_linear_data);
        //  u3d_linear_dataresults = findViewById(R.id.u3d_linear_dataresults);

        u3d_progress_zhuansu = findViewById(R.id.u3d_progress_zhuansu);
        u3d_progress_zhuli = findViewById(R.id.u3d_progress_zhuli);

        u3d_progress_spasm = findViewById(R.id.u3d_progress_spasm);
        u3d_progress_left = findViewById(R.id.u3d_progress_left);
        u3d_progress_right = findViewById(R.id.u3d_progress_right);

        u3d_txt_zhuansu = findViewById(R.id.u3d_txt_zhuansu);
        u3d_txt_spasm = findViewById(R.id.u3d_txt_spasm);
        u3d_txt_left = findViewById(R.id.u3d_txt_left);
        u3d_txt_right = findViewById(R.id.u3d_txt_right);
        u3d_txt_resistance = findViewById(R.id.u3d_txt_resistance);
        u3d_txt_begin = findViewById(R.id.u3d_txt_begin);

        u3d_txt_high = findViewById(R.id.u3d_txt_high);
        u3d_txt_low = findViewById(R.id.u3d_txt_low);
        u3d_txt_coory = findViewById(R.id.u3d_txt_coory);
        u3d_txt_boxygen = findViewById(R.id.u3d_txt_boxygen);

        u3d_txt_bloodstate2 = findViewById(R.id.u3d_txt_bloodstate2);
        u3d_txt_ecgstate = findViewById(R.id.u3d_txt_ecgstate);
        u3d_txt_bloodstate1 = findViewById(R.id.u3d_txt_bloodstate1);
        u3d_txt_o2State = findViewById(R.id.u3d_txt_o2State);

        u3d_jia_zhuansu = findViewById(R.id.u3d_jia_zhuansu);
        u3d_jian_zhuansu = findViewById(R.id.u3d_jian_zhuansu);
        u3d_jia_resistance = findViewById(R.id.u3d_jia_resistance);
        u3d_jian_resistance = findViewById(R.id.u3d_jian_resistance);
        u3d_jia_spasm = findViewById(R.id.u3d_jia_spasm);
        u3d_jian_spasm = findViewById(R.id.u3d_jian_spasm);
        u3d_img_blood = findViewById(R.id.u3d_img_blood);
        u3d_img_begin = findViewById(R.id.u3d_img_begin);
        u3d_btn_return = findViewById(R.id.u3d_btn_return);
        u3d_linear_data.setVisibility(View.GONE);
        //  u3d_btn_return.setVisibility(View.GONE);
        Intent intent = new Intent(context, BtKeepService.class);
        startService(intent);

        OftenListData = new ArrayList<>();
        ondata = new Ondata();
        EcgListData = new ArrayList<>();
        initClick();
        try {
            //U3D开始连接接口

//            Bundle bundle = getIntent().getExtras();
//            int sex = bundle.getInt("sex");
//            String ip = bundle.getString("ip");
//            String medicalNumber = bundle.getString("medicalNumber");
//            String userName = bundle.getString("userName");

            U3DFactory.Connect();
            u3d_waveview_one.resetCanavas();
            OftenListData.clear();
            OftenListData = new ArrayList<>();
            U3DFactory.btDataPro.sendBTMessage(U3DFactory.btDataPro.CONNECT_SEND);
            //  isBegin = true;
            U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "53", false, 5, 1));
            //获取蓝牙数据
            GetBtData();
            initView();
            //处理心电数据
            PassEcg();
            UnityPlayer.UnitySendMessage("GameMenue", "OnAndPause", "");
        } catch (Exception ex) {
            Log.d("U3D", ex.getMessage());
        }
    }
// Do others

    //  单人或多人的开始
    public void BeginAnima() {

        timeCountTool = TimeCountTool.getInstance();
        activeTimeTool = ActiveTimeTool.getInstance();
        UnityPlayer.UnitySendMessage("GameMenue", "OnAndStart", "");
        UnityPlayer.UnitySendMessage("GameMenue", "onDuorenStart", "");
        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", true, zhuansu, jingluan));
        U3DActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                u3d_linear_data.setVisibility(View.GONE);
            }
        });
        u3d_img_begin.setBackground(getResources().getDrawable(R.drawable.stop));
        u3d_txt_begin.setCenterString("停  止");
        timeCountTool.startCount();
    }

    public void initClick() {

        u3d_btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BloodEndState == 1) {
                    //取消测量运动后血压
                    BloodEndState = 2;
                } else if (BloodEndState == 0) {
                    DialogLoader.getInstance().showConfirmDialog(
                            context,
                            getString(R.string.active_return),
                            getString(R.string.lab_yes),
                            (dialog, which) -> {
                                dialog.dismiss();
                                if (isBegin) {
                                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, 5, 1));
                                } else {
                                    U3DActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            u3d_linear_data.setVisibility(View.GONE);
                                        }
                                    });
                                    UnityPlayer.UnitySendMessage("GameMenue", "OnAndStop", "");
                                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(1, "53", false, 5, 1));
                                }
                                initView();
                            },
                            getString(R.string.lab_no),
                            (dialog, which) -> {
                                dialog.dismiss();
                            }
                    );
                }
            }
        });

        //开始按钮点击事件
        u3d_img_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (BloodEndState == 1) {
                        Toast.makeText(context, "还未测量运动后血压！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String txts = u3d_txt_begin.getCenterString();
                    if (txts.equals("开  始")) {
                        String highblood = u3d_txt_high.getCenterString();
                        String lowblood = u3d_txt_low.getCenterString();
                        if (highblood.equals("0") && lowblood.equals("0")) {
                            DialogLoader.getInstance().showConfirmDialog(
                                    context,
                                    getString(R.string.active_blood),
                                    getString(R.string.lab_ok),
                                    (dialog, which) -> {
                                        dialog.dismiss();
                                        BeginAnima();
                                    },
                                    getString(R.string.lab_cancel),
                                    (dialog, which) -> {
                                        dialog.dismiss();
                                        // timeTask.start();
                                    }
                            );
                        } else {
                            BeginAnima();
                        }
                    } else {
                        // UnityPlayer.UnitySendMessage("GameMenue", "OnAndPause", "");
                        u3d_img_begin.setBackground(getResources().getDrawable(R.drawable.begin));
                        u3d_txt_begin.setCenterString("开  始");
                        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });

        //血压测量按钮
        u3d_img_blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isBegin) {
                        Toast.makeText(context, "运动中，请勿测量血压！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (uploadData != null && uploadData.getBlood().equals("已连接")) {
                        if (ContorlState.equals("00") || ContorlState.equals("52")) {
                            U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "51", false, zhuansu, jingluan));
                        } else if (ContorlState.equals("51")) {
                            //    sendBTMessage(btDataPro.CONTORL_CODE_END);
                            //btDataPro.sendBTMessage(btDataPro.CONTORL_CODE_END);
                            U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "52", false, zhuansu, jingluan));
                            ContorlState = "52";
                        }
                    } else {
                        Toast.makeText(context, "血压仪未连接，请检查设备", Toast.LENGTH_SHORT).show();
                    }
                    isBlood = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        u3d_jia_zhuansu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (zhuansu + 1 <= 60) {
                    if (zhuansu + 1 >= 30) {
                        if (isOk) {
                            zhuansu = zhuansu + 1;
                            u3d_progress_zhuansu.setGraduatedEnabled(true);
                            u3d_txt_zhuansu.setCenterString(zhuansu + "");
                        } else {
                            DialogLoader.getInstance().showConfirmDialog(
                                    context,
                                    getString(R.string.tip_permission),
                                    getString(R.string.lab_ok),
                                    (dialog, which) -> {
                                        dialog.dismiss();
                                        isOk = true;
                                        zhuansu = zhuansu + 1;
                                        u3d_progress_zhuansu.setGraduatedEnabled(true);
                                        u3d_txt_zhuansu.setCenterString(zhuansu + "");
                                        //    btDataPro.sendBTMessage(GetCmdCode(resistance, "50", false, zhuansuData, spasm));
                                    },
                                    getString(R.string.lab_cancel),
                                    (dialog, which) -> {
                                        dialog.dismiss();
                                        isOk = false;
                                    }
                            );
                        }
                    } else {
                        zhuansu = zhuansu + 1;
                        u3d_progress_zhuansu.setGraduatedEnabled(true);
                        u3d_txt_zhuansu.setCenterString(zhuansu + "");
                    }
                    u3d_progress_zhuansu.setGraduatedEnabled(true);
                    u3d_txt_zhuansu.setCenterString(zhuansu + "");
                } else {
                    zhuansu = 60;
                    return;
                }
            }
        });

        u3d_jian_zhuansu.setOnClickListener(new View.OnClickListener() {
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
                    // U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                    return;
                } else {
                    u3d_progress_zhuansu.setGraduatedEnabled(true);
                    // u3d_progress_zhuansu.setEndProgress(Float.parseFloat(GetProgress((float) zhuansu, (float) 60)));
                    //   u3d_progress_zhuansu.startProgressAnimation();
                    u3d_txt_zhuansu.setCenterString(zhuansu + "");
                    //   U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingl uan));
                }
            }
        });

        u3d_jia_resistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                resistance = resistance + 1;
                if (resistance <= 12) {
                    u3d_progress_zhuli.setGraduatedEnabled(true);
                    //  u3d_progress_zhuli.setEndProgress(Float.parseFloat(GetProgress((float) resistance, (float) 12)));
                    //  u3d_progress_zhuli.startProgressAnimation();
                    u3d_txt_resistance.setCenterString(resistance + "");
                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                } else {
                    resistance = 12;
                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                    return;
                }
            }
        });

        u3d_jian_resistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                resistance = resistance - 1;
                if (resistance < 1) {
                    resistance = 1;
                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                    return;
                } else {
                    u3d_progress_zhuli.setGraduatedEnabled(true);
                    // u3d_progress_zhuli.setEndProgress(Float.parseFloat(GetProgress((float) resistance, (float) 12)));
                    //  u3d_progress_zhuli.startProgressAnimation();
                    u3d_txt_resistance.setCenterString(resistance + "");
                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                }
            }
        });

        u3d_jia_spasm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                jingluan = jingluan + 1;
                if (jingluan <= 12) {
                    u3d_progress_spasm.setGraduatedEnabled(true);
                    // u3d_progress_spasm.setEndProgress(Float.parseFloat(GetProgress((float) jingluan, (float) 12)));
                    // u3d_progress_spasm.startProgressAnimation();
                    u3d_txt_spasm.setCenterString(jingluan + "");
                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                } else {
                    jingluan = 12;
                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                    return;
                }
            }
        });

        u3d_jian_spasm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBegin) {
                    Toast.makeText(context, "运动中，请勿调节参数！", Toast.LENGTH_SHORT).show();
                    return;
                }
                jingluan = jingluan - 1;
                if (jingluan < 1) {
                    jingluan = 1;
                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                    return;
                } else {
                    u3d_progress_spasm.setGraduatedEnabled(true);
                    // u3d_progress_spasm.setEndProgress(Float.parseFloat(GetProgress((float) jingluan, (float) 12)));
                    //   u3d_progress_spasm.startProgressAnimation();
                    u3d_txt_spasm.setCenterString(jingluan + "");
                    U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
                }
            }
        });
    }

    public void initLiveData(PoolMessage poolMessage, int mark) {
        if (poolMessage != null && poolMessage.isState()) {
            Message msg = Message.obtain();
            msg.what = mark;//Message类有属性字段arg1、arg2、what...
            Bundle bundle = new Bundle();
            bundle.putString("data", poolMessage.getObjectJson());
            msg.setData(bundle);
            mHandler.sendMessage(msg);//sendMessage()用来传送Message类的值到mHandler
            //   DataDisplay(mark, poolMessage.getObjectJson());
//            SendData();
        } else {
            Log.d("DataError", "数据异常");
            // Toast.makeText(context, "数据异常", Toast.LENGTH_SHORT).show();
        }
    }

    Handler mHandler = new Handler() {

        //handleMessage为处理消息的方法
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String datas = bundle.getString("data");
            DataDisplay(msg.what, datas);
            if (isBegin) {
                UpdatProgress();
            }

            SendData();
        }
    };

    public void PassEcg() {
        timer1 = new Timer();
        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (OftenListData != null) {
                        if (OftenListData.size() > 0) {
                            Float cooY = OftenListData.get(0);
                            u3d_waveview_one.showLine(cooY);
                            OftenListData.remove(0);
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    Log.d("EcgError", e.getMessage());
                }
            }
        };
        timer1.schedule(timerTask1, 1, 10);
    }

    public void GetBtData() {
        timer2 = new Timer();
        timerTask2 = new TimerTask() {
            @Override
            public void run() {
                initLiveData(LocalConfig.poolMessage, 1);
                initLiveData(LocalConfig.poolMessage1, 2);
            }
        };
        timer2.schedule(timerTask2, 1, 500);
    }

    public void DataDisplay(int mark, String ObjectJson) {

        switch (mark) {
            case 1:
                uploadData = U3DFactory.gson.fromJson(ObjectJson, UploadData.class);
                if (isBegin) {
                    jingluan = Integer.parseInt(uploadData.getSTspasm());
                    u3d_progress_spasm.setGraduatedEnabled(true);
                    //u3d_progress_spasm.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) jingluan, (float) 12)));
                    //  binding.progressViewSpasm.startProgressAnimation();
                    u3d_txt_spasm.setCenterString(uploadData.getSTspasm());

                    zhuansu = Integer.parseInt(uploadData.getSTspeed());
                    u3d_progress_zhuansu.setGraduatedEnabled(true);
                    // u3d_progress_zhuansu.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) zhuansu, (float) 60)));
                    //   binding.progressViewZhuansuIntelligence.startProgressAnimation();
                    u3d_txt_zhuansu.setCenterString(uploadData.getSTspeed());

                    resistance = Integer.parseInt(uploadData.getSTresistance());
                    u3d_progress_zhuli.setGraduatedEnabled(true);
                    // u3d_progress_zhuli.setEndProgress(Float.parseFloat(LocalConfig.GetProgress((float) resistance, (float) 12)));
                    //  binding.progressViewResistance.startProgressAnimation();
                    u3d_txt_resistance.setCenterString(uploadData.getSTresistance());
                }

                if (uploadData.getBlood_oxy().equals("已连接")) {
                    if (uploadData.getOxy_vaulestr().equals("手指未插入")
                            || uploadData.getOxy_vaulestr().equals("探头脱落")
                            || uploadData.getOxy_vaulestr().equals("127")) {
                        if (uploadData.getOxy_vaulestr().equals("127")) {
                            u3d_txt_o2State.setCenterString("检测中...");
                        } else {
                            u3d_txt_o2State.setCenterString(uploadData.getBlood_oxy());
                        }
                        //血氧
                        xueyang = 0;
                    } else {
                        xueyang = Integer.valueOf(uploadData.getOxy_vaulestr());
                        u3d_txt_o2State.setCenterString("");
                    }
                } else {
                    xueyang = 0;
                    u3d_txt_o2State.setCenterString(uploadData.getBlood_oxy());
                }
                u3d_txt_boxygen.setCenterString(xueyang + "");

                if (uploadData.getBlood().equals("已连接")) {
                    if (uploadData.getHigh().equals("255") || uploadData.getLow().equals("255")) {
                        u3d_txt_high.setCenterString("0");
                        u3d_txt_low.setCenterString("0");
                        u3d_txt_bloodstate2.setCenterString("测量错误");
                        u3d_txt_bloodstate1.setCenterString("测量错误");
                    } else {
                        if (BloodEndState == 1) {
                            //运动后血压
                            u3d_L_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
                            if (!u3d_B_Diastole_Shrink.equals(u3d_L_Diastole_Shrink)) {
                                BloodEndState = 2;
                                Toast.makeText(context, "运动后血压测量已完成！", Toast.LENGTH_SHORT).show();
                            }
                        } else if (BloodEndState == 0) {
                            //运动前血压
                            u3d_B_Diastole_Shrink = uploadData.getLow() + "/" + uploadData.getHigh();
                        }
                        shuzhangya = Integer.valueOf(uploadData.getLow());
                        shousuoya = Integer.valueOf(uploadData.getHigh());
                        u3d_txt_bloodstate2.setCenterString("");
                        u3d_txt_bloodstate1.setCenterString("");
                        u3d_txt_high.setCenterString(shousuoya + "");
                        u3d_txt_low.setCenterString(shuzhangya + "");
                    }
                } else {

                    u3d_txt_bloodstate2.setCenterString(uploadData.getBlood());
                    u3d_txt_bloodstate1.setCenterString(uploadData.getBlood());
                    u3d_txt_high.setCenterString(shuzhangya + "");
                    u3d_txt_low.setCenterString(shousuoya + "");
                }

                if (uploadData.getECG().equals("已连接")) {
                    u3d_txt_ecgstate.setCenterString("");
                } else {
                    u3d_txt_coory.setCenterString("0");
                    u3d_txt_ecgstate.setCenterString(uploadData.getECG());
                }
                try {
                    zuozhi = Integer.valueOf(uploadData.getLeft());
                    u3d_progress_left.setGraduatedEnabled(true);
                    u3d_txt_left.setCenterString(uploadData.getLeft());

                    youzhi = Integer.valueOf(uploadData.getRight());
                    u3d_progress_right.setGraduatedEnabled(true);
                    u3d_txt_right.setCenterString(uploadData.getRight());

                    if (uploadData.getActiveState().equals("停机状态")) {
                        if (isBegin) {
                            isBegin = false;
                            UnityPlayer.UnitySendMessage("GameMenue", "OnAndPause", "");
                            LocalConfig.Model = "U3D";
                            u3d_img_begin.setBackground(getResources().getDrawable(R.drawable.begin));
                            u3d_txt_begin.setCenterString("开  始");

                            timecount = timeCountTool.stopCount();
                            activeTimeTool.stopCount();
                            getCalories_mileage();

                            if (uploadData.getSpasmState() != -1) {
                                if (spasmCount == 5) {
                                    DialogLoader.getInstance().showConfirmDialog(
                                            context,
                                            getString(R.string.active_out),
                                            getString(R.string.lab_yes),
                                            (dialog, which) -> {
                                                BloodEndState = 1;
                                                if (uploadData != null && uploadData.getBlood().equals("已连接")) {
                                                    if (ContorlState.equals("00") || ContorlState.equals("52")) {
                                                        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "51", false, zhuansu, jingluan));
                                                    } else if (ContorlState.equals("51")) {
                                                        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "52", false, zhuansu, jingluan));
                                                        ContorlState = "52";
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
                                                        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "51", false, zhuansu, jingluan));
                                                    } else if (ContorlState.equals("51")) {
                                                        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "52", false, zhuansu, jingluan));
                                                        ContorlState = "52";
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
                        u3d_img_begin.setBackground(getResources().getDrawable(R.drawable.stop));
                        u3d_txt_begin.setCenterString("停  止");
                        timeCountTool.startCount();
                        String ActiveType = uploadData.getActiveType();
                        if (ActiveType.equals("智能模式/主动")) {
                            activeTimeTool.startCount();
                        } else if (ActiveType.equals("智能模式/被动")) {
                            activeTimeTool.stopCount();
                        } else {
                            activeTimeTool.stopCount();
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

                        UnityPlayer.UnitySendMessage("GameMenue", "OnAndStop", "");
                        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(1, "53", false, 5, 1));
                        U3DActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                u3d_linear_data.setVisibility(View.GONE);
                                //  u3d_btn_return.setVisibility(View.GONE);
                            }
                        });
                        TimeCountTool.setClean();
                        ActiveTimeTool.setClean();

                        initView();
//

                        Intent in = new Intent(context, DataResultsActivity.class);
                        startActivity(in);
                    }

                } catch (Exception e) {
                    e.getMessage();
                }
                break;

            case 2:
                try {
                    ecgData = U3DFactory.gson.fromJson(ObjectJson, EcgData.class);
                    xinlv = Integer.valueOf(ecgData.getHeartrate());
                    u3d_txt_coory.setCenterString(xinlv + "");
                    EcgListData = ecgData.getEcgCoorY();
                    if (EcgListData == null) {
                        u3d_txt_ecgstate.setCenterString("心电仪佩戴异常！");
                        OftenListData = new ArrayList<>();
                        //    u3d_waveview_one.showLine(0f);
                    } else {
                        // Float
                        u3d_txt_ecgstate.setCenterString("");
                        Log.d("EcgListData", LocalConfig.CoorYList.size() + "");
                        for (int i = 0; i < LocalConfig.CoorYList.size(); i++) {
                            Float cooY = LocalConfig.CoorYList.get(i);
                            OftenListData.add(cooY);
                        }
                        LocalConfig.CoorYList = new ArrayList<>();
                    }
                } catch (Exception e) {
                    Toast.makeText(U3DActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                ContorlState = ObjectJson;
                if (ContorlState.equals("51")) {
                    //binding.activeTxtBlood.setCenterString("测量中");
                    Toast.makeText(context, "测量中...", Toast.LENGTH_SHORT).show();
                } else if (ContorlState.equals("52")) {
                }
                break;
        }
    }

    private void initView() {
        zhuansu = 5;
        zuozhi = 0;
        youzhi = 0;
        shuzhangya = 0;
        shousuoya = 0;
        xueyang = 0;
        resistance = 1;
        jingluan = 1;
        xinlv = 0;

        u3d_txt_zhuansu.setCenterString(zhuansu + "");
        u3d_txt_spasm.setCenterString(jingluan + "");
        u3d_txt_left.setCenterString(zuozhi + "");
        u3d_txt_right.setCenterString(youzhi + "");
        u3d_txt_resistance.setCenterString(resistance + "");
        u3d_txt_high.setCenterString(shuzhangya + "");
        u3d_txt_low.setCenterString(shousuoya + "");
        u3d_txt_coory.setCenterString(xinlv + "");
        u3d_txt_boxygen.setCenterString(xueyang + "");
    }


    public void SendData() {
        Random random = new Random();
        ondata = new Ondata();

        ondata.setZhuansu(isBegin ? zhuansu + "" : tempZhuansu + "");
        ondata.setZuozhi(zuozhi + "");
        ondata.setYouzhi(youzhi + "");

        ondata.setXinlv(xinlv + "");
        ondata.setXinlv1(xinlv + "");
        ondata.setXinlv2(xinlv + "");
        ondata.setXinlv3(xinlv + "");
        ondata.setXinlv4(xinlv + "");

        ondata.setShuzhangya(shuzhangya + "");
        ondata.setShousuoya(shousuoya + "");
        ondata.setXueyang(xueyang + "");
        ondata.setKaluli(String.valueOf(random.nextInt(51)));
        ondata.setLicheng(String.valueOf(random.nextInt(51)));
        ondata.setStrength(String.valueOf(jingluan));
        // ondata.setZuli(String.valueOf(resistance));
        ondata.setJingluanCount(spasmCount + "");
        UnityPlayer.UnitySendMessage("DataManager", "OnZuli", String.valueOf(resistance));
        UnityPlayer.UnitySendMessage("DataManager", "Ondata", U3DFactory.Analysis(ondata));
    }
    //InvalidOperationException: Collection was modified; enumeration operation may not execute

    public String onTimeOut() {
        UnityPlayer.UnitySendMessage("GameMenue", "OnAndPause", "");

        U3DActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                u3d_linear_data.setVisibility(View.VISIBLE);
            }
        });
        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(1, "50", false, 5, 1));
        return "倒计时结束";
    }

    public String onExit() {
        U3DActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                u3d_linear_data.setVisibility(View.GONE);
            }
        });
        // u3d_btn_return.setVisibility(View.GONE);
        stop();
        LocalConfig.UserID = 0;
        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
        U3DFactory.btDataPro.sendBTMessage(U3DFactory.btDataPro.CONNECT_CLOSE);
        // SelectRolesActivity.seleinstance.finish();
        Intent intent = new Intent(context, AdminMainActivity.class);
        startActivity(intent);
        //  finish();
        return "退出程序";
    }

    public String onGameOver() {

        //   u3d_linear_data.setVisibility(View.GONE);
        // u3d_btn_return.setVisibility(View.GONE);
        u3d_waveview_one.resetCanavas();
        OftenListData.clear();
        OftenListData = new ArrayList<>();
        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "50", false, zhuansu, jingluan));
        return "归零";
    }

    public String onStartGame() {
        OftenListData = new ArrayList<>();
        if (timer2 == null && timerTask2 == null) {
            GetBtData();
        }

        if (timer1 == null && timerTask1 == null) {
            PassEcg();
        }

        //显示操作面板
        U3DActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                u3d_linear_data.setVisibility(View.VISIBLE);
                //  u3d_btn_return.setVisibility(View.VISIBLE);
            }
        });
        return "训练恢复";
    }

    public String onDataHide() {
        U3DActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                u3d_linear_data.setVisibility(View.GONE);
            }
        });
        return "隐藏";
    }

    public String onDataShow() {
        U3DActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                u3d_linear_data.setVisibility(View.VISIBLE);
            }
        });
        return "显示";
    }

//    //弹出框点击确定，调用OnHintOK
//    public String onHintOK() {
//        u3d_linear_data.setVisibility(View.GONE);
//        u3d_waveview_one.resetCanavas();
//        OftenListData = new ArrayList<>();
//        // btDataPro.sendBTMessage(GetCmdCode(resistance, "50", false, zhuansu, jingluan));
//        U3DFactory. btDataPro.sendBTMessage(U3DFactory.GetCmdCode(1, "53", false, 5, 1));
//        LocalConfig.Model = "U3D";
//        Intent in = new Intent(context, DataResultsActivity.class);
//        startActivity(in);
//        //计算平均转速，阻力档位，获取阻力值
//        //计算完成清除数据库表数据
//        return "确认退出";
//    }

//    //点击暂停训练时，调用OnHint
//    public String onHint() {
//
//        U3DFactory.btDataPro.sendBTMessage(U3DFactory.GetCmdCode(resistance, "53", false, 5, 1));
////        //发送暂停指令，通知下位机暂停
//        U3DActivity.this.runOnUiThread(new Runnable() {
//            public void run() {
//                u3d_linear_data.setVisibility(View.GONE);
//            }
//        });
//
//
//        return "暂停";
//    }

    public void stop() {

        if (timer1 != null) {
            timer1.cancel();
            timer1.purge();
            timer1 = null;
        }

        if (timerTask1 != null) {
            timerTask1.cancel();
            timerTask1 = null;
        }

        if (timer2 != null) {
            timer2.cancel();
            timer2.purge();
            timer2 = null;
        }

        if (timerTask2 != null) {
            timerTask2.cancel();
            timerTask2 = null;
        }
    }

    public void SaveRecord() {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sim = dateFormat.format(date);
        activeTimeTool.stopCount();

        int SurplusTime = timeCountTool.getTime() - activeTimeTool.getTime();

        String Pduration = activeTimeTool.SurplusTi(SurplusTime);

        String Aduration = activeTimeTool.SurplusTi(activeTimeTool.getTime());

        ActivitRecord activitRecord = new ActivitRecord();
        activitRecord.setRecordID(LocalConfig.UserID);//20230523103215
        activitRecord.setUserName(LocalConfig.userName);
        activitRecord.setUserNumber(LocalConfig.medicalNumber);
        activitRecord.setRecordTime(sim);
        activitRecord.setLongTime(timecount);
        activitRecord.setAduration(Aduration);
        activitRecord.setPduration(Pduration);
        activitRecord.setActivtType(3 + "");
        activitRecord.setB_Diastole_Shrink(u3d_B_Diastole_Shrink);
        activitRecord.setL_Diastole_Shrink(u3d_L_Diastole_Shrink);
        //使用String.format()格式化(四舍五入)
        activitRecord.setTotal_mileage(String.format("%.2f", Total_mileage));
        activitRecord.setCalories(String.format("%.2f", Calories));
        activitRecord.setSpasmCount(spasmCount + "");
        activitRecordDao.insert(activitRecord);

        MyAVG myAVG = new MyAVG();
        myAVG.GetAvg(LocalConfig.UserID + "");

        u3d_B_Diastole_Shrink = "0/0";
        u3d_L_Diastole_Shrink = "0/0";
        timeCountTool.setTime(0);
        spasmCount = 0;
    }

    /**
     * 模拟源源不断的数据源
     */
    public void UpdatProgress() {
        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String sim = dateFormat.format(date);

            RecordDetailed recordDetailed = new RecordDetailed();
            if (LocalConfig.UserID == 0) {
                LocalConfig.UserID = Long.valueOf(dateFormat1.format(date)).longValue();
            }//20230523103215
            recordDetailed.setRecordID(LocalConfig.UserID);
            recordDetailed.setActivtType("情景模式");
            recordDetailed.setRecordTime(sim);
            int zhuansu = Integer.parseInt(u3d_txt_zhuansu.getCenterString());
            recordDetailed.setSpeed(zhuansu);
            int leftlimb = Integer.parseInt(u3d_txt_left.getCenterString());
            recordDetailed.setLeftLimb(leftlimb);
            int rightlimb = Integer.parseInt(u3d_txt_right.getCenterString());
            recordDetailed.setRightLimb(rightlimb);
            int resistance = Integer.parseInt(u3d_txt_resistance.getCenterString());
            recordDetailed.setResistance(resistance);

            int heartRate;
            if (u3d_txt_coory.getCenterString().equals("--")) {
                heartRate = 0;
            } else {
                heartRate = Integer.parseInt(u3d_txt_coory.getCenterString());
            }
            recordDetailed.setHeartRate(heartRate);

            int Hbo2;
            if (u3d_txt_boxygen.getCenterString().equals("--")) {
                Hbo2 = 0;
            } else {
                Hbo2 = Integer.parseInt(u3d_txt_boxygen.getCenterString());
            }
            recordDetailed.setHbo2(Hbo2);

            recordDetailed.setSpasm(jingluan);
            //  recordDetailed.setSpasmCount(0);
            recordDetailedDao.insert(recordDetailed);
            //500表示调用schedule方法后等待500ms后调用run方法，50表示以后调用run方法的时间间隔
        } catch (Exception e) {
            Toast.makeText(context, "数据库异常" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
//                Intent intent = new Intent(context, AdminMainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
                //  mUnityPlayer.quit();
            } catch (Exception ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
