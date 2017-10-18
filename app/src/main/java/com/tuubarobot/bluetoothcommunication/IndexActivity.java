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


    private BluetoothDevice bluetoothDevice;

    private Context context;

    private Button getBoundDevicesBtn;

    private int sdkInt=-1;

    private List<BluetoothDevice> bluetoothDevices;
    private RecyclerView boundDeviceRecyclerView;
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;

    private BluetoothUtils bluetoothUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        context=this;

        initUI();
        
        initData();

        sdkInt=Build.VERSION.SDK_INT;
        bluetoothUtils=new BluetoothUtils();
        checkBleSupportAndInitialize();

        
        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        getBondedDevices();
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

        getBoundDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getBoundDevicesBtn.setOnClickListener onClick: ");
                getBondedDevices();
            }
        });

        bluetoothDeviceAdapter.setOnItemClickListener(new BluetoothDeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                bluetoothDevice=bluetoothDevices.get(position);
                Intent intent=new Intent(context,MainActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelable(ConstantString.BLUETUUTH_DEVICE,bluetoothDevice);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private void initUI() {
        boundDeviceRecyclerView= (RecyclerView) findViewById(R.id.boundDeviceRecyclerView);
        getBoundDevicesBtn= (Button) findViewById(R.id.getBoundDevicesBtn);
    }


    private void getBondedDevices(){
        Log.d(TAG, "getBondedDevices: ");

        prepareGetBondedDevices();

        if (!bluetoothDevices.isEmpty()){
            bluetoothDevices.clear();
            Log.d(TAG, "getBondedDevices:clear.... ");
            refreshBluetoothData();
        }

        devices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG, "bonded device size ="+devices.size());
        for(BluetoothDevice bonddevice:devices){
            Log.d(TAG, "bonded device: name =="+bonddevice.getName()+" address--"+bonddevice.getAddress());
            bluetoothDevices.add(bonddevice);
            refreshBluetoothData();
        }

    }

    private void prepareGetBondedDevices(){
        Log.d(TAG, "prepareGetBondedDevices: ");

        String[] permissions=new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION    };
        //Android M Permission check
        Log.d(TAG, "Build.VERSION.SDK_INT: "+ Build.VERSION.SDK_INT);
        if(sdkInt>= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED ){
            Log.d(TAG, "Android M Permission check ");
            Log.d(TAG, "ask for Permission... ");
            ActivityCompat.requestPermissions(this,permissions, PERMISSION_REQUEST_COARSE_LOCATION);

        }else{
            startScan();
        }

    }

    private void startScan() {

    }

    private void refreshBluetoothData(){
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

}
