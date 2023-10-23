package com.rick.recoveryapp.fragment.setting;

import static android.content.Context.MODE_PRIVATE;

import static com.xuexiang.xutil.data.SPUtils.getSharedPreferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.rick.recoveryapp.R;
import com.rick.recoveryapp.activity.MacDrDialog;
import com.rick.recoveryapp.activity.SettingActivity;
import com.rick.recoveryapp.bluetooth.BtDataPro;
import com.rick.recoveryapp.bluetooth.BtReceiver;
import com.rick.recoveryapp.databinding.FragmentSettingBinding;
import com.rick.recoveryapp.databinding.FragmentSettingloginBinding;
import com.rick.recoveryapp.utils.LocalConfig;
import com.rick.recoveryapp.utils.XToastUtils;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.dialog.strategy.InputInfo;

public class SettingLoginFragement extends Fragment {

    private boolean isHideFirst = true;  // 输入框密码是否是隐藏的，默认为true
    FragmentSettingloginBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingloginBinding.inflate(getLayoutInflater(), container, false);
        binding.imageView.setEnabled(false);
        binding.settingloginEtxtUser.setEnabled(false);
        binding.settingloginEtxtPassword.setEnabled(false);
        binding.settingloginBtnSave.setEnabled(false);
        AgainInto();
        initClinck();
        return binding.getRoot();
    }

    public void initClinck() {
        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHideFirst == true) {
                    binding.imageView.setImageResource(R.drawable.eyeb);
                    //密文
                    HideReturnsTransformationMethod method1 = HideReturnsTransformationMethod.getInstance();
                    binding.settingloginEtxtPassword.setTransformationMethod(method1);
                    isHideFirst = false;
                } else {
                    binding.imageView.setImageResource(R.drawable.eyeg);
                    //密文
                    TransformationMethod method = PasswordTransformationMethod.getInstance();
                    binding.settingloginEtxtPassword.setTransformationMethod(method);
                    isHideFirst = true;
                }
                // 光标的位置
                int index = binding.settingloginEtxtPassword.getText().toString().length();
                binding.settingloginEtxtPassword.setSelection(index);
            }
        });

        binding.settingloginBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogLoader.getInstance().showInputDialog(
                        LocalConfig.SettingContext,
                        R.drawable.icon_warning,
                        getString(R.string.tip_warning),
                        getString(R.string.setting_warning),
                        new InputInfo(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                                getString(R.string.hint_please_input_password)),
                        (dialog, input) -> XToastUtils.toast(input.toString()),
                        getString(R.string.lab_continue),
                        (dialog, which) -> {
                            KeyboardUtils.hideSoftInput(dialog);
                            dialog.dismiss();
                            if (dialog instanceof MaterialDialog) {
                                String changePass = ((MaterialDialog) dialog).getInputEditText().getText().toString();
                                if (changePass.equals("654321")) {
                                    binding.settingloginEtxtUser.setEnabled(true);
                                    binding.settingloginEtxtPassword.setEnabled(true);
                                    binding.settingloginBtnSave.setEnabled(true);

                                } else {
                                    Toast.makeText(LocalConfig.SettingContext, "密码输入错误！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        getString(R.string.lab_change),
                        (dialog, which) -> {
                            KeyboardUtils.hideSoftInput(dialog);
                            dialog.dismiss();
                        });
            }
        });

        binding.settingloginBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.settingloginEtxtUser.getText().toString().trim();
                String password = binding.settingloginEtxtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LocalConfig.SettingContext, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    SettingActivity.sharedPreferences.edit().putString("name", name).apply();
                    SettingActivity.sharedPreferences.edit().putString("password", password).apply();
                    binding.imageView.setEnabled(false);
                    binding.settingloginEtxtUser.setEnabled(false);
                    binding.settingloginEtxtPassword.setEnabled(false);
                    binding.settingloginBtnSave.setEnabled(false);
                    Toast.makeText(LocalConfig.SettingContext, "修改成功！", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void AgainInto() {

        String name = SettingActivity.sharedPreferences.getString("name", "");
        String password = SettingActivity.sharedPreferences.getString("password", "");
        binding.settingloginEtxtUser.setText(name);
        binding.settingloginEtxtPassword.setText(password);
        //记住密码打上√
    }

}
