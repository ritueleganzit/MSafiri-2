package com.eleganz.msafiri;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.HistoryData;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;

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
    String user_id,trip_id;
    GoogleMap map;
    ImageView back;
    CircleImageView fab;
    SpotsDialog dialog;
    RelativeLayout layout_map;
    boolean isVisible=true;
    LinearLayout txtno_data;
    RelativeLayout dummyrel,cnfrel;

    RobotoMediumTextView cr_vehicle_name,cr_trip_price,cr_pickup,cr_pickupaddress,cr_dest,cr_destaddress,fullname,cr_calculate_time;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_screen);
        initView();
        sessionManager = new SessionManager(TrackingScreen.this);
        layout_map=findViewById(R.id.layout_map);
        sessionManager.checkLogin();
        dummyrel=findViewById(R.id.temp);
        dialog = new SpotsDialog(TrackingScreen.this);
        txtno_data=findViewById(R.id.txtno_data);
        HashMap<String, String> userData = sessionManager.getUserDetails();
        user_id = userData.get(SessionManager.USER_ID);
       
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tellbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrackingScreen.this, TellYourDriverActivity.class));
            }
        });


        cancelride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              AlertDialog alertDialog=  new AlertDialog.Builder(TrackingScreen.this)
                        .setMessage("Are you sure you want to cancel trip?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                dialog.show();

                                cancelTrip(trip_id);
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
                startActivity(new Intent(TrackingScreen.this,ChangePickupActivity.class));
            }
        });*/
        mapView.getMapAsync(this);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
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
        userTrips();
    }

    private void userTrips() {
        dialog.show();
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.userTrips(user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {


                dialog.dismiss();

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

                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);


                            Log.d("sdad",""+stringBuilder);

                            String user_trip_status=jsonObject1.getString("user_trip_status");
                            if(user_trip_status != null && !user_trip_status.isEmpty())
                            {
                                txtno_data.setVisibility(View.GONE);
                                layout_map.setVisibility(View.VISIBLE);
                                if ((user_trip_status.equalsIgnoreCase("booked")) || (user_trip_status.equalsIgnoreCase("confirm")) || (user_trip_status.equalsIgnoreCase("onboard")))
                                {

                                    trip_id=jsonObject1.getString("trip_id");
                                    cr_vehicle_name.setText(""+jsonObject1.getString("vehicle_name"));
                                    cr_trip_price.setText("$ "+jsonObject1.getString("trip_price"));
                                    fullname.setText(""+jsonObject1.getString("fullname"));
                                    cr_pickupaddress.setText(""+jsonObject1.getString("from_title"));
                                    cr_destaddress.setText(""+jsonObject1.getString("to_title"));
                                    cr_calculate_time.setText(""+jsonObject1.getString("calculate_time")+"");
                                    Glide.with(TrackingScreen.this)
                                            .load(jsonObject1.getString("photo"))
                                            .into(fab)
                                                ;
                                    Log.d("sdadbooked",""+stringBuilder);
                                }
                                else {
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

    private void cancelTrip(String trip_id) {
        RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(BASEURL).build();
        ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
        apiInterface.confirmTrip(trip_id, user_id, "cancel", new Callback<Response>() {
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

                        dialog.dismiss();
                        startActivity(new Intent(TrackingScreen.this, CancelRideActivity.class));
                        finish();
                    }

                    else
                    {
                        dialog.dismiss();

                        Toast.makeText(TrackingScreen.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
                dialog.dismiss();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TrackingScreen.this, HomeActivity.class));
        finish();
    }


}
