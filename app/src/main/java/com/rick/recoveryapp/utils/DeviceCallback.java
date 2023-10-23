package com.rick.recoveryapp.utils;

import android.bluetooth.BluetoothDevice;

/**
 * 作者 ：WangJinchan
 * 邮箱 ：945750315@qq.com
 * 时间 ：2021/4/8
 * 说明 ：
 */
public interface DeviceCallback {
    void result(BluetoothDevice device, int rssi);
}
