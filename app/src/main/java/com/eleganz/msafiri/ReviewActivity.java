package com.eleganz.msafiri;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

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

public class ReviewActivity extends AppCompatActivity  {
    EditText ed_comment;
    RatingBar addrating;
    SessionManager sessionManager;
    String user_id,trip_id,comments;
    TextView review_price, btnrating,review_date,review_destination,review_drivername,review_vehicle,review_pickup;
    float rating;
    ProgressDialog progressDialog;
    CurrentTripSession currentTripSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tripreview);
        ImageView review_close=findViewById(R.id.review_close);
        review_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ed_comment=  findViewById(R.id.ed_comment);
        review_date=  findViewById(R.id.review_date);
        review_price=  findViewById(R.id.review_price);
        review_destination=  findViewById(R.id.review_destination);
        review_pickup=  findViewById(R.id.review_pickup);
        review_drivername=  findViewById(R.id.review_drivername);
        review_vehicle=  findViewById(R.id.review_vehicle);
        btnrating=  findViewById(R.id.btnrating);
        sessionManager=new SessionManager(ReviewActivity.this);
        currentTripSession=new CurrentTripSession(ReviewActivity.this);
        progressDialog=new ProgressDialog(ReviewActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        sessionManager.checkLogin();
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        trip_id=getIntent().getStringExtra("trip_id");
        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);

Log.d("ttrip_idnoti",""+trip_id);
        addrating= (RatingBar) findViewById(R.id.addrating);
        addrating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
rating=v;
            }
        });

        getSingleTripData();
        btnrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
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


                progressDialog.dismiss();
                Toast.makeText(ReviewActivity.this, "Review Send", Toast.LENGTH_SHORT).show();
                currentTripSession.clearTripData();
                startActivity(new Intent(ReviewActivity.this,HomeActivity.class));

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
            }
        });



    }



    private void getSingleTripData() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getSingleTripData(trip_id, new Callback<Response>() {
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

                            review_drivername.setText(""+childObjct.getString("fullname"));
                            review_vehicle.setText(""+childObjct.getString("vehicle_name"));
                            review_date.setText(""+childObjct.getString("datetime"));
                            review_destination.setText(""+childObjct.getString("to_address"));
                            review_pickup.setText(""+childObjct.getString("from_address"));

                            if (review_price.getText().toString().equalsIgnoreCase("null"))

                            {
                                review_price.setText("$ 0");
                            }
                            else
                            {
                                review_price.setText("$ "+childObjct.getString("trip_price"));
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
