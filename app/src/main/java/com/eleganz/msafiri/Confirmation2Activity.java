package com.eleganz.msafiri;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class Confirmation2Activity extends AppCompatActivity implements OnMapReadyCallback{

    Button confirmbtn;
    MapView mapView;
    RatingBar ratingBar;
    Button cnf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation2);

        confirmbtn=findViewById(R.id.confirmbtn);
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Confirmation2Activity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.alert_confirm);
                // Set dialog title
                dialog.setTitle("Custom Dialog");
                Button btncnf=dialog.findViewById(R.id.btncnf);
                Button btncncl=dialog.findViewById(R.id.btncncl);
                btncnf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startActivity(new Intent(Confirmation2Activity.this,PaymentActivity.class));
                    }
                });
                btncncl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                // set values for custom dialog components - text, image and button


                dialog.show();
            }
        });

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mapView= (MapView) findViewById(R.id.map);
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
}
