package com.eleganz.msafiri.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eleganz.msafiri.CurrentTrip;
import com.eleganz.msafiri.TripActivity;

import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service implements GoogleApiClient.ConnectionCallbacks
{

    Intent intent;
    CurrentTripSession currentTripSession;
    SessionManager sessionManager;
String user_id,id;
    private Handler handler; // Handler used to execute code on the UI thread

    public SensorService(Handler handler, SessionManager sessionManager1,CurrentTripSession currentTripSession1) {
        super();
        Log.i("wherreeeeeee", "here I am!");
        this.handler = handler;
        sessionManager=sessionManager1;
        currentTripSession=currentTripSession1;
        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        id=tripData.get(CurrentTripSession.TRIP_ID);
    }

    public SensorService() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

this.intent=intent;

        startTimer();
        /* handler.postDelayed(new Runnable() {
            public void run() {
                Toast.makeText(context, "location update", Toast.LENGTH_SHORT).show();         // this method will contain your almost-finished HTTP calls
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);*/
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent(this, TripActivity.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 10000); //


    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                Location crntLocation=new Location("crntlocation");
                crntLocation.setLatitude(23.011783);
                crntLocation.setLongitude(72.523117);

                //Toast.makeText(SensorService.this, "location update", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        getSingleTripData();
                    }
                },5000);
            }
        };

    }

    private void getSingleTripData() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getSingleTripData(id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("SensorService",""+System.currentTimeMillis());
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {

                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject childObjct = jsonArray.getJSONObject(i);




                            intent.putExtra("latitude", childObjct.getString("last_lat"));

                            intent.putExtra("longitude", childObjct.getString("last_lng"));

                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);



                        }

                    }
                    else
                    {

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });


    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}