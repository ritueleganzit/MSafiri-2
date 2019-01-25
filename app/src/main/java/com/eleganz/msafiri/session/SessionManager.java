package com.eleganz.msafiri.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.eleganz.msafiri.MainActivity;

import java.util.HashMap;

/**
 * Created by eleganz on 2/11/18.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "MSafiri";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String USER_ID = "user_id";
    public static final String PHOTO = "photo";
    public static final String USERNAME= "username";
    public static final String PASSWORD= "password";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String user_id,String username,String password,String photo){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(USER_ID, user_id);
        editor.putString(PHOTO, photo);
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);



        // commit changes
        editor.commit();
    }

    public void updateImage(String photo){

        editor.putString(PHOTO, photo);
        editor.commit();


    }

    public void updatePassword(String password){

        editor.putString(PASSWORD, password);
        editor.commit();


    }


    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }


    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(USER_ID, pref.getString(USER_ID, null));
        user.put(USERNAME, pref.getString(USERNAME, null));
        user.put(PASSWORD, pref.getString(PASSWORD, null));
        user.put(PHOTO, pref.getString(PHOTO, null));


        // return user
        return user;
    }



    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }



}
