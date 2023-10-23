package com.rick.recoveryapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rick.recoveryapp.activity.u3d.U3DActivity;
import com.rick.recoveryapp.activity.u3d.U3DDialogActivity;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.bluetooth.BtKeepService;
import com.rick.recoveryapp.databinding.ActivitySelectrolesBinding;
import com.rick.recoveryapp.utils.APKVersionInfoUtils;
import com.rick.recoveryapp.utils.LocalConfig;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;

public class SelectRolesActivity extends XPageActivity {

    ActivitySelectrolesBinding binding;
    Context context;
    BtDataPro btDataPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivitySelectrolesBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            StatusBarUtils.translucent(this);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context = this;

            initClick();
            LocalConfig.ip = "180.102.132.148";
            LocalConfig.sex = -1;
            btDataPro = new BtDataPro();

            binding.Box1.setEnabled(false);
            binding.Box2.setEnabled(false);
            binding.Box3.setEnabled(false);
            binding.Box4.setEnabled(false);

            binding.Box1.setChecked(false);
            binding.Box2.setChecked(false);
            binding.Box3.setChecked(false);
            binding.Box4.setChecked(false);
            //  CheckVersion();

        } catch (Exception e) {
            Log.d("error", e.getMessage());
            //     android.view.InflateException: Binary XML file line #19 in com.rick.recoveryapp:layout/xui_dialog_loading: Binary XML file line #19 in com.rick.recoveryapp:layout/xui_dialog_loading: Error inflating class <unknown>
        }
    }

    public void initClick() {
        binding.initTxtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, U3DDialogActivity.class);
                startActivity(in);
            }
        });

        binding.initTxtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LocalConfig.netWorkCheck(context)) {
                    Toast.makeText(context, "当前网络异常，请检查网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (LocalConfig.sex < 0) {
                    Toast.makeText(context, "请先选择角色，再进入情景模式", Toast.LENGTH_SHORT).show();
                } else {
//                    if (BaseApplication.mConnectService != null)
                    btDataPro = null;
                    AdminMainActivity.instance.finish();
                    Intent intent = new Intent(context, U3DActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("sex", LocalConfig.sex);
//                    bundle.putString("ip", LocalConfig.ip);
//                    bundle.putString("medicalNumber", LocalConfig.medicalNumber);
//                    bundle.putString("userName", LocalConfig.userName);
//                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
        });

        binding.initImgRole1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalConfig.sex >= 0 && LocalConfig.sex == 3) {
                    binding.Box1.setChecked(false);
                    LocalConfig.sex = -1;
                } else {
                    LocalConfig.sex = 3;
                    binding.Box1.setChecked(true);
                    binding.Box2.setChecked(false);
                    binding.Box3.setChecked(false);
                    binding.Box4.setChecked(false);
                }
            }
        });

        binding.initImgRole2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalConfig.sex >= 0 && LocalConfig.sex == 2) {
                    binding.Box2.setChecked(false);
                    LocalConfig.sex = -1;
                } else {
                    LocalConfig.sex = 2;
                    binding.Box1.setChecked(false);
                    binding.Box2.setChecked(true);
                    binding.Box3.setChecked(false);
                    binding.Box4.setChecked(false);
                }
            }
        });

        binding.initImgRole3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalConfig.sex >= 0 && LocalConfig.sex == 1) {
                    binding.Box3.setChecked(false);
                    LocalConfig.sex = -1;
                } else {
                    LocalConfig.sex = 1;
                    binding.Box1.setChecked(false);
                    binding.Box2.setChecked(false);
                    binding.Box3.setChecked(true);
                    binding.Box4.setChecked(false);
                }
            }
        });

        binding.initImgRole4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalConfig.sex >= 0 && LocalConfig.sex == 0) {
                    binding.Box4.setChecked(false);
                    LocalConfig.sex = -1;
                } else {
                    LocalConfig.sex = 0;
                    binding.Box1.setChecked(false);
                    binding.Box2.setChecked(false);
                    binding.Box3.setChecked(false);
                    binding.Box4.setChecked(true);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        btDataPro = null;
    }

    public void CheckVersion() {
//117.50.182.170/api/TestManagement/IsUpada?Version_name=1.0&Version_code=1

        // mMiniLoadingDialog.show();
        XHttp.get("/api/TestManagement/IsUpada?")
                .keepJson(true)
                .params("Version_name", APKVersionInfoUtils.getVersionName(this))
                .params("Version_code", APKVersionInfoUtils.getVersionCode(this))
                .execute(new SimpleCallBack<String>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {

//                            ResultDa redata = gson.fromJson(response, ResultDa.class);
//                            int code = redata.getCode();
//                            String res = redata.getResult();
//                            String message = redata.getMessage();
//                            if (res.equals("true")) {
//
////                                mMiniLoadingDialog.dismiss();
//                                //更新提示框
//                                XUpdate.newBuild(context)
//                                        .updateUrl(Constants.FORCED_UPDATE_URL)
//                                        .promptTopResId(R.drawable.update_top)
//                                        .promptThemeColor(ResUtils.getColor(R.color.Progress_bule))
//                                        .promptButtonTextColor(Color.WHITE)
//                                        //  .promptHeightRatio(1F)
//                                        .promptWidthRatio(0.7F)
//                                        .update();
////                                promptTopResId：设置顶部背景图片资源ID
////                                promptTopDrawable：设置顶部背景图片drawable
////                                promptTopBitmap：设置顶部背景图片位图
//
//                            } else if (res.equals("false")) {
//                                //mMiniLoadingDialog.dismiss();
//                                Toast.makeText(context, "已经是最新版本", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            //mMiniLoadingDialog.dismiss();
//                            Toast.makeText(context, "未获取到服务器信息，版本检测失败", Toast.LENGTH_SHORT).show();
//                        }
                        }
                    }

                    @Override
                    public void onError(ApiException e) {
                        // ToastUtils.toast("编辑失败：" + e.getMessage());
                        Log.d("HttpMessage", e.getMessage());
                    }
                });
    }
}
