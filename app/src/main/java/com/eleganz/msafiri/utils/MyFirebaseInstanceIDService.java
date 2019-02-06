package com.eleganz.msafiri.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by eleganz on 29/1/19.
 */

public class MyFirebaseInstanceIDService
        extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d("mytokenrefreshed",token);

    }
}
