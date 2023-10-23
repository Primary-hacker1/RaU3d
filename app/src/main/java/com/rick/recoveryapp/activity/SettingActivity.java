/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.rick.recoveryapp.activity;

import static com.xuexiang.xutil.data.SPUtils.getSharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.viewpager.widget.ViewPager;

import com.rick.recoveryapp.adapter.MyFragmentPagerAdapter;
import com.rick.recoveryapp.base.BaseApplication;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.bluetooth.BtReceiver;
import com.rick.recoveryapp.databinding.ActivitySettingBinding;
import com.rick.recoveryapp.fragment.setting.SettingFragment;
import com.rick.recoveryapp.utils.HideKeyboard;
import com.rick.recoveryapp.utils.LocalConfig;
import com.xuexiang.xui.utils.StatusBarUtils;

public class SettingActivity extends XPageActivity implements ViewPager.OnPageChangeListener {

    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    Context context;
    // ShadowButton setting_btn_return;
    private MyFragmentPagerAdapter mAdapter;
    // NoSwipeViewPager setting_pager_test;
    //  SuperTextView setting_txt_setting,setting_txt_history;
    Intent intent;
    ActivitySettingBinding binding;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.translucent(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        LocalConfig.SettingContext = context;
        itinClick();
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        binding.settingPagerTest.setAdapter(mAdapter);
        binding.settingPagerTest.setCurrentItem(0);
        binding.settingPagerTest.addOnPageChangeListener(this);
        sharedPreferences = getSharedPreferences("Personal", MODE_PRIVATE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        if (state == 2) {
//            switch (binding.settingPagerTest.getCurrentItem()) {
//                case PAGE_ONE:
//                    break;
//                default:
//            }
//        }
    }

    public void itinClick() {

        binding.settingBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, AdminMainActivity.class);
                //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        binding.settingTxtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.settingPagerTest.setCurrentItem(PAGE_ONE);
            }
        });

        binding.settingLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.settingPagerTest.setCurrentItem(PAGE_TWO);
            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (HideKeyboard.isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalConfig.SettingContext = null;
        unregisterReceiver(SettingFragment.mBtReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            intent = new Intent(LocalConfig.SettingContext, AdminMainActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return false;
        }
        return true;
    }
}
