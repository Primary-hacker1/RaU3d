package com.rick.recoveryapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rick.recoveryapp.R;
import com.rick.recoveryapp.adapter.BtDevAdapter;
import com.rick.recoveryapp.base.XPageActivity;
import com.rick.recoveryapp.bluetooth.BtReceiver;
import com.rick.recoveryapp.databinding.ActivityActiviteBinding;
import com.rick.recoveryapp.databinding.ActivityTestBinding;
import com.rick.recoveryapp.utils.ActiveTimeTool;
import com.rick.recoveryapp.utils.LocalConfig;
import com.rick.recoveryapp.utils.NetWorkUtil;
import com.rick.recoveryapp.utils.TimeCountTool;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends XPageActivity {

    ActivityTestBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
       // initPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}


