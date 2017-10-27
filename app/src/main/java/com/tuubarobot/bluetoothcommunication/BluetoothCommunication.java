package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by YF-04 on 2017/10/13.
 */

public class BluetoothCommunication {
    private static final String TAG = "Bluetoothcommunication";

    public   static String My_UUID="00001101-0000-1000-8000-00805F9B34FB";
    private ConnectThread connectThread;
    private AcceptThread acceptThread;

    private BluetoothDevice mDevice;

    public BluetoothCommunication(){

    }



    /**
     * 客户端线程
     * @param device
     */
    public void startConnectedThread(BluetoothDevice device,ConnectThread.ConnectThreadInterface connectThreadInterface){
        Log.d(TAG, "startConnectedThread: ");
        this.mDevice=device;

        connectThread=new ConnectThread(mDevice);
        if (connectThreadInterface!=null){
            connectThread.setConnectThreadInterface(connectThreadInterface);
        }
        connectThread.start();
    }



    /**
     * 服务器端 线程
     */
    public void startAcceptThread(AcceptThread.HandleMessage handleMessage){
        Log.d(TAG, "startAcceptThread: ");

        if (acceptThread!=null && acceptThread.isAlive()){
            acceptThread.close();
        }

        acceptThread=new AcceptThread();
        if (handleMessage!=null){
            acceptThread.setHandleMessage(handleMessage);
        }else {
            acceptThread.setHandleMessage(new AcceptThread.HandleMessage() {
                @Override
                public void handleMessage(String msg) {
                    Log.d(TAG, "handleMessage: "+msg);
                }
            });
        }

        acceptThread.start();
    }


    public ConnectThread getConnectThread() {
        return connectThread;
    }

    public void setConnectThread(ConnectThread connectThread) {
        this.connectThread = connectThread;
    }

    public AcceptThread getAcceptThread() {
        return acceptThread;
    }

    public void setAcceptThread(AcceptThread acceptThread) {
        this.acceptThread = acceptThread;
    }
}
