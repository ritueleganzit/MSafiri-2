package com.eleganz.msafiri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import spencerstudios.com.bungeelib.Bungee;

public class ReviewActivity extends AppCompatActivity implements OnMapReadyCallback {
    MapView mapView;
    RatingBar ratingBar;
    SessionManager sessionManager;
    String user_id,trip_id,rating,comments;
    Button btnrating;
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
        btnrating=  findViewById(R.id.btnrating);
        sessionManager=new SessionManager(ReviewActivity.this);

        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);


        ratingBar= (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

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
        apiInterface.addReview(user_id, trip_id, rating, comments, new Callback<Response>() {
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
        MapsInitializer.initialize(getApplicationContext());
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getApplicationContext(), R.raw.style_json));

        if (!success) {
            Log.e("MainAct", "Style parsing failed.");
        }
        Log.e("ddddddd", "Style parsing failed.");


        LatLng india=new LatLng(20.5937,78.9629);
        googleMap.addMarker(new MarkerOptions().position(india).title("INDIA").snippet("MY INDIA"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(india));


        googleMap.animateCamera(CameraUpdateFactory.zoomTo(5.0f));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        finish();
    }
}
