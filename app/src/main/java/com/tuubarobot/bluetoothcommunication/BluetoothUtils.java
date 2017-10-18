package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by YF-04 on 2017/10/13.
 */

public class BluetoothUtils {

    private   String TAG = "BluetoothUtils";
    private BluetoothAdapter mBluetoothAdapter;

    public BluetoothUtils(){
        if (mBluetoothAdapter==null){
            mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        }
    }

    /**
     * 设置（开启）蓝牙可见性
     * @param timeout
     */
    public void setDiscoverableTimeout(int timeout) {
        Log.d(TAG, "setDiscoverableTimeout: ");

        mBluetoothAdapter.getScanMode();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(mBluetoothAdapter, timeout);
            setScanMode.invoke(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭蓝牙可见性
     */
    public void closeDiscoverableTimeout() {
        Log.d(TAG, "closeDiscoverableTimeout: ");
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(mBluetoothAdapter, 1);
            setScanMode.invoke(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void openBluetooth() {
        Log.d(TAG, "openBluetooth: ");
        if (mBluetoothAdapter!=null){
            if ( !mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.enable();
                //设置蓝牙可见性
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: 延时之后  设置蓝牙可见性");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        setDiscoverableTimeout(120);
                    }
                }).start();
            }else {
                Log.d(TAG, "run:  不 延时   设置蓝牙可见性");
                setDiscoverableTimeout(120);
            }
        }


    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }
}
