package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

/**
 * Created by YF-04 on 2017/10/24.
 */

public class BluetoothConnectActivityReceiver extends BroadcastReceiver {
    private static final String TAG = "MainActivity";


    private String pin = "1234";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            Log.d(TAG, "手机配对 广播: ");
            BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


            try {
                //1.确认配对
                Log.d(TAG, "确认配对..... ");
                ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
            } catch (Exception e) {
                Log.e(TAG, "确认配对:Exception e : "+e.getMessage());
                e.printStackTrace();
            }
            //2.终止有序广播
            Log.i("order...", "isOrderedBroadcast:"+isOrderedBroadcast()+",isInitialStickyBroadcast:"+isInitialStickyBroadcast());
            abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
            try {
                //3.调用setPin方法进行配对...
                Log.d(TAG, "调用setPin方法进行配对..... ");
                boolean ret = ClsUtils.setPin(mBluetoothDevice.getClass(), mBluetoothDevice, pin);
            } catch (Exception e) {
                Log.e(TAG, "调用setPin方法进行配对:Exception e : "+e.getMessage());
                e.printStackTrace();
            }


            try {
                //取消用户输入
                Log.d(TAG, "取消用户输入.....  ");
//                ClsUtils.cancelPairingUserInput(mBluetoothDevice.getClass(), mBluetoothDevice);
            } catch (Exception e) {
                Log.e(TAG, "取消用户输入:Exception e : "+e.getMessage());
                e.printStackTrace();
            }


        }

    }
}
