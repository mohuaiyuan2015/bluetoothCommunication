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
    private static final String TAG = "BluetoothConnectActivit";


    private String pin = "1234";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            Log.d(TAG, "手机配对 广播: ");
            BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            try {
                //(三星)4.3版本测试手机还是会弹出用户交互页面(闪一下)，如果不注释掉下面这句页面不会取消但可以配对成功。(中兴，魅族4(Flyme 6))5.1版本手机两中情况下都正常
                ClsUtils.setPairingConfirmation(mBluetoothDevice.getClass(), mBluetoothDevice, true);
                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                //3.调用setPin方法进行配对...
                boolean ret = ClsUtils.setPin(mBluetoothDevice.getClass(), mBluetoothDevice, pin);
//                ClsUtils.cancelPairingUserInput(mBluetoothDevice.getClass(), mBluetoothDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
