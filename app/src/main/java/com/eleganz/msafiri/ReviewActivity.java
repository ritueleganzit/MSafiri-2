package com.eleganz.msafiri;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.KeyRep;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import spencerstudios.com.bungeelib.Bungee;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class ReviewActivity extends AppCompatActivity implements OnMapReadyCallback {
    MapView mapView;
    EditText ed_comment;
    RatingBar ratingBar;
    SessionManager sessionManager;
    String user_id,trip_id,comments;
    Button btnrating;
    float rating;
    GoogleMap map;
    CurrentTripSession currentTripSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mapView= (MapView) findViewById(R.id.map);
        ed_comment=  findViewById(R.id.ed_comment);
        btnrating=  findViewById(R.id.btnrating);
        sessionManager=new SessionManager(ReviewActivity.this);
        currentTripSession=new CurrentTripSession(ReviewActivity.this);

        sessionManager.checkLogin();
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        trip_id=tripData.get(CurrentTripSession.TRIP_ID);
trip_id="65";
        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);


        ratingBar= (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
rating=v;
            }
        });
        mapView.getMapAsync(this);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        btnrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                giveRating();
            }
        });

    }

    private void giveRating() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.addReview(user_id, trip_id, ""+rating, ""+ed_comment.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {



            }

            @Override
            public void failure(RetrofitError error) {

            }
        });



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        MapsInitializer.initialize(getApplicationContext());
        map.getUiSettings().setAllGesturesEnabled(false);

        boolean success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getApplicationContext(), R.raw.style_json));

        if (!success) {
            Log.e("MainAct", "Style parsing failed.");
        }
        Log.e("ddddddd", "Style parsing failed.");


        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));
        getSingleTripData();
    }

    private void getSingleTripData() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getSingleTripData(trip_id, user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;


                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject childObjct = jsonArray.getJSONObject(i);

                            LatLng origin = new LatLng(childObjct.getDouble("from_lat"), childObjct.getDouble("from_lng"));
                            LatLng destination = new LatLng(childObjct.getDouble("to_lat"), childObjct.getDouble("to_lng"));
                            ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
                            MarkerOptions options = new MarkerOptions();
                            // Setting the position of the marker
                            options.position(origin);

                            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_green);
                            Bitmap b=bitmapdraw.getBitmap();
                            Bitmap firstMarker = Bitmap.createScaledBitmap(b, 70   , 70, false);

                            Marker amarker1=    map.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(firstMarker)));
                            MarkerOptions options2 = new MarkerOptions();
                            BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.location_red);
                            Bitmap b2=bitmapdraw2.getBitmap();
                            Bitmap firstMarker2 = Bitmap.createScaledBitmap(b2, 70   , 70, false);

                            // Setting the position of the marker
                            options2.position(destination);
                            Marker amarker2=    map.addMarker(options2.icon(BitmapDescriptorFactory.fromBitmap(firstMarker2)));

                            mMarkerArray.add(amarker1);
                            mMarkerArray.add(amarker2);


                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : mMarkerArray) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            int padding = 80; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            map.moveCamera(cu);
                            map.animateCamera(cu);
                            SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(ReviewActivity.this);

                            Gson gson = new Gson();
                            String arrayListString = db.getString("mylatlon", null);
                           Type type = new TypeToken<ArrayList<List<HashMap<String,String>>>>() {}.getType();
                            ArrayList<List<HashMap<String,String>>> arrayList = gson.fromJson(arrayListString, type);
                           ArrayList points=new ArrayList();
                            PolylineOptions lineOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                            for (int j = 0; j < arrayList.size(); j++) {



                                List<HashMap<String,String>> path = arrayList.get(j);

                                for (int k = 0; k < path.size(); k++) {
                                    HashMap<String,String> point = path.get(k);

                                    double lat = Double.parseDouble(point.get("lat"));
                                    double lng = Double.parseDouble(point.get("lng"));
                                    LatLng position = new LatLng(lat, lng);

                                    points.add(position);
                                }

                                lineOptions.addAll(points);
                                lineOptions.width(10);
                                lineOptions.color(Color.parseColor("#4885ed"));
                                lineOptions.geodesic(true);
                                map.addPolyline(lineOptions);
                            }
                        }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        finish();
    }
}
