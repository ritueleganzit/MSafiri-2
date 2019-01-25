package com.eleganz.msafiri.utils;

import android.app.Application;

/**
 * Created by eleganz on 3/1/19.
 */

public class MyApplication  extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


}