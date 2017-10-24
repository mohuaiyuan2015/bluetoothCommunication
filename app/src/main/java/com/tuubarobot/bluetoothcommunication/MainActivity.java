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
    private RecyclerView recyclerView;
    private RecyclerView deviceRecyclerView;

//    private List<Integer> orders;
    private List<String> questions;
    private List<String> answers;
    private List<Map<String,String>> dataList;
    private MyAdapter myAdapter;

    private List<BluetoothDevice> bluetoothDevices;
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;

    private BluetoothUtils bluetoothUtils;
    private BluetoothCommunication communication;
    private BluetoothDevice bluetoothDevice;

    private OutputStream outputStream;
    private InputStream inputStream;

    private List<String> list=new LinkedList<>();

    private BluetoothDiscovery bluetoothDiscovery;

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

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        bluetoothDevice= (BluetoothDevice) bundle.get(ConstantString.BLUETUUTH_DEVICE);

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
            map.put(ConstantString.QUESTION,questionTemp[i]);
            map.put(ConstantString.ANSWER,answerTemp[i]);
            dataList.add(map);
        }

        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(llm);

        myAdapter = new MyAdapter(dataList);
        recyclerView.setAdapter(myAdapter);

        bluetoothDevices=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        deviceRecyclerView.setLayoutManager(linearLayoutManager);
        bluetoothDeviceAdapter=new BluetoothDeviceAdapter(bluetoothDevices);
        deviceRecyclerView.setAdapter(bluetoothDeviceAdapter);



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
                recyclerView.setVisibility(View.VISIBLE);

               startConnectedThread();
            }
        });

        bluetoothDiscovery.setDiscoveryListener(new BluetoothDiscovery.BluetoothDiscoveryListener() {
            @Override
            public void discovery(BluetoothDevice bluetoothDevice) {
                bluetoothDevices.add(bluetoothDevice);
                refreshBluetoothData();

            }
        });

        startDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "startDiscovery.setOnClickListener onClick: ");
                if (!bluetoothDevices.isEmpty()){
                    bluetoothDevices.clear();
                    refreshBluetoothData();
                }
                bluetoothDiscovery.startDiscovery();

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "send.setOnClickListener onClick: ");
                try {
                    String tempStr=sendMesssageEditText.getText().toString().trim();
                    if (tempStr==null || tempStr.length()<1){
                        tempStr="蓝牙发送数据了hellowrold";
                    }
                    outputStream.write(tempStr.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d(TAG, "myAdapter onItemClick: ");
                try {

                    Log.d(TAG, "onItemClick position: "+position);
                    int data=position;
                    if ((position+1)==dataList.size()){
                        data=100;
                    }
                    Log.d(TAG, "发送的数据: "+data);
                    outputStream.write(String.valueOf(data).getBytes());
                } catch (IOException e) {
                    Log.d(TAG, "发送数据出现 IOException : "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        bluetoothDeviceAdapter.setOnItemClickListener(new BluetoothDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d(TAG, "bluetoothDeviceAdapter.setOnItemClickListener onItemClick: ");
                startConnectedThread(bluetoothDevices.get(position));
            }
        });

    }

    private void initUI() {
        startServiceThread= (Button) findViewById(R.id.startServiceThread);
        startClientThread= (Button) findViewById(R.id.startClientThread);
        sendMesssageEditText= (EditText) findViewById(R.id.sendMesssageEditText);
        send= (Button) findViewById(R.id.send);
        startDiscovery= (Button) findViewById(R.id.startDiscovery);
        recyclerView= (RecyclerView) findViewById(R.id.orderRecyclerView);
        deviceRecyclerView= (RecyclerView) findViewById(R.id.deviceRecyclerView);
    }


    private void startConnectedThread(){
        Log.d(TAG, "startConnectedThread: ");

        ConnectThread.ConnectThreadInterface connectThreadInterface=new ConnectThread.ConnectThreadInterface() {
            @Override
            public void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice) {
                Log.d(TAG, " ConnectThread.ConnectThreadInterface connected: ");
                try {
                    outputStream=bluetoothSocket.getOutputStream();
                    inputStream=bluetoothSocket.getInputStream();

//                            while (true){
//                                if (inputStream!=null) {
//                                    Log.d(TAG, "inputStream!=null: ");
//                                    byte[] buffer = new byte[128];
//                                    // 每次读取128字节，并保存其读取的角标
//                                    int count = inputStream.read(buffer);
//                                    String str=new String(buffer, 0, count, "utf-8");
//                                    Log.d(TAG, "buffer: "+str);
//                                    if (str.equals("准备接收数据")){
//                                        Log.d(TAG, "接收到数据。。。: ");
//                                        break;
//                                    }
//                                }
//                            }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        communication.startConnectedThread(bluetoothDevice,connectThreadInterface);


    }

    private void startConnectedThread(BluetoothDevice bluetoothDevice){
        Log.d(TAG, "startConnectedThread: ");
        this.bluetoothDevice=bluetoothDevice;
        startConnectedThread();

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









    private void refreshBluetoothData(){
        if (bluetoothDeviceAdapter != null) {
            bluetoothDeviceAdapter.notifyDataSetChanged();
        }
    }

}
