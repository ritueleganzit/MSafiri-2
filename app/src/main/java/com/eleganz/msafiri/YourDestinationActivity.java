package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eleganz.msafiri.session.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class YourDestinationActivity extends AppCompatActivity implements OnMapReadyCallback {
    MapView mapView;
    LinearLayout findride;
    SessionManager sessionManager;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_destination);
        ImageView back=findViewById(R.id.back);
        sessionManager=new SessionManager(YourDestinationActivity.this);

        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mapView= findViewById(R.id.map);
        findride= findViewById(R.id.findride);
        findride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(YourDestinationActivity.this,FindRideActivity.class));
            }
        });
        mapView.getMapAsync(this);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());


        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getApplicationContext(), R.raw.style_json));

        if (!success) {
            Log.e("", "Style parsing failed.");
        }
        Log.e("ddddddd", "Style parsing failed.");


        LatLng india=new LatLng(20.5937,78.9629);
        googleMap.addMarker(new MarkerOptions().position(india).title("INDIA").snippet("MY INDIA").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(india));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));

    }


}
