package com.eleganz.msafiri.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.eleganz.msafiri.MainActivity;

import java.util.HashMap;

/**
 * Created by eleganz on 2/11/18.
 */

public class CurrentTripSession {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MSafiriTrip";


    public static final String TRIP_ID = "trip_id";
    public static final String DRIVER_ID = "driver_id";
    public static final String TRIP_STATUS = "trip_status";


    public CurrentTripSession(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createTripSession(String trip_id,String driver_id,boolean status){
        // Storing login value as TRUE

        // Storing name in pref
        editor.putString(TRIP_ID, trip_id);
        editor.putString(DRIVER_ID, driver_id);

editor.putBoolean(TRIP_STATUS,status);


        // commit changes
        editor.commit();
    }






    public HashMap<String, String> getTripDetails(){
        HashMap<String, String> trip = new HashMap<String, String>();
        // user name
        trip.put(TRIP_ID, pref.getString(TRIP_ID, null));
        trip.put(DRIVER_ID, pref.getString(DRIVER_ID, null));
        // return user
        return trip;
    }



    public boolean hasTrip(){
        return pref.getBoolean(TRIP_STATUS, false);
    }

    public void clearTripData()
    {
        editor.clear();
        editor.commit();
    }

}
