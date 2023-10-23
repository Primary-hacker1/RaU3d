package com.rick.recoveryapp.activity;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;


import com.rick.recoveryapp.R;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.bluetooth.KeepLifeService;
import com.rick.recoveryapp.databinding.ActivityWelcomeBinding;
import com.xuexiang.xui.utils.StatusBarUtils;
import com.xuexiang.xutil.common.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;

public class WelcomeActivity extends XPageActivity {

    Context context;
    ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        StatusBarUtils.translucent(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initBackground();
        startAnimation();//开启动画效果
    }

    private void initBackground() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK) - 1;
        Log.e("!!!!!!!!", day + "");
        switch (day) {
            case 0:
                binding.ivBg.setBackgroundResource(R.drawable.one);
                break;
            case 1:
                binding.ivBg.setBackgroundResource(R.drawable.two);
                break;
            case 2:
                binding.ivBg.setBackgroundResource(R.drawable.three);
                break;
            case 3:
                binding.ivBg.setBackgroundResource(R.drawable.four);
                break;
            case 4:
                binding.ivBg.setBackgroundResource(R.drawable.five);
                break;
            case 5:
                binding.ivBg.setBackgroundResource(R.drawable.six);
                break;
            case 6:
                binding.ivBg.setBackgroundResource(R.drawable.seven);
                break;
            default:
                binding.ivBg.setBackgroundResource(R.drawable.one);
        }
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofObject(new FloatEvaluator(), 1.0f, 1.2f);
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                if (value != 1.2f) {
                    binding.ivBg.setScaleX(value);
                    binding.ivBg.setScaleY(value);
                } else {
                    goToActivity();
                }
            }

            private void goToActivity() {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, android.R.anim.fade_out);
                finish();
            }
        });
        animator.start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //  ClickUtils.exitBy2Click(2000, AdminMainActivity.this);
        }
        return true;
    }
}
