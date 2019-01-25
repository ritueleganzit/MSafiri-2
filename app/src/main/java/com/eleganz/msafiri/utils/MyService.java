package com.eleganz.msafiri.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by eleganz on 3/1/19.
 */

public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("keshav", "onStartCommand ");
       /* Intent dialogIntent = new Intent(this, NoInternet.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);*/


        return super.onStartCommand(intent, flags, startId);
    }
}
