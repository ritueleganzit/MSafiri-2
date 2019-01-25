package com.eleganz.msafiri.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by eleganz on 3/1/19.
 */

public class NetworkChangeReceiver  extends BroadcastReceiver {


    public NetworkChangeReceiver() {
        super();
    }
    NoInternet activity;
    @Override
    public void onReceive(Context context, Intent arg1) {

        if(context instanceof NoInternet) {
             activity = (NoInternet) context;
        }
        try
        {
            if (isOnline(context)) {

                Log.e("keshav", "Online Connect Intenet ");
            } else {
                Log.e("keshav", "no  Intenet ");
                Intent myIntent = new Intent(context, MyService.class);
                context.startService(myIntent);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
