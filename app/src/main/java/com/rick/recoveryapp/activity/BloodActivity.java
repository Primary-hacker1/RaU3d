package com.rick.recoveryapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.rick.recoveryapp.R;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.databinding.ActivityBloodBinding;
import com.rick.recoveryapp.databinding.ActivityLoginBinding;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

public class BloodActivity extends XPageActivity {

    SuperTextView blood_txt_title;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood);
        context = this;

        //设置界面全屏
        StatusBarUtils.translucent(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        blood_txt_title=findViewById(R.id.blood_txt_title);
        blood_txt_title.setCenterString("训练前，请测量血压");

    }

}
