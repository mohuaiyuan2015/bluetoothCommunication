package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by YF-04 on 2017/10/13.
 */

public class BluetoothCommunication {
    private static final String TAG = "Bluetoothcommunication";

    private ConnectThread connectThread;
    private AcceptThread acceptThread;

    private List<ConnectThread> connectThreadList;

    private BluetoothDevice mDevice;

    private List<BluetoothDevice> mbluetoothDeviceList;

    private ExecutorService installPool;

    public BluetoothCommunication(){
        //初始化 线程池
        installPool = Executors. newSingleThreadExecutor();

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
     * 客户端线程
     * @param bluetoothDeviceList
     * @param connectThreadInterface
     */
    public void startConnectedThread(List<BluetoothDevice> bluetoothDeviceList, final ConnectThread.ConnectThreadInterface connectThreadInterface){
        Log.d(TAG, "startConnectedThread: ");
        this.mbluetoothDeviceList=bluetoothDeviceList;
        if (connectThreadList==null){
            connectThreadList=new ArrayList<>();
        }else if (!connectThreadList.isEmpty()){
            connectThreadList.clear();
        }

        for (int i=0;i<mbluetoothDeviceList.size();i++){
            final BluetoothDevice device=mbluetoothDeviceList.get(i);

            connectThread=new ConnectThread(device);
            if (connectThreadInterface!=null){
                connectThread.setConnectThreadInterface(connectThreadInterface);
            }
            connectThreadList.add(connectThread);
            connectThread.start();

//            installPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    synchronized (this){
//
//                    }
//
//                }
//            });

        }

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

    public List<ConnectThread> getConnectThreadList() {
        return connectThreadList;
    }

    public void setConnectThreadList(List<ConnectThread> connectThreadList) {
        this.connectThreadList = connectThreadList;
    }

    public AcceptThread getAcceptThread() {
        return acceptThread;
    }

    public void setAcceptThread(AcceptThread acceptThread) {
        this.acceptThread = acceptThread;
    }

    public void cancleConnectThread() {
        if (connectThread!=null){
            connectThread.cancel();
            connectThread=null;
        }
    }

    public void cancleAllConnectThread() {
        for (int i=0;i<connectThreadList.size();i++){
            if (connectThreadList.get(i)!=null){
                connectThreadList.get(i).cancel();
                connectThreadList.set(i,null);
            }
        }
    }
}
