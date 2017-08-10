package com.pratham.prathamdigital;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by HP on 09-08-2017.
 */

public class PrathamApplication extends Application {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
