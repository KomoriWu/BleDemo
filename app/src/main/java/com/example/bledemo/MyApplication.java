package com.example.bledemo;

import android.app.Application;
import android.content.Context;

import com.example.bledemo.drawables.SVGLoader;
import com.inuker.bluetooth.library.BluetoothClient;

import timber.log.Timber;

/**
 * Created by KomoriWu
 * on 2017-04-18.
 */

public class MyApplication extends Application {
    private static BluetoothClient mBluetoothClient;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        SVGLoader.load(this);
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {

        }
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
