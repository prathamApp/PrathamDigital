package com.pratham.prathamdigital;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import java.util.UUID;

/**
 * Created by HP on 09-08-2017.
 */

public class PrathamApplication extends Application {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static String sessionId = UUID.randomUUID().toString();

}

