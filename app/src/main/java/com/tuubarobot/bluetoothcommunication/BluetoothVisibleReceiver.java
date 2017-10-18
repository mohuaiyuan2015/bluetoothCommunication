package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * 开机  设置蓝牙可见性
 */
public class BluetoothVisibleReceiver extends BroadcastReceiver {
    private static final String TAG = "BluetoothVisibleReceive";

    private Context context;

    private BluetoothCommunication communication;

    private BluetoothUtils bluetoothUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        this.context=context;
        String action=intent.getAction();
        Log.d(TAG, "开机广播: "+action);
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            //mohuaiyuan 20171016  以下内容已经不需要了
//            //开启（设置）蓝牙可见性
//            bluetoothUtils=new BluetoothUtils();
//            bluetoothUtils.setDiscoverableTimeout(120);
//
//            communication=new BluetoothCommunication();
//            communication.startAcceptThread(null);

        }

    }




}
