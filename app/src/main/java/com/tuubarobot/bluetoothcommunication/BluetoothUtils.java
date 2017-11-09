package com.tuubarobot.bluetoothcommunication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by YF-04 on 2017/10/13.
 */

public class BluetoothUtils {

    private   String TAG = "BluetoothUtils";


    public   static String My_UUID="00001101-0000-1000-8000-MAC";

    public static  UUID SERVICE_UUID;

    private BluetoothAdapter mBluetoothAdapter;

    private static Vibrator vibrator;


    public BluetoothUtils(){
        if (mBluetoothAdapter==null){
            mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        }
    }

    /**
     * 震动
     * @param context
     * @param milliseconds :震动的时间（毫秒：ms）
     */
    public void vibrate(Context context,long milliseconds){
        if (vibrator==null){
            vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        }
        vibrator.vibrate(milliseconds);
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

    /**
     * 开启蓝牙并且开启蓝牙可见性
     */
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

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth(){
        Log.d(TAG, "closeBluetooth: ");
        if (mBluetoothAdapter!=null){
            if (mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.disable();
            }else {
                Log.d(TAG, "蓝牙已经关闭: ");
            }
        }else {
            Log.e(TAG, "mBluetoothAdapter==null: ");
        }
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getMac(){
        Log.d(TAG, "getMac: ");
        return mBluetoothAdapter.getAddress();
    }

    public UUID createServerUUID(){
        Log.d(TAG, "createServerUUID: ");
        String mac=getMac();
        if (mac==null){
            return null;
        }
        mac=mac.trim();
        Log.d(TAG, "bluetooth mac: "+mac);
        if (mac.contains(":")){
            mac=mac.replace(":","");
        }
        Log.d(TAG, "bluetooth mac: "+mac);
        String uuidString=My_UUID.replace("MAC",mac);
        SERVICE_UUID= UUID.fromString(uuidString);
        return SERVICE_UUID;
    }


    public UUID createClientUUID(BluetoothDevice bluetoothDevice){
        Log.d(TAG, "createClientUUID: ");
        if (bluetoothDevice==null || bluetoothDevice.getAddress()==null){
            return null;
        }
        String mac=bluetoothDevice.getAddress();
        mac=mac.trim();
        Log.d(TAG, "bluetooth mac: "+mac);
        if (mac.contains(":")){
            mac=mac.replace(":","");
        }
        Log.d(TAG, "bluetooth mac: "+mac);
        String uuidString=My_UUID.replace("MAC",mac);
        SERVICE_UUID= UUID.fromString(uuidString);
        return SERVICE_UUID;

    }

}
