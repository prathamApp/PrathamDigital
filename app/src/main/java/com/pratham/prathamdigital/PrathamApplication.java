package com.pratham.prathamdigital;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.pratham.prathamdigital.util.ConnectivityReceiver;

import java.util.UUID;

/**
 * Created by HP on 09-08-2017.
 */

public class PrathamApplication extends Application {
    private static PrathamApplication mInstance;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static String sessionId = UUID.randomUUID().toString();

    public static synchronized PrathamApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}

