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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.rick.recoveryapp.R;

import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.utils.HideKeyboard;
import com.rick.recoveryapp.utils.LocalConfig;
import com.xuexiang.xpage.base.XPageActivity;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xutil.tip.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogActivity extends XPageActivity implements View.OnClickListener {

    ButtonView dialog_close, dialog_next;
    EditText dialog_userid, dialog_name, dialog_medicalNumber;
    Context context;
    BtDataPro btDataPro;
    //   public static DialogActivity dialogActivity;
    String pas = "^[a-zA-Z\u4e00-\u9fa5]{2,15}";
    String name, medicalNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_dialog);
        dialog_close = findViewById(R.id.dialog_close);
        dialog_next = findViewById(R.id.dialog_next);
        dialog_userid = findViewById(R.id.dialog_userid);
        dialog_name = findViewById(R.id.dialog_name);
        dialog_medicalNumber = findViewById(R.id.dialog_medicalNumber);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String sim = dateFormat.format(date);

        dialog_userid.setText(sim);
        dialog_close.setOnClickListener(this);
        dialog_next.setOnClickListener(this);
        btDataPro = new BtDataPro();
        name = LocalConfig.userName;
        medicalNumber = LocalConfig.medicalNumber;
        if (name != null && medicalNumber != null) {
            dialog_name.setText(LocalConfig.userName);
            dialog_medicalNumber.setText(LocalConfig.medicalNumber);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_close:
                finish();
                break;
            case R.id.dialog_next:
                try {
                    LocalConfig.userName = dialog_name.getText().toString();
                    LocalConfig.medicalNumber = dialog_medicalNumber.getText().toString();
                    LocalConfig.UserID = Long.valueOf(dialog_userid.getText().toString()).longValue();
                    if (LocalConfig.userName.equals("")) {
                        Toast.makeText(context, "请输入患者姓名！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (LocalConfig.medicalNumber.equals("")) {
                        Toast.makeText(context, "请输入病历号！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Pattern p = Pattern.compile(pas);
                    Matcher m = p.matcher(LocalConfig.userName);
                    if (!m.matches()) {
                        Toast.makeText(context, "患者姓名要求字母或汉字的组合", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btDataPro.sendBTMessage(btDataPro.CONNECT_SEND);
                    if (LocalConfig.ModType == 0) {
                        Intent in = new Intent(context, ActiveActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if (LocalConfig.ModType == 1) {
                        Intent in = new Intent(context, PassiveActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if (LocalConfig.ModType == 2) {
                        Intent in = new Intent(context, IntelligenceActivity.class);
                        startActivity(in);
                        finish();
                    }
                    if (LocalConfig.ModType == 3) {
                        Intent in = new Intent(context, SelectRolesActivity.class);
                        startActivity(in);
                        finish();
                    }
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
