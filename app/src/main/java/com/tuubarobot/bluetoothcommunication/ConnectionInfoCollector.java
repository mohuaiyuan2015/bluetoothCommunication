package com.tuubarobot.bluetoothcommunication;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YF-04 on 2017/10/30.
 */

public class ConnectionInfoCollector {
    private static final String TAG = "ConnectionInfoCollector";
    private static List<BluetoothDeviceModel> bluetoothDeviceModelList=new ArrayList<>();

    public static List<BluetoothDeviceModel> getBluetoothDeviceModelList() {
        return bluetoothDeviceModelList;
    }

    public static void setBluetoothDeviceModelList(List<BluetoothDeviceModel> bluetoothDeviceModelList) {
        ConnectionInfoCollector.bluetoothDeviceModelList = bluetoothDeviceModelList;
    }

    public static boolean addDeviceModel(BluetoothDeviceModel model){
        Log.d(TAG, "addDeviceModel: ");
       return bluetoothDeviceModelList.add(model);
    }
    public static boolean removeDeviceModel(BluetoothDeviceModel model){
        Log.d(TAG, "removeDeviceModel: ");
       return bluetoothDeviceModelList.remove(model);
    }
    public static void clearDeviceModelList(){
        Log.d(TAG, "clearDeviceModelList: ");
        bluetoothDeviceModelList.clear();
    }

}
