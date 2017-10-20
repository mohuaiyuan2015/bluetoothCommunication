package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by YF-04 on 2017/10/13.
 */

public class AcceptThread extends Thread {
    private String TAG = "MainActivity";

    private final String NAME = "Bluetooth_Socket";

    public static  final UUID SERVICE_UUID= UUID.fromString(BluetoothCommunication.My_UUID) ;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothServerSocket serverSocket;// 服务端接口
    private BluetoothSocket socket;// 获取到客户端的接口
    private InputStream inputStream;// 获取到输入流
    private OutputStream outputStream;// 获取到输出流


    private boolean isLoop=false;

    public AcceptThread() {
        Log.d(TAG, "AcceptThread: ");
        if (mBluetoothAdapter==null){
            mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "AcceptThread run(): ");

        try {
            // 通过UUID监听请求，然后获取到对应的服务端接口
            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, SERVICE_UUID);

            isLoop=true;
            while(isLoop && !Thread.interrupted()){
                Log.d(TAG, "接收其客户端的接口: ");
                // 接收其客户端的接口
                socket = serverSocket.accept();
                Log.d(TAG, "socket==null: "+(socket==null));

                Thread handleSocket=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 获取到输入流
                            inputStream = socket.getInputStream();
                            // 获取到输出流
                            outputStream = socket.getOutputStream();

                            Log.d(TAG, "准备接收数据: ");
                            Log.d(TAG, "获取到输入流 inputStream: "+inputStream);
                            Log.d(TAG, "获取到输出流 outputStream: "+outputStream);

                            boolean isConnected=socket.isConnected();
                            Log.d(TAG, "isConnected: "+isConnected);

                            while (socket.isConnected()){
                                Log.d(TAG, "读数据。。。");

                                outputStream.write("准备接收数据".getBytes());

                                // 创建一个128字节的缓冲
                                byte[] buffer = new byte[128];
                                // 每次读取128字节，并保存其读取的角标
                                if (inputStream!=null){
                                    int count = inputStream.read(buffer);
                                    // 创建Message类，向handler发送数据
                                    Message msg = new Message();
                                    // 发送一个String的数据，让他向上转型为obj类型
                                    msg.obj = new String(buffer, 0, count, "utf-8");
                                    // 发送数据
                                    handler.sendMessage(msg);
                                }else {
                                    Log.e(TAG, "inputStream==null ..." );
                                }
                            }

                            inputStream.close();
                            socket.close();
                        } catch (IOException e) {
                            Log.d(TAG, "读数据线程中出现 IOException ");

                            Log.e(TAG, "IOException e: "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

                handleSocket.setDaemon(true);
                handleSocket.start();
            }
        } catch (IOException e) {
            isLoop=false;
            Log.d(TAG, "不在  接收其客户端的接口");
            Log.e(TAG, "e.getMessage(): "+e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket!=null){
                    serverSocket.close();
                }
            } catch (IOException e) {
                Log.d(TAG, "关闭serverSocket 出现 IOException ");
                Log.e(TAG, "IOException e :"+e.getMessage());
                e.printStackTrace();
            }
        }




    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (handleMessage!=null){
                if (msg!=null && msg.obj!=null){
                    String str=msg.obj.toString();
                    handleMessage.handleMessage(str);
                }else {
                    Log.e(TAG, "msg==null || msg.obj==null: " );
                }
            }else {
                Log.d(TAG, "handleMessage: "+msg.obj);
            }
        }
    };

    private HandleMessage handleMessage;

    public HandleMessage getHandleMessage() {
        return handleMessage;
    }

    public void setHandleMessage(HandleMessage handleMessage) {
        this.handleMessage = handleMessage;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public interface HandleMessage{
        public void handleMessage(String msg);
    }

    public void close(){
        isLoop=false;
    }

}
