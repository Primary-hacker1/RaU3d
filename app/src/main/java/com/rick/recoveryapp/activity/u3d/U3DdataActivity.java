package com.rick.recoveryapp.activity.u3d;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.rick.recoveryapp.databinding.ActivityMainBinding;
import com.rick.recoveryapp.databinding.ActivityU3ddataBinding;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xui.utils.StatusBarUtils;

public class U3DdataActivity extends XPageActivity {

    ActivityU3ddataBinding binding;
    Context context;
    public  static  U3DdataActivity u3DdataActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ActivitytTest", "开始调用onCreate方法");

        binding = ActivityU3ddataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.translucent(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        u3DdataActivity=this;

        binding.u3dBtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
