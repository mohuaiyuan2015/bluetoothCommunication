package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by YF-04 on 2017/10/12.
 */

public class ConnectThread extends Thread {
    private  String TAG = "ConnectThread";


    private BluetoothSocket mmSocket;
    private final BluetoothDevice mDevice;

    private ConnectThreadInterface connectThreadInterface;
    private ConnectStatusInterface connectStatusInterface;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothUtils bluetoothUtils;

    public ConnectThread(BluetoothDevice device) {
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        bluetoothUtils=new BluetoothUtils();
        mDevice = device;
        BluetoothSocket tmp = null;
        // 得到一个bluetoothsocket
        try {
            if (mmSocket==null || !mmSocket.isConnected()){
                UUID uuid=bluetoothUtils.createClientUUID(mDevice);
                Log.d(TAG, "uuid: "+uuid.toString());
                mmSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
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
        Log.d(TAG, "ConnectThread  run: ");

        // Always cancel discovery because it will slow down a connection
//        bluetoothAdapter.cancelDiscovery();
        
        try {
            // socket 连接,该调用会阻塞，直到连接成功或失败
            mmSocket.connect();
        } catch (IOException e) {
            try {//关闭这个socket
                mmSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            if(connectStatusInterface!=null){
                connectStatusInterface.connectionFailed();
            }

            return;
        }
        try {
            // 启动连接线程
            if (connectThreadInterface!=null){
                connectThreadInterface.connected(mmSocket, mDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void cancel() {
        try {
            if (mmSocket!=null){
                mmSocket.close();
            }
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

    public ConnectStatusInterface getConnectStatusInterface() {
        return connectStatusInterface;
    }

    public void setConnectStatusInterface(ConnectStatusInterface connectStatusInterface) {
        this.connectStatusInterface = connectStatusInterface;
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

    interface ConnectStatusInterface{
        public void connectionFailed();
    }

}