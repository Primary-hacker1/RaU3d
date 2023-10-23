///*
// * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// */
//package com.rick.recoveryapp.activity;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.rick.recoveryapp.R;
//import com.rick.recoveryapp.adapter.BtDevAdapter;
//import com.rick.recoveryapp.bluetooth.BtReceiver;
//import com.xuexiang.xpage.base.XPageActivity;
//import com.xuexiang.xui.widget.button.ButtonView;
//
//public class BluetoolthDialog extends XPageActivity implements View.OnClickListener, BtBase.Listener, BtReceiver.Listener, BtDevAdapter.Listener{
//
//    private BtReceiver mBtReceiver;
//    private final BtDevAdapter mBtDevAdapter = new BtDevAdapter(this);
//    private final BtClient mClient = new BtClient(this);
//    ButtonView dialog_refresh,dialog_close;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_buletoohdlg);
//        RecyclerView rv = findViewById(R.id.rv_list);
//        dialog_refresh=findViewById(R.id.dialog_refresh);
//        dialog_close=findViewById(R.id.dialog_close);
//
//        dialog_refresh.setOnClickListener(this);
//        dialog_close.setOnClickListener(this);
//        rv.setLayoutManager(new LinearLayoutManager(this));
//        rv.setAdapter(mBtDevAdapter);
//
//        mBtReceiver = new BtReceiver(this, this);//注册蓝牙广播
//        BluetoothAdapter.getDefaultAdapter().startDiscovery();
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.dialog_refresh:
//                mBtDevAdapter.reScan();
//                break;
//
//            case R.id.dialog_close:
//                finish();
//                break;
//        }
//    }
//
//    @Override
//    public void onItemClick(BluetoothDevice dev) {
//        if (mClient.isConnected(dev)) {
//            return;
//        }
//        mClient.connect(dev);
//    }
//
//    @Override
//    public void foundDev(BluetoothDevice dev) {
//        mBtDevAdapter.add(dev);
//    }
//
//    @Override
//    public void socketNotify(int state, Object obj) {
//
//    }
////    @Override
////    //安卓重写返回键事件
////    public boolean onKeyDown(int keyCode, KeyEvent event) {
////        finish();
////        return true;
////    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(mBtReceiver);
//        mClient.unListener();
//        mClient.close();
//    }
//}
