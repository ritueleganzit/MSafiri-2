package com.eleganz.msafiri;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.MyFirebaseMessagingService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class CurrentTrip extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "CurrentTrip";
    MapView mapView;
    Button cancelride, tellbtn;
    SessionManager sessionManager;
    String user_id,id,driver_id;
    GoogleMap map;
    ImageView back;
    CircleImageView fab;
    SpotsDialog spotsDialog;
    boolean isVisible=true;
    RelativeLayout dummyrel,cnfrel;
    String noti_message="",type="",ntrip_id="";
    RobotoMediumTextView cr_vehicle_name,cr_trip_price,cr_pickup,cr_pickupaddress,cr_dest,cr_destaddress,fullname,cr_calculate_time;
    CurrentTripSession currentTripSession;

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(CurrentTrip.this)
                .unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(CurrentTrip.this)
                .registerReceiver(mBroadcastReceiver, MyFirebaseMessagingService.BROADCAST_INTENT_FILTER);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            // read any data you might need from intent and do your action here

            String data=intent.getStringExtra("complete");
            type=intent.getStringExtra("type");
            ntrip_id=intent.getStringExtra("trip_id");

            if(data != null && !data.isEmpty())
            {
                startActivity(new Intent(CurrentTrip.this, ReviewActivity.class).putExtra("trip_id",ntrip_id));

            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trip);


       initView();
        sessionManager = new SessionManager(CurrentTrip.this);

        sessionManager.checkLogin();
        dummyrel=findViewById(R.id.temp);
        currentTripSession=new CurrentTripSession(CurrentTrip.this);
        spotsDialog = new SpotsDialog(CurrentTrip.this);

        HashMap<String, String> userData = sessionManager.getUserDetails();
        user_id = userData.get(SessionManager.USER_ID);
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        id=tripData.get(CurrentTripSession.TRIP_ID);
        driver_id=tripData.get(CurrentTripSession.DRIVER_ID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tellbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CurrentTrip.this, TellYourDriverActivity.class)
                .putExtra("tripbookedid",id)
                .putExtra("tripdriverid",driver_id));
            }
        });


        cancelride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog=  new AlertDialog.Builder(CurrentTrip.this)
                        .setMessage("Are you sure you want to cancel trip?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                spotsDialog.show();

                                cancelTrip(id);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false)
                        .show();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                Typeface face= Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
                textView.setTypeface(face);



            }
        });
        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CurrentTrip.this,ChangePickupActivity.class));
            }
        });*/
        mapView.getMapAsync(this);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

   /* @Override
    public void onBackPressed() {

        *//*if (currentTripSession.hasTrip())
        {
            new AlertDialog.Builder(CurrentTrip.this).setMessage("Are you sure you want to exit?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.finishAffinity(CurrentTrip.this);



                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

        else {

        }*//*
    }
*/
    public void initView(){
        mapView = (MapView) findViewById(R.id.map);
        //btn= (Button) findViewById(R.id.btn);
        tellbtn = (Button) findViewById(R.id.tellbtn);
        cancelride = (Button) findViewById(R.id.cancelride);
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
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
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

                if(isVisible)
                {
                    isVisible=false;
                    dummyrel.setVisibility(View.GONE);
                    YoYo.with(Techniques.SlideOutDown).duration(500).repeat(0).playOn(cnfrel);

                }
                else
                {
                    isVisible=true;


                    YoYo.with(Techniques.SlideInUp).duration(100).repeat(0).playOn(cnfrel);
                    dummyrel.setVisibility(View.VISIBLE);

                }
                Log.d("OnMapClick","clicked");


            }
        });
        getSingleTripData();
    }
    private void drawRoute(double from_lat, double from_lng, double to_lat, double to_lng) {
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

                .icon(BitmapDescriptorFactory.fromBitmap(firstMarker2)));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(markerLoc, 12);
        map.animateCamera(update);
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
                    Log.d(TAG,""+stringBuilder);
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject childObjct = jsonArray.getJSONObject(i);


                            if (childObjct.getString("user_trip_status").equalsIgnoreCase("onboard"))
                            {
                                cancelride.setText("Ongoing");
                                cancelride.setEnabled(false);
                                cancelride.setClickable(false);
                                tellbtn.setClickable(false);
                                tellbtn.setEnabled(false);

                            }
                            else {
                                cancelride.setText("Cancel");
                                cancelride.setEnabled(true);
                                cancelride.setClickable(true);
                                cancelride.setVisibility(View.VISIBLE);
                                tellbtn.setText("Tell Your Driver");
                            }


                            cr_vehicle_name.setText(""+childObjct.getString("vehicle_name")+" "+childObjct.getString("vehicle_number"));
                         //   cr_pickup.setText(""+childObjct.getString("from_title"));
                            cr_pickupaddress.setText(""+childObjct.getString("from_address"));

                            fullname.setText(""+childObjct.getString("fullname"));
                            cr_destaddress.setText(""+childObjct.getString("to_address"));
                            cr_calculate_time.setText(""+childObjct.getString("calculate_time"));

                            String price=childObjct.getString("trip_price");

                            if (price.equalsIgnoreCase("null"))

                            {
                                cr_trip_price.setText("$ 0");
                            }
                            else
                            {
                                cr_trip_price.setText("$ "+price);

                            }
                            Glide.with(CurrentTrip.this)
                                    .load(childObjct.getString("photo"))
                                    .apply(new RequestOptions().placeholder(R.drawable.pr).error(R.drawable.pr))
                                    .into(fab);

                            drawRoute(childObjct.getDouble("from_lat"),childObjct.getDouble("from_lng"),childObjct.getDouble("to_lat"),childObjct.getDouble("to_lng"));



                           /* fullname.setText(childObjct.getString("fullname"));
                            from_address.setText(childObjct.getString("from_address"));
                            to_address.setText(childObjct.getString("to_address"));
                            vehicle_number.setText("Vehicle Name: "+childObjct.getString("vehicle_type")+" "+childObjct.getString("vehicle_number"));
                            Glide.with(CurrentTrip.this)
                                    .load(childObjct.getString("photo"))
                                    .apply(new RequestOptions().placeholder(R.drawable.pr).error(R.drawable.pr))
                                    .into(photo);
                            drawRoute(childObjct.getDouble("from_lat"),childObjct.getDouble("from_lng"),childObjct.getDouble("to_lat"),childObjct.getDouble("to_lng"));
*/






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
    private void cancelTrip(String trip_id) {
        RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(BASEURL).build();
        ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
        apiInterface.cancelTrip(trip_id, user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    final StringBuilder stringBuilder=new StringBuilder();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("cancelTrip",""+stringBuilder);
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))

                    {
                        currentTripSession.clearTripData();

                        spotsDialog.dismiss();
                        startActivity(new Intent(CurrentTrip.this, CancelRideActivity.class));
                        finish();
                    }

                    else
                    {
                        spotsDialog.dismiss();

                        Toast.makeText(CurrentTrip.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        Log.d("cancelTrip",""+jsonObject.getString("message"));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                spotsDialog.dismiss();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CurrentTrip.this, HomeActivity.class));
        finish();
    }
}
