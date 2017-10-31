package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothDevice;

/**
 * Created by YF-04 on 2017/10/30.
 */

class BluetoothDeviceModel {
    private BluetoothDevice device;
    private boolean selectState;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public boolean isSelectState() {
        return selectState;
    }

    public void setSelectState(boolean selectState) {
        this.selectState = selectState;
    }
}
