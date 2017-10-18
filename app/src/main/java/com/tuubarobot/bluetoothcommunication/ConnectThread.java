package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by YF-04 on 2017/10/12.
 */

public class ConnectThread extends Thread {
    private  String TAG = "ConnectThread";


    public static  final UUID SERVICE_UUID= UUID.fromString(BluetoothCommunication.My_UUID) ;

    private BluetoothSocket mmSocket;
    private final BluetoothDevice mDevice;

    private ConnectThreadInterface connectThreadInterface;

    public ConnectThread(BluetoothDevice device) {
        mDevice = device;
        BluetoothSocket tmp = null;
        // 得到一个bluetoothsocket
        try {
            if (mmSocket==null || !mmSocket.isConnected()){
                mmSocket = mDevice.createRfcommSocketToServiceRecord(SERVICE_UUID);
            }else {
                Log.d(TAG, "ConnectThread is connected: ");
            }
        } catch (IOException e) {
            Log.e(TAG, "create() failed", e);
            mmSocket = null;
        }
    }

    @Override
    public void run() {
        Log.i(TAG, "BEGIN mConnectThread");
        try {
            // socket 连接,该调用会阻塞，直到连接成功或失败
            mmSocket.connect();
        } catch (IOException e) {
            try {//关闭这个socket
                mmSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return;
        }
        // 启动连接线程
        if (connectThreadInterface!=null){
            connectThreadInterface.connected(mmSocket, mDevice);
        }

    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "close() of connect socket failed", e);
        }
    }

    public ConnectThreadInterface getConnectThreadInterface() {
        return connectThreadInterface;
    }

    public void setConnectThreadInterface(ConnectThreadInterface connectThreadInterface) {
        this.connectThreadInterface = connectThreadInterface;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    interface ConnectThreadInterface{
        public void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice);
    }

}