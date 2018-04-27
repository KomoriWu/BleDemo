package com.example.bledemo;

import android.app.Application;
import android.content.Context;

import com.inuker.bluetooth.library.BluetoothClient;

/**
 * Created by KomoriWu
 * on 2017-04-18.
 */

public class MyApplication extends Application {
    private static BluetoothClient mBluetoothClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static BluetoothClient getBluetoothClient(Context context) {
        if (mBluetoothClient == null) {
            synchronized (BluetoothClient.class) {
                if (mBluetoothClient == null) {
                    mBluetoothClient = new BluetoothClient(context);
                }
            }
        }
        return mBluetoothClient;
    }


}
