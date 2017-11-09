package com.tuubarobot.bluetoothcommunication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    private Context context;

    private BluetoothAdapter mBluetoothAdapter;
    private int sdkInt=-1;

    private Set<BluetoothDevice> devices;

    private EditText sendMesssageEditText;
    private Button send;
    private Button startServiceThread;
    private Button startClientThread;
    private Button startDiscovery;
    private Button getDebugMsgBtn;
//    private Button changeRecycleVisible;

    private RecyclerView recyclerView;
    private RecyclerView deviceRecyclerView;

//    private List<Integer> orders;
    private List<String> questions;
    private List<String> answers;
    private List<Map<String,String>> dataList;
    private MyAdapter myAdapter;

//    private List<BluetoothDevice> bluetoothDevices;
//    private BluetoothDeviceAdapter bluetoothDeviceAdapter;

    private BluetoothUtils bluetoothUtils;
    private BluetoothCommunication communication;
    private BluetoothDevice bluetoothDevice;

    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private List<OutputStream> outputStreamList;
    private List<InputStream> inputStreamList;

    private List<String> list=new LinkedList<>();

    private BluetoothDiscovery bluetoothDiscovery;

    private MyHandler myHandler;

    private int connectionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);

        context=this;

        initUI();

        initData();

        //蓝牙通讯 初始化
        sdkInt=Build.VERSION.SDK_INT;
        communication=new BluetoothCommunication();
        bluetoothUtils=new BluetoothUtils();
        bluetoothUtils.setTAG(TAG);
        checkBleSupportAndInitialize();
        bluetoothUtils.setDiscoverableTimeout(120);

        //mohuaiyuan 注释掉 不使用 这个方法了
//        getBondedDevices();

        bluetoothDiscovery=new BluetoothDiscovery(context);

        initListener();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

    }

    private void initData() {
        Log.d(TAG, "initData: ");

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null){
            bluetoothDevice= (BluetoothDevice) bundle.get(Constants.BLUETUUTH_DEVICE);
            Log.d(TAG, "bluetoothDevice:name- "+bluetoothDevice.getName()+"  address:"+bluetoothDevice.getAddress());
        }

//        orders =new ArrayList<>();
//        String[] temp=getResources().getStringArray(R.array.orders);
//        for (int i=0;i<temp.length;i++){
//            Integer integer=Integer.valueOf(temp[i]);
//            orders.add(integer);
//        }

        dataList=new ArrayList<>();
        String []questionTemp=getResources().getStringArray(R.array.questionArray);
        String []answerTemp=getResources().getStringArray(R.array.answerArray);
        for (int i=0;i<questionTemp.length;i++){
            Map<String,String>map=new HashMap<>();
            map.put(Constants.QUESTION,questionTemp[i]);
            map.put(Constants.ANSWER,answerTemp[i]);
            dataList.add(map);
        }

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);

        myAdapter = new MyAdapter(dataList);
        recyclerView.setAdapter(myAdapter);

//        bluetoothDevices=new ArrayList<>();
//        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
//        deviceRecyclerView.setLayoutManager(linearLayoutManager);
//        bluetoothDeviceAdapter=new BluetoothDeviceAdapter(bluetoothDevices);
//        deviceRecyclerView.setAdapter(bluetoothDeviceAdapter);

        myHandler=new MyHandler();

        outputStreamList=new ArrayList<>();
        inputStreamList=new ArrayList<>();


    }



    private void initListener() {
        Log.d(TAG, "initListener: ");

        startServiceThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "startServiceThread.setOnClickListener onClick: ");

                AcceptThread.HandleMessage handleMessage=new AcceptThread.HandleMessage() {
                    @Override
                    public void handleMessage(String msg) {
                        Log.d(TAG, "startServiceThread handleMessage: "+msg);
                    }
                };
                communication.startAcceptThread(handleMessage);

            }
        });

        startClientThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "startConnectThread.setOnClickListener onClick: ");
                //mohuaiyuan 20171025  暂时注释
//                recyclerView.setVisibility(View.VISIBLE);
                Log.d(TAG, "connnectInfo size: "+ConnectionInfoCollector.getBluetoothDeviceModelList().size());
                if (connectionCount!=0){
                    Message message=new Message();
                    message.what=Constants.VIEW_GONE;
                    myHandler.sendMessage(message);
                    reStartConnectThread();
                }else {
                    connectToServer();
                }

            }
        });

//        bluetoothDiscovery.setDiscoveryListener(new BluetoothDiscovery.BluetoothDiscoveryListener() {
//            @Override
//            public void discovery(BluetoothDevice bluetoothDevice) {
//                bluetoothDevices.add(bluetoothDevice);
//                refreshBluetoothData();
//
//            }
//        });
//
//        startDiscovery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "startDiscovery.setOnClickListener onClick: ");
//                if (!bluetoothDevices.isEmpty()){
//                    bluetoothDevices.clear();
//                    refreshBluetoothData();
//                }
//                bluetoothDiscovery.startDiscovery();
//
//            }
//        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "send.setOnClickListener onClick: ");

                String tempStr = sendMesssageEditText.getText().toString().trim();
                if (tempStr == null || tempStr.length() < 1) {
                    tempStr = "蓝牙发送数据了hellowrold";
                }
                write(tempStr.getBytes());
            }
        });

        getDebugMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getDebugMsgBtn.setOnClickListener onClick: ");
                int size=ConnectionInfoCollector.getBluetoothDeviceModelList().size();
                List<BluetoothDeviceModel>list=ConnectionInfoCollector.getBluetoothDeviceModelList();
                Log.d(TAG, "----------------connect info ----------------: ");
                Log.d(TAG, "size: "+size);
                for (int i=0;i<size;i++){
                    String name=list.get(i).getDevice().getName();
                    String mac=list.get(i).getDevice().getAddress();
                    boolean selectState=list.get(i).isSelectState();
                    Log.d(TAG, "name- "+name+"  mac-"+mac+"  selectState-"+selectState);

                }
                Log.d(TAG, "-------------inputStreamList------------------------: ");
                int isSize=inputStreamList.size();
                Log.d(TAG, "inputStreamList.size(): "+isSize);

                Log.d(TAG, "-------------inputStreamList------------------------: ");
                int osSize=outputStreamList.size();
                Log.d(TAG, "outputStreamList.size(): "+isSize);
                Log.d(TAG, "------------------------------------------------------: ");
                Log.d(TAG, "connectionCount: "+connectionCount);



            }
        });

//        changeRecycleVisible.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "changeRecycleVisible.setOnClickListener onClick: ");
//                Message message=new Message();
//                if (recyclerView.getVisibility()==View.GONE){
//                    message.what= Constants.VIEW_VISIBLE;
//                }else{
//                    message.what=Constants.VIEW_GONE;
//                }
//                myHandler.sendMessage(message);
//            }
//        });

        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d(TAG, "myAdapter onItemClick: ");

                Log.d(TAG, "onItemClick position: " + position);
                int data = position;
                if ((position + 1) == dataList.size()) {
                    data = 100;
                }
                Log.d(TAG, "发送的数据: " + data);
                if (ConnectionInfoCollector.getBluetoothDeviceModelList().isEmpty()) {
                    //mohuaiyuan 发送给单个蓝牙
                    Log.d(TAG, "给 单个蓝牙发送数据: ");
                    write(String.valueOf(data).getBytes());
                }else {
                    //mohuaiyuan 发送给多个蓝牙
                    Log.d(TAG, "给 多个蓝牙发送数据：");
                    write(outputStreamList,String.valueOf(data).getBytes());
                }
                //震动
                bluetoothUtils.vibrate(context,40);

            }
        });

//        bluetoothDeviceAdapter.setOnItemClickListener(new BluetoothDeviceAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View itemView, int position) {
//                Log.d(TAG, "bluetoothDeviceAdapter.setOnItemClickListener onItemClick: ");
//                startConnectedThread(bluetoothDevices.get(position));
//            }
//        });

    }

    private void initUI() {
        startServiceThread= (Button) findViewById(R.id.startServiceThread);
        startClientThread= (Button) findViewById(R.id.startClientThread);
        sendMesssageEditText= (EditText) findViewById(R.id.sendMesssageEditText);
        send= (Button) findViewById(R.id.send);
        startDiscovery= (Button) findViewById(R.id.startDiscovery);
        getDebugMsgBtn= (Button) findViewById(R.id.getDebugMsgBtn);
//        changeRecycleVisible= (Button) findViewById(R.id.changeRecycleVisible);
        recyclerView= (RecyclerView) findViewById(R.id.orderRecyclerView);
        deviceRecyclerView= (RecyclerView) findViewById(R.id.deviceRecyclerView);
    }

    private void write(byte[] array){
        Log.d(TAG, "write(byte[] array): ");
        try {
            mOutputStream.write(array);
            mOutputStream.flush();
        } catch (IOException e) {
            Log.d(TAG, "发送数据 出现 IOException e:"+e.getMessage());
            e.printStackTrace();

            Message message=new Message();
            message.what=Constants.VIEW_GONE;
            myHandler.sendMessage(message);
            //重新连接
            reStartConnectThread();
        }

    }

    private void write(OutputStream outputStream,byte[] array){
        Log.d(TAG, "write(OutputStream outputStream,byte[] array): ");
        try {
            outputStream.write(array);
            outputStream.flush();
        } catch (IOException e) {
            Log.d(TAG, "发送数据 出现 IOException e:"+e.getMessage());
            e.printStackTrace();

            Message message=new Message();
            message.what=Constants.VIEW_GONE;
            myHandler.sendMessage(message);
            //重新连接
            reStartConnectThread();
        }
    }

    private void write(List<OutputStream>streams ,byte[]array){
        Log.d(TAG, "write(List<OutputStream>streams ,byte[]array): ");
        for (int i=0;i<streams.size();i++){
            write(streams.get(i),array);
        }
    }
    
    private void reStartConnectThread(){
        Log.d(TAG, "reStartConnectThread: ");
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                //关闭原来的连接
                closeConnection();

                //mohuaiyuan 20171111 连接多个蓝牙
                //重新连接
                connectToServer();

            }
        };
        handler.postDelayed(runnable,500);
//        handler.post(runnable);

    }

    private void closeConnection(){
        Log.d(TAG, "closeConnection: ");
        if (ConnectionInfoCollector.getBluetoothDeviceModelList().isEmpty()){
                //mohuaiyuan 单个蓝牙  关闭连接
                communication.cancleConnectThread();

        }else {

            communication.cancleAllConnectThread();
        }

    }

    private void connectToServer(){
        Log.d(TAG, "connectToServer: ");

        //init envirment
        connectionCount=0;
        if (!outputStreamList.isEmpty()){
            outputStreamList.clear();
        }
        if (!inputStreamList.isEmpty()){
            inputStreamList.clear();
        }


        if (ConnectionInfoCollector.getBluetoothDeviceModelList().isEmpty()){
            Log.d(TAG, "开始  连接 单个 蓝牙: ");

            startConnectedThread();
        }else {
            //TODO mohuaiyuan 20171030 连接多个 蓝牙
            Log.d(TAG, "开始  连接 多个 蓝牙: ");
            int size=ConnectionInfoCollector.getBluetoothDeviceModelList().size();
            List<BluetoothDevice> temp=new ArrayList<>();

            for (int i=0;i<size;i++){
                BluetoothDevice device=ConnectionInfoCollector.getBluetoothDeviceModelList().get(i).getDevice();
                temp.add(device);
            }
            startConnectedThread(temp);
        }
    }


    ConnectThread.ConnectThreadInterface connectThreadInterface=new ConnectThread.ConnectThreadInterface() {
        @Override
        public void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice) {
            Log.d(TAG, " ConnectThread.ConnectThreadInterface connected: ");
            
            ClientThread clientThread=new ClientThread(bluetoothSocket);
            clientThread.start();
        }
    };


    private void startConnectedThread(){
        Log.d(TAG, "startConnectedThread: ");

        communication.startConnectedThread(bluetoothDevice,connectThreadInterface);

    }

    private void startConnectedThread(List<BluetoothDevice> bluetoothDeviceList){
        Log.d(TAG, "startConnectedThread(List<BluetoothDevice> bluetoothDeviceList): ");
        communication.startConnectedThread(bluetoothDeviceList,connectThreadInterface);
    }
    
    private void prepareGetBondedDevices(){

        String[] permissions=new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION    };
        //Android M Permission check
        Log.d(TAG, "Build.VERSION.SDK_INT: "+ Build.VERSION.SDK_INT);
        if(sdkInt>= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION )!=PackageManager.PERMISSION_GRANTED ){
            Log.d(TAG, "Android M Permission check ");
            Log.d(TAG, "ask for Permission... ");
            ActivityCompat.requestPermissions(this,permissions, PERMISSION_REQUEST_COARSE_LOCATION);

        }else{
            startScan();
        }

    }

    private void startScan() {

    }

    private void getBondedDevices(){

        prepareGetBondedDevices();

         devices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG, "bonded device size ="+devices.size());
        for(BluetoothDevice bonddevice:devices){
            Log.d(TAG, "bonded device: name =="+bonddevice.getName()+" address--"+bonddevice.getAddress());
            if (bonddevice.getName().contains("BT")){
                bluetoothDevice=bonddevice;
            }
        }

    }


    /**
     * get bluetoothAdapter and open bluetooth if the bluetooth is disabled
     */
    private void checkBleSupportAndInitialize() {
        Log.d(TAG, "checkBleSupportAndInitialize: ");
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(TAG,"device_ble_not_supported ");
            Toast.makeText(this, R.string.device_ble_not_supported,Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Log.d(TAG, "device_ble_not_supported ");
            Toast.makeText(this,R.string.device_ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "open bluetooth ");
            bluetoothUtils.openBluetooth();
        }
    }


//    private void refreshBluetoothData(){
//        if (bluetoothDeviceAdapter != null) {
//            bluetoothDeviceAdapter.notifyDataSetChanged();
//        }
//    }


    class MyHandler extends Handler{

        public MyHandler(){

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "MyHandler handleMessage: ");
            switch (msg.what){
                case Constants.BLUETOOTH_CONNECT_SUCCESS:
                    Log.d(TAG, "Constants.BLUETOOTH_CONNECT_SUCCESS: ");

                    break;

                case Constants.BLUETOOTH_CONNECT_FAILED:
                    Log.d(TAG, "Constants.BLUETOOTH_CONNECT_FAILED: ");

                    break;

                case Constants.BLUETOOTH_CONNECT_LOST:
                    Log.d(TAG, " Constants.BLUETOOTH_CONNECT_LOST: ");

                    break;

                case Constants.VIEW_VISIBLE:
                    recyclerView.setVisibility(View.VISIBLE);
                    break;

                case Constants.VIEW_INVISIBLE:

                    break;

                case Constants.VIEW_GONE:
                    recyclerView.setVisibility(View.GONE);
                    break;

                default:

                    break;
            }
        }
    }

    class ClientThread extends Thread{

        private BluetoothSocket mBluetoothSocket;
        private InputStream inputStream;// 获取到输入流
        private OutputStream outputStream;// 获取到输出流
        //mohuaiyuan 暂时先注释
        private  boolean isLoop;

        public ClientThread(BluetoothSocket bluetoothSocket){
            Log.d(TAG, "ClientThread: ");
            this.mBluetoothSocket=bluetoothSocket;
            Log.d(TAG, "mBluetoothSocket.getRemoteDevice().getAddress(): "+mBluetoothSocket.getRemoteDevice().getAddress());

            try {
                // 获取到输入流
                mInputStream= inputStream = mBluetoothSocket.getInputStream();
                // 获取到输出流
               mOutputStream= outputStream = mBluetoothSocket.getOutputStream();

                outputStreamList.add(outputStream);
                inputStreamList.add(inputStream);
                //mohuaiyuan 暂时先注释
                isLoop=true;

            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "准备接收数据: ");
            Log.d(TAG, "Client 获取到输入流 inputStream: "+inputStream);
            Log.d(TAG, "Client 获取到输出流 outputStream: "+outputStream);
        }

        @Override
        public void run() {
            super.run();
            Log.d(TAG, "ClientThread run: ");
            synchronized (this){
                try {
                    Log.d(TAG, "开始的值 connectionCount: "+connectionCount);
                    while (isLoop) {
                        if (inputStream != null) {
                            Log.d(TAG, "inputStream!=null: ");
                            byte[] buffer = new byte[128];
                            // 每次读取128字节，并保存其读取的角标
                            int count = inputStream.read(buffer);
                            String str = new String(buffer, 0, count, "utf-8");
                            Log.d(TAG, "buffer: " + str);

                            isLoop = false;
                            if (str.equals("准备接收数据")) {

//                            connectCountIncrease();
                                connectionCount++;

                                Log.d(TAG, "改变之后的值 connectionCount: " + connectionCount);
                                Log.d(TAG, "接收到数据啦。。。: ");

                                Message message = new Message();
                                if (ConnectionInfoCollector.getBluetoothDeviceModelList().isEmpty()) {
                                    Log.d(TAG, "当个 蓝牙已经连接: ");
                                    Log.d(TAG, "设置 recycleView 可见。。。: ");
                                    message.what = Constants.VIEW_VISIBLE;
                                    myHandler.sendMessage(message);
                                } else {
                                    if (connectionCount == ConnectionInfoCollector.getBluetoothDeviceModelList().size()) {
                                        Log.d(TAG, "多个蓝牙都 已经连接。。。");
                                        Log.d(TAG, "设置 recycleView 可见。。。: ");
                                        message.what = Constants.VIEW_VISIBLE;
                                        myHandler.sendMessage(message);
                                    }
                                }


                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    private synchronized void connectCountIncrease() {
        connectionCount++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (ConnectionInfoCollector.getBluetoothDeviceModelList().isEmpty()){
            communication.cancleConnectThread();
        }else {
            communication.cancleAllConnectThread();
        }

    }
}
