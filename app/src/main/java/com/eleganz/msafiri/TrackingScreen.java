package com.eleganz.msafiri;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.adapter.MyTripAdapter;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.locationupdate.LatLngInterpolator;
import com.eleganz.msafiri.locationupdate.MarkerAnimation;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.HistoryData;
import com.eleganz.msafiri.utils.MyFirebaseMessagingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class TrackingScreen extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "TrackingScreen";
    MapView mapView;
    Button cancelride, tellbtn;
    SessionManager sessionManager;
    String user_id, trip_id;
    GoogleMap map;
    ImageView back;
    CircleImageView fab;
    SpotsDialog dialog;
    LinearLayout lin5;
    RelativeLayout layout_map;
    boolean isVisible = true;
    TimerTask hourlyTask;
    LinearLayout txtno_data;
    Handler h = new Handler();
    int delay = 15 * 1000; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;
    RelativeLayout dummyrel, cnfrel;
    String noti_message = "", type = "", ntrip_id = "";

    RobotoMediumTextView cr_vehicle_name, cr_trip_price, cr_pickup, cr_pickupaddress, cr_dest, cr_destaddress, fullname, cr_calculate_time;

    LocationManager locationmanager;


    //






    @Override
    public void onStop() {
        //SmartLocation.with(TrackingScreen.this).location().stop();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_screen);



        initView();

        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sessionManager = new SessionManager(TrackingScreen.this);
        layout_map = findViewById(R.id.layout_map);
        sessionManager.checkLogin();
        dummyrel = findViewById(R.id.temp);
        lin5 = findViewById(R.id.lin5);
        dialog = new SpotsDialog(TrackingScreen.this);
        txtno_data = findViewById(R.id.txtno_data);
        HashMap<String, String> userData = sessionManager.getUserDetails();
        user_id = userData.get(SessionManager.USER_ID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mapView.getMapAsync(this);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        MapsInitializer.initialize(getApplicationContext());

        map.getUiSettings().setAllGesturesEnabled(false);

        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getApplicationContext(), R.raw.style_json));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
        Log.e(TAG, "Style parsing failed.");


        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (isVisible) {
                    isVisible = false;
                    dummyrel.setVisibility(View.GONE);
                    YoYo.with(Techniques.SlideOutDown).duration(500).repeat(0).playOn(cnfrel);

                } else {
                    isVisible = true;


                    YoYo.with(Techniques.SlideInUp).duration(100).repeat(0).playOn(cnfrel);
                    dummyrel.setVisibility(View.VISIBLE);

                }
                Log.d("OnMapClick", "clicked");


            }
        });
        userTrips();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(TrackingScreen.this)
                .unregisterReceiver(mBroadcastReceiver);


        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();


        LocalBroadcastManager.getInstance(TrackingScreen.this)
                .registerReceiver(mBroadcastReceiver, MyFirebaseMessagingService.BROADCAST_INTENT_FILTER);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            // read any data you might need from intent and do your action here

            ntrip_id=intent.getStringExtra("trip_id");

            String data=intent.getStringExtra("complete");
            if(data != null && !data.isEmpty())
            {
                startActivity(new Intent(TrackingScreen.this, ReviewActivity.class).putExtra("trip_id",ntrip_id));

            }

        }
    };    public void getStatus(final String tripid)
    {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.userTrips(user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {




                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }


                    JSONObject jsonObject=new JSONObject(""+stringBuilder);

                    if (jsonObject.getString("status").equalsIgnoreCase("1"))
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        Log.d("Trackingscreen",""+stringBuilder);

                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);

                            getLatLong();
                            Log.d("sdad",""+stringBuilder);

                            String user_trip_status=jsonObject1.getString("user_trip_status");
                            String trpid=jsonObject1.getString("trip_id");
                            if(user_trip_status != null && !user_trip_status.isEmpty())
                            {


                                Log.d("statusdetail","-->1"+user_trip_status);

                                if ((user_trip_status.equalsIgnoreCase("completed"))  && (trpid.equalsIgnoreCase(tripid)) )
                                {


                                    txtno_data.setVisibility(View.VISIBLE);
                                    layout_map.setVisibility(View.GONE);
                                }
                            }


                        }


                    }


                    Log.d("your",""+stringBuilder);

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

    private void userTrips() {
        dialog.show();
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.userTrips(user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {




                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }


                    JSONObject jsonObject=new JSONObject(""+stringBuilder);

                    if (jsonObject.getString("status").equalsIgnoreCase("1"))
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        Log.d("Trackingscreen",""+stringBuilder);

                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);


                            Log.d("sdad",""+stringBuilder);

                            String user_trip_status=jsonObject1.getString("user_trip_status");
                            if(user_trip_status != null && !user_trip_status.isEmpty())
                            {


                                Log.d("statusdetail","-->1"+user_trip_status);

                                if ( (user_trip_status.equalsIgnoreCase("onboard")))
                                {
                                    dialog.dismiss();
                                    txtno_data.setVisibility(View.GONE);
                                    layout_map.setVisibility(View.VISIBLE);

                                    Log.d("statusdetail","-->1"+user_trip_status);
                                  //  Toast.makeText(TrackingScreen.this, ""+user_trip_status, Toast.LENGTH_SHORT).show();
                                    trip_id=jsonObject1.getString("trip_id");
                                    cr_vehicle_name.setText(""+jsonObject1.getString("vehicle_name"));

                                    String price="";
                                    price=jsonObject1.getString("trip_price");

                                    if (price!=null && !price.isEmpty())

                                    {
                                        cr_trip_price.setText("$ "+jsonObject1.getString("trip_price"));
                                    }
                                    if (price.equalsIgnoreCase("null"))
                                    {
                                        cr_trip_price.setText("$ 0" );

                                    }
                                    fullname.setText(""+jsonObject1.getString("fullname"));
                                    cr_pickupaddress.setText(""+jsonObject1.getString("from_title"));
                                    cr_destaddress.setText(""+jsonObject1.getString("to_title"));
                                    cr_calculate_time.setText(""+jsonObject1.getString("calculate_time")+"");
                                    Glide.with(TrackingScreen.this)
                                            .load(jsonObject1.getString("photo"))
                                            .into(fab)
                                                ;
                                    Log.d("sdadbooked",""+stringBuilder);
                                    Log.d("sdadbooked",""+jsonObject1.getDouble("from_lat"));
                                    Log.d("sdadbooked",""+jsonObject1.getDouble("from_lng"));
                                    Log.d("sdadbooked",""+jsonObject1.getDouble("to_lat"));
                                    Log.d("sdadbooked",""+jsonObject1.getDouble("to_lng"));
                                    drawRoute(jsonObject1.getDouble("from_lat"),jsonObject1.getDouble("from_lng"),jsonObject1.getDouble("to_lat"),jsonObject1.getDouble("to_lng"));

                                    if (user_trip_status.equalsIgnoreCase("onboard"))

                                    {


                                        h.postDelayed( runnable = new Runnable() {
                                            public void run() {
                                                //do something

getStatus(trip_id);
h.postDelayed(runnable, delay);
                                            }
                                        }, delay);

                                    }


                                }
                                else {
                                    dialog.dismiss();
                            txtno_data.setVisibility(View.VISIBLE);
                            layout_map.setVisibility(View.GONE);
                                }
                            }


                        }


                    }
                    else {
                        txtno_data.setVisibility(View.VISIBLE);
                        layout_map.setVisibility(View.GONE);
                        dialog.dismiss();
                    }

                    Log.d("your",""+stringBuilder);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
                txtno_data.setVisibility(View.VISIBLE);
                layout_map.setVisibility(View.GONE);
            }
        });

    }

    private void drawRoute(double from_lat, double from_lng, double to_lat, double to_lng) {
        dialog.dismiss();

        LatLng markerLoc=new LatLng(from_lat, from_lng);
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_green);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap firstMarker = Bitmap.createScaledBitmap(b, 84   , 84, false);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(from_lat, from_lng))
                .anchor(0.5f, 0.5f)
                .draggable(true)

                .icon(BitmapDescriptorFactory.fromBitmap(firstMarker)));

        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.location_red);
        Bitmap b2=bitmapdraw2.getBitmap();
        Bitmap firstMarker2 = Bitmap.createScaledBitmap(b2, 84   , 84, false);

        map.addMarker(new MarkerOptions()

                .position(new LatLng(to_lat, to_lng))
                .anchor(0.5f, 0.5f)
                .draggable(true)

                .icon(BitmapDescriptorFactory.fromBitmap(firstMarker2))
        );
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(markerLoc, 12);
        map.animateCamera(update);

    }

    public void initView(){
        mapView = (MapView) findViewById(R.id.map);
        //btn= (Button) findViewById(R.id.btn);
        tellbtn = (Button) findViewById(R.id.tellbtn);
        back = findViewById(R.id.back);
        fab=findViewById(R.id.fab);
        cr_vehicle_name=findViewById(R.id.cr_vehicle_name);
        cr_pickup=findViewById(R.id.cr_pickup);
        cr_pickupaddress=findViewById(R.id.cr_pickupaddress);
        cr_dest=findViewById(R.id.cr_dest);
        cr_destaddress=findViewById(R.id.cr_destaddress);
        fullname=findViewById(R.id.fullname);
        cr_calculate_time=findViewById(R.id.cr_calculate_time);
        cr_trip_price=findViewById(R.id.cr_trip_price);
        cnfrel=findViewById(R.id.cnfrel);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TrackingScreen.this, HomeActivity.class));
        finish();
    }
    public void getLatLong()
    {
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(TrackingScreen.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {


                locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        Log.e("lat", String.valueOf(location.getLatitude()));
                        Log.e("lat", String.valueOf(location.getLongitude()));
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });
                getLastKnownLocation();
                   /* if(location!=null)
                    {
                        onLocationChanged(location);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"location not found",Toast.LENGTH_LONG ).show();
                    }*/


            }
        }
    }

    private void getLastKnownLocation() {
        List<String> providers = locationmanager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(TrackingScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                    checkSelfPermission(TrackingScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return ;
            }
            Location l = locationmanager.getLastKnownLocation(provider);
//            Log.e("last known location, provider: %s, location: %s", provider,
//                    l);

//            Toast.makeText(this, ""+l.getLatitude()+","+l.getLongitude(), Toast.LENGTH_SHORT).show();

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                //    ALog.d("found best last known location: %s", l);
                bestLocation = l;


                Log.d("mmmmmmmmmmmm",""+l.getLatitude());






            }
        }
        if (bestLocation == null) {

        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(TrackingScreen.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TrackingScreen.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(TrackingScreen.this)
                        .setTitle("Location Permission")
                        .setMessage("Allow app to use your current location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TrackingScreen.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(TrackingScreen.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }
    private void requestStoragePermission(final int position) {

        Dexter.withActivity(TrackingScreen.this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                            getLatLong();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                    }
                })
                .onSameThread()
                .check();
    }


}
