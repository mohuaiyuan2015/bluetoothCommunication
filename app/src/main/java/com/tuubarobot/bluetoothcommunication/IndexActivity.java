package com.tuubarobot.bluetoothcommunication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IndexActivity extends AppCompatActivity {

    private static final String TAG = "IndexActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;


    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> devices;

    public BluetoothDeviceModel deviceModel;

    private Context context;

    private Button startDiscovery;
    private Button getBoundDevicesBtn;
    private Button getConnectInfoBtn;
    private Button connectBtn;

    private int sdkInt=-1;

    private List<BluetoothDeviceModel> bluetoothDevices;
    private RecyclerView boundDeviceRecyclerView;
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;

    private BluetoothUtils bluetoothUtils;
    private BluetoothDiscovery bluetoothDiscovery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_index);
        context=this;

        initUI();
        
        initData();

        sdkInt=Build.VERSION.SDK_INT;
        bluetoothUtils=new BluetoothUtils();
        checkBleSupportAndInitialize();

        bluetoothDiscovery=new BluetoothDiscovery(context);

        
        initListener();

        requestPermissions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        //方式一
        //mohuaiyuan 获取 已经绑定(配对)的 蓝牙
//        getBondedDevices();

        //方式二
        //mohuaiyuan 扫描蓝牙
        startDiscovery();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
        ConnectionInfoCollector.clearDeviceModelList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        bluetoothDiscovery.stopDiscovery();
    }

    private void initData() {
        Log.d(TAG, "initData: ");
        bluetoothDevices=new ArrayList<>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        boundDeviceRecyclerView.setLayoutManager(linearLayoutManager);
        bluetoothDeviceAdapter=new BluetoothDeviceAdapter(bluetoothDevices);
        boundDeviceRecyclerView.setAdapter(bluetoothDeviceAdapter);

    }

    private void initListener() {
        Log.d(TAG, "initListener: ");

        startDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "startDiscovery.setOnClickListener onClick: ");
                startDiscovery();
                
            }
        });

        getBoundDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getBoundDevicesBtn.setOnClickListener onClick: ");

                //mohuaiyuan 获取 已经绑定(配对)的 蓝牙
                getBondedDevices();

            }
        });

        getConnectInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getConnectInfoBtn.setOnClickListener onClick: ");

                String mac=mBluetoothAdapter.getAddress();
                Log.d(TAG, "mac: "+mac);

                List<BluetoothDeviceModel>list=ConnectionInfoCollector.getBluetoothDeviceModelList();
                int size=list.size();
                Log.d(TAG, "------------------all connect info ----------------- ");
                for (int i=0;i<size;i++){
                    BluetoothDeviceModel model=list.get(i);
                    Log.d(TAG, "conect Info :  name-"+model.getDevice().getName()+" address-"+model.getDevice().getAddress());
                }
                Log.d(TAG, "---------------------------------------------------: ");
            }
        });

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " connectBtn.setOnClickListener onClick: ");
                if (ConnectionInfoCollector.getBluetoothDeviceModelList().isEmpty()){
                    Toast.makeText(context, context.getString(R.string.connectReminder), Toast.LENGTH_SHORT).show();

                }else {
                    Intent intent=new Intent(context,MainActivity.class);
                    context.startActivity(intent);
                }
            }
        });


        bluetoothDeviceAdapter.setOnItemClickListener(new BluetoothDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Log.d(TAG, "bluetoothDeviceAdapter.setOnItemClickListener onItemClick: ");
                //方式一
                // mohuaiyuan 直接进入下一个界面
//                bluetoothDevice=bluetoothDevices.get(position);
//                Intent intent=new Intent(context,MainActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putParcelable(ConstantString.BLUETUUTH_DEVICE,bluetoothDevice);
//                intent.putExtras(bundle);
//                context.startActivity(intent);

                //方式二
                // mohuaiyuan 绑定（配对）蓝牙
//                bluetoothDevice=bluetoothDevices.get(position);
//                boolean bondResult= bluetoothDevice.createBond();
//                Log.d(TAG, "bondResult: "+bondResult);

                //方式三
                //mohuaiyuan 绑定（配对）蓝牙
//                try {
//                    bluetoothDevice=bluetoothDevices.get(position);
//                    bluetoothDiscovery.setDevice(bluetoothDevice);
//                    //通过工具类ClsUtils,调用createBond方法
//                    ClsUtils.createBond(bluetoothDevice.getClass(),bluetoothDevice);
//
////                    //1.确认配对
////                    ClsUtils.setPairingConfirmation(bluetoothDevice.getClass(), bluetoothDevice, true);
////                    //2.终止有序广播
////                    bluetoothDiscovery.abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
////                    //3.调用setPin方法进行配对...
////                    boolean ret = ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, pin);
//                } catch (Exception e) {
//                    Log.e(TAG, "配对蓝牙 出现错误 : " );
//                    Log.e(TAG, "Exception e: "+e.getMessage() );
//                    e.printStackTrace();
//                }

                //mohuaiyuan  暂时 这样子写  20171025
                deviceModel=bluetoothDevices.get(position);
                if (deviceModel.getDevice().getBondState()==BluetoothDevice.BOND_BONDED) {

                    deviceModel = bluetoothDevices.get(position);
                    Intent intent = new Intent(context, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Constants.BLUETUUTH_DEVICE, deviceModel.getDevice());
                    intent.putExtras(bundle);
                    context.startActivity(intent);

                }else {

                    try {
                        deviceModel=bluetoothDevices.get(position);
                        bluetoothDiscovery.setDevice(deviceModel.getDevice());
                        //通过工具类ClsUtils,调用createBond方法
                        ClsUtils.createBond(deviceModel.getDevice().getClass(),deviceModel.getDevice());

                    } catch (Exception e) {
                        Log.e(TAG, "配对蓝牙 出现错误 : " );
                        Log.e(TAG, "Exception e: "+e.getMessage() );
                        e.printStackTrace();
                    }

                }
                //震动
                bluetoothUtils.vibrate(context,40);


            }
        });

        bluetoothDeviceAdapter.setOnSelectListener(new BluetoothDeviceAdapter.OnSelectListener() {
            @Override
            public void onSelectClick(View itemView, int position) {
                Log.d(TAG, "bluetoothDeviceAdapter.setOnSelectListener onSelectClick: ");
                //CheckBox 状态的变化
                BluetoothDeviceModel model=bluetoothDevices.get(position);
                boolean selectState=model.isSelectState();
                bluetoothDevices.get(position).setSelectState(!selectState);
                refreshBluetoothData();

                //选中的蓝牙的信息放到ConnectionInfoCollector 中
                if (!selectState){
                    ConnectionInfoCollector.addDeviceModel(model);
                }else {
                    ConnectionInfoCollector.removeDeviceModel(model);
                }

            }
        });

        bluetoothDiscovery.setDiscoveryListener(new BluetoothDiscovery.BluetoothDiscoveryListener() {
            @Override
            public void discovery(BluetoothDevice bluetoothDevice) {
                Log.d(TAG, "bluetoothDiscovery.setDiscoveryListener discovery: ");
                BluetoothDeviceModel model=new BluetoothDeviceModel();
                model.setDevice(bluetoothDevice);
                bluetoothDevices.add(model);
                refreshBluetoothData();
            }
        });

    }

    private void initUI() {
        Log.d(TAG, "initUI: ");

        startDiscovery= (Button) findViewById(R.id.startDiscovery);
        getBoundDevicesBtn= (Button) findViewById(R.id.getBoundDevicesBtn);
        getConnectInfoBtn= (Button) findViewById(R.id.getConnectInfoBtn);
        connectBtn= (Button) findViewById(R.id.connectBtn);

        boundDeviceRecyclerView= (RecyclerView) findViewById(R.id.boundDeviceRecyclerView);
    }


    private void getBondedDevices(){
        Log.d(TAG, "getBondedDevices: ");

        //清空 蓝牙列表
//        if (!bluetoothDevices.isEmpty()){
//            bluetoothDevices.clear();
//            Log.d(TAG, "getBondedDevices:clear.... ");
//            refreshBluetoothData();
//        }

        devices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG, "bonded device size ="+devices.size());
        for(BluetoothDevice bonddevice:devices){
            Log.d(TAG, "bonded device: name =="+bonddevice.getName()+" address--"+bonddevice.getAddress());
            BluetoothDeviceModel model=new BluetoothDeviceModel();
            model.setDevice(bonddevice);
            bluetoothDevices.add(model);
            refreshBluetoothData();
        }

    }

    private void requestPermissions() {
        Log.d(TAG, "requestPermissions: ");

        String[] permissions = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION ,
                Manifest.permission.ACCESS_FINE_LOCATION ,
                Manifest.permission.BLUETOOTH_PRIVILEGED };
        //Android M Permission check
        Log.d(TAG, "Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        if (sdkInt >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Android M Permission check ");
            Log.d(TAG, "ask for Permission... ");
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_COARSE_LOCATION);

        } else {
            startScan();
        }

    }

    private void startScan() {
        Log.d(TAG, "startScan: ");

    }

    //add API 23 Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: "+requestCode);

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                Log.d(TAG, "grantResults.length: "+grantResults.length);
                if(grantResults.length>0){
                    Log.d(TAG, "grantResults[0]: "+grantResults[0]);
                }

                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success

                    startScan();
                }else {
                    Toast.makeText(context, "Bluetooth need some permisssions ,please grante permissions and try again !", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void refreshBluetoothData(){
        Log.d(TAG, "refreshBluetoothData: ");
        if (bluetoothDeviceAdapter != null) {
            bluetoothDeviceAdapter.notifyDataSetChanged();
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

    private void startDiscovery(){
        Log.d(TAG, "startDiscovery: ");
        if (!bluetoothDevices.isEmpty()){
            bluetoothDevices.clear();
            refreshBluetoothData();
        }
        bluetoothDiscovery.startDiscovery();
    }




}

 /* * ━━━━━━感觉萌萌哒━━━━━━
 * 　　　　　　　　┏┓　　　┏┓
 * 　　　　　　　┏┛┻━━━┛┻┓
 * 　　　　　　　┃　　　　　　　┃ 　
 * 　　　　　　　┃　　　━　　　┃
 * 　　　　　　　┃　＞　　　＜　┃
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃...　⌒　...　┃
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃　Code is far away from bug with the animal protecting　　　　　　　　　　
 * 　　　　　　　　　┃　　　┃       神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃　　　　　　　　　　　
 * 　　　　　　　　　┃　　　┃ 　　　　　　
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　　　　　　　　　　
 * 　　　　　　　　　┃　　　┗━━━┓
 * 　　　　　　　　　┃　　　　　　　┣┓
 * 　　　　　　　　　┃　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛
 *
 */
