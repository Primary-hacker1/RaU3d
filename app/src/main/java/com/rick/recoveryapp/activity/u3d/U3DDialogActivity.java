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

package com.rick.recoveryapp.activity.u3d;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.rick.recoveryapp.R;
import com.rick.recoveryapp.utils.HideKeyboard;
import com.rick.recoveryapp.utils.LocalConfig;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xui.widget.button.ButtonView;

public class U3DDialogActivity extends XPageActivity implements View.OnClickListener {

//    ActivityU3dDialogBinding binding;
//    Context context;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        context = this;
//        setContentView(R.layout.activity_dialog);
//        binding = ActivityU3dDialogBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        initClick();
//
//        binding.dialogUserid.setText("V0.7");
//        binding.dialogName.setText(LocalConfig.ip);
//    }
//
//    public void initClick() {
//        binding.dialogClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        binding.dialogNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    LocalConfig.ip = binding.dialogName.getText().toString();
//                    if (LocalConfig.ip.equals("")) {
//                        Toast.makeText(context, "请输服务器IP地址！", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    finish();
//                } catch (Exception e) {
//                    e.getMessage();
//                }
//
//            }
//        });
//
//    }
//
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (HideKeyboard.isShouldHideKeyboard(v, ev)) {
//                hideKeyboard(v.getWindowToken());
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    /**
//     * 获取InputMethodManager，隐藏软键盘
//     *
//     * @param token
//     */
//    private void hideKeyboard(IBinder token) {
//        if (token != null) {
//            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }

    ButtonView dialog_close, dialog_next;
    EditText dialog_userid, dialog_name;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_u3d_dialog);
        dialog_close = findViewById(R.id.dialog_close);
        dialog_next = findViewById(R.id.dialog_next);
        dialog_userid = findViewById(R.id.dialog_userid);
        dialog_name = findViewById(R.id.dialog_name);

        dialog_userid.setText("V0.7");
        dialog_name.setText(LocalConfig.ip);
        dialog_close.setOnClickListener(this);
        dialog_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_close:
                finish();
                break;
            case R.id.dialog_next:
                try {
                    LocalConfig.ip = dialog_name.getText().toString();
                    if (LocalConfig.ip.equals("")) {
                        Toast.makeText(context, "请输服务器IP地址！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    finish();

                    //  TrainFragment
                    //    openPage(TrainFragment.class, getIntent().getExtras());

//                    PageOption.to(TrainFragment.class) //跳转的fragment
//                            .setAnim(CoreAnim.zoom) //页面跳转动画
//                            .open(this); //打开页面进行跳转

//                    openPage(TrainFragment.class);

                } catch (Exception e) {
                    e.getMessage();
                }
                break;
        }
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
}
