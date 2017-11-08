package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by YF-04 on 2017/10/20.
 */

public class BluetoothDiscovery {
    private static final String TAG = "BluetoothDiscovery";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDiscoveryListener discoveryListener;

    private Context context;

    private BluetoothDevice device;

    public BluetoothDiscovery(Context context){
        this.context=context;
        if (mBluetoothAdapter==null){
            mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        }
    }



    /**
     * 开始扫描
     */
    public void startDiscovery() {
        Log.d(TAG, "startDiscovery: ");
        if (mHandler != null) {
            mHandler.removeMessages(MSG_STOP_SCAN);
        }
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(mReceiver, filter);

            mBluetoothAdapter.startDiscovery();
//            if (mBtDeviceListener != null) {
//                mBtDeviceListener.onStartScan();
//            }
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(MSG_STOP_SCAN, 12000);
            }
        }
    }

    public void stopDiscovery(){
        Log.d(TAG, "stopDiscovery: ");
        mBluetoothAdapter.cancelDiscovery();
    }


    public final static int MSG_STOP_SCAN      = 0xc06;//停止扫描
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg!=null){
                switch (msg.what){
                    case MSG_STOP_SCAN://停止扫描
                        cancelDiscovery();
                        break;

                    default:
                }
            }
        }
    };

    /**
     * 取消扫描
     */
    public void cancelDiscovery() {
        Log.d(TAG, "cancelDiscovery: ");
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
        }
    }

    /**
     * 蓝牙广播接受者
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "action:" + action);
            }
            // 发现一个设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获取设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!TextUtils.isEmpty(device.getName()) && !device.getName().equalsIgnoreCase("null")) {
                    Log.d(TAG, "onReceive : device address-"+device.getAddress()+" device name-"+device.getName());
                    if (discoveryListener!=null){
                        discoveryListener.discovery(device);
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                context.unregisterReceiver(mReceiver);
//                if (mBtDeviceListener != null) {
//                    mBtDeviceListener.onStopScan();
//                }
            }
        }
    };

    public void abortBroadcast(){
        Log.d(TAG, "abortBroadcast: ");
        mReceiver.abortBroadcast();
    }

    public BluetoothDiscoveryListener getDiscoveryListener() {
        return discoveryListener;
    }

    public void setDiscoveryListener(BluetoothDiscoveryListener discoveryListener) {
        this.discoveryListener = discoveryListener;
    }

    interface BluetoothDiscoveryListener{
        void discovery(BluetoothDevice bluetoothDevice);
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
