package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by YF-04 on 2017/10/13.
 */

public class AcceptThread extends Thread {
    private  String TAG = "MainActivity";

    private final String NAME = "Bluetooth_Socket";

    public static  final UUID SERVICE_UUID= UUID.fromString(BluetoothCommunication.My_UUID) ;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothServerSocket serverSocket;// 服务端接口
    private BluetoothSocket socket;// 获取到客户端的接口
    private InputStream is;// 获取到输入流
    private OutputStream os;// 获取到输出流


    private boolean isExit=false;

    public AcceptThread() {
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        try {
            // 通过UUID监听请求，然后获取到对应的服务端接口
            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, SERVICE_UUID);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void run() {
        try {
            // 接收其客户端的接口
            socket = serverSocket.accept();
            // 获取到输入流
            is = socket.getInputStream();
            // 获取到输出流
            os = socket.getOutputStream();

            Log.d(TAG, "准备接收数据: ");
//            if (os!=null){
//                Log.d(TAG, "os!=null: 发送数据给手机。。。");
//                os.write("准备接收数据".getBytes());
//            }

            // 无线循环来接收数据
            while (!isExit) {
                // 创建一个128字节的缓冲
                byte[] buffer = new byte[128];
                // 每次读取128字节，并保存其读取的角标
                if (is!=null){
                    int count = is.read(buffer);
                    // 创建Message类，向handler发送数据
                    Message msg = new Message();
                    // 发送一个String的数据，让他向上转型为obj类型
                    msg.obj = new String(buffer, 0, count, "utf-8");
                    // 发送数据
                    handler.sendMessage(msg);
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
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
        isExit=true;
        socket=null;
        is=null;
        os=null;

    }

}
