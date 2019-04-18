package com.eleganz.msafiri;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.eleganz.msafiri.utils.DirectionsJSONParser;
import com.eleganz.msafiri.utils.HistoryData;
import com.eleganz.msafiri.utils.SensorService;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

public class TripBooked extends AppCompatActivity implements OnMapReadyCallback {
    SessionManager sessionManager;
    String user_id;
    SpotsDialog spotsDialog;
    RelativeLayout dummyrel;
    boolean isVisible=true;
    TimerTask mTimerTask;
    final Handler handler = new Handler();
    Timer t = new Timer();
    MapView mapView;
    private String TAG="TripBooked";
    HistoryData historyData;
    CircleImageView driver_fab;
    RelativeLayout  cnfrel;
    RobotoMediumTextView pickuploc,pickuplocaddress,destloc,destlocaddress,comment,trip_rate;
    RatingBar ratingBar;
    RobotoMediumTextView driver_txt1,vehicle_tx1,calculate_time;
    GoogleMap map;
    Runnable runnable;
    CurrentTripSession currentTripSession;
    private int nCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_booked);
        ImageView back=findViewById(R.id.back);
        mapView= (MapView) findViewById(R.id.map);
        sessionManager=new SessionManager(TripBooked.this);
        currentTripSession=new CurrentTripSession(TripBooked.this);
        sessionManager.checkLogin();

        Log.d(TAG,"oncreate");
        cnfrel=findViewById(R.id.cnfrel);
        dummyrel=findViewById(R.id.temp);
        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        ratingBar=findViewById(R.id.ratingBar);
        pickuploc=findViewById(R.id.pickuploc);
        pickuplocaddress=findViewById(R.id.pickuplocaddress);
        destlocaddress=findViewById(R.id.destlocaddress);
        destloc=findViewById(R.id.destloc);
        driver_fab=findViewById(R.id.driver_fab);
        driver_txt1=findViewById(R.id.driver_txt1);
        vehicle_tx1=findViewById(R.id.vehicle_tx1);
        calculate_time=findViewById(R.id.calculate_time);
        spotsDialog=new SpotsDialog(TripBooked.this);

        trip_rate=findViewById(R.id.trip_rate);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mapView.getMapAsync(this);

        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        historyData= (HistoryData) getIntent().getSerializableExtra("historyData");
        if (historyData.getRating().equalsIgnoreCase("")) {

            Log.d("kkklll", "--" + historyData.getFrom_lng());

        }
        else {
            ratingBar.setRating(Float.parseFloat(historyData.getRating()));
        }

        if (historyData.getUser_trip_status().equalsIgnoreCase("booked"))
        {

            // Toast.makeText(this, ""+historyData.getUser_trip_status(), Toast.LENGTH_SHORT).show();
            // doTimerTask();
            startService(new Intent(this, SensorService.class));
        }

        else {
            // Toast.makeText(this, ""+historyData.getUser_trip_status(), Toast.LENGTH_SHORT).show();
            stopService(new Intent(this, SensorService.class));
            //stopTask();
        }


            /*Toast.makeText(this, "called", Toast.LENGTH_SHORT).show();
             runnable=new Runnable() {
                @Override
                public void run() {
                    if (historyData.getUser_trip_status().equalsIgnoreCase("booked"))

                    {
                        Log.d(TAG + "mmmmm", "if" + System.currentTimeMillis());

                        getSingleTripData();
                        handler1.postDelayed(this, 3000);
                    } else {
                        handler1.removeCallbacks(this);
                        handler1.removeCallbacks(runnable);

                        handler1.removeCallbacksAndMessages(runnable);
                        handler1.removeMessages(0);
                        Log.d(TAG + "mmmmm", "" + System.currentTimeMillis());


                    }
                }
            };
             handler1.postDelayed(runnable,3000);*/




        //
        // pickuploc.setText(""+historyData.getFrom_title());

        pickuplocaddress.setText(""+historyData.getFrom_address());
        //destloc.setText(""+historyData.getTo_title());
        destlocaddress.setText(""+historyData.getTo_address());
        driver_txt1.setText(""+historyData.getFullname());
        vehicle_tx1.setText(""+historyData.getVehicle_name());
        calculate_time.setText(""+historyData.getCalculate_time());


        findViewById(R.id.tellbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TripBooked.this, TellYourDriverActivity.class)
                        .putExtra("tripbookedid",historyData.getTrip_id())
                        .putExtra("tripdriverid",historyData.getDriver_id()));

            }
        });
        findViewById(R.id.cancelride).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(TripBooked.this);
                input.setHint("Reason");

                AlertDialog alertDialog=  new AlertDialog.Builder(TripBooked.this)
                        .setMessage("Are you sure you want to cancel trip?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                spotsDialog.show();

                                cancelTrip(historyData.getTrip_id(),""+input.getText().toString());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setView(input)
                        .setCancelable(false)
                        .show();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                Typeface face= Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
                textView.setTypeface(face);



            }
        });
        if (historyData.getTrip_price()!=null && !historyData.getTrip_price().isEmpty())

        {
            trip_rate.setText("$ "+historyData.getTrip_price());


        }
        if (historyData.getTrip_price().equalsIgnoreCase("null"))
        {
            trip_rate.setText("$ 0" );

        }



        Glide.with(TripBooked.this).load(""+historyData.getPhoto()).apply(new RequestOptions().placeholder(R.drawable.male)).into(driver_fab);
        // trip_rate.setText(""+historyData.getTrip_price());
    }

    private void cancelTrip(String trip_id,String reason) {
        RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(BASEURL).build();
        ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
        apiInterface.cancelTrips(trip_id, user_id,"cancel","",reason, new Callback<Response>() {
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
                        startActivity(new Intent(TripBooked.this, CancelRideActivity.class));
                        finish();
                    }

                    else
                    {
                        spotsDialog.dismiss();

                        Toast.makeText(TripBooked.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();


            Double currentLatitude = intent.getDoubleExtra("latitude", 0);

            Double currentLongitude = intent.getDoubleExtra("longitude", 0);

            //  ... react to local broadcast message

        }

    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        spotsDialog.show();
        map = googleMap;
        MapsInitializer.initialize(getApplicationContext());
        map.getUiSettings().setAllGesturesEnabled(false);

        boolean success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getApplicationContext(), R.raw.style_json));

        if (!success) {
            Log.e("MainAct", "Style parsing failed.");
        }
        Log.e("ddddddd", "Style parsing failed.");
        Log.d("drawRoute", "--" + historyData.getFrom_lng());
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                drawRoute(historyData.getFrom_lat(), historyData.getFrom_lng(), historyData.getTo_lat(), historyData.getTo_lng());
            }
        });
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




    }
    private class DownloadTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            TripBooked.ParserTask parserTask = new TripBooked.ParserTask();


            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String,String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String,String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String,String>>> result) {
            ArrayList points  = new ArrayList();
            SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(TripBooked.this);

            SharedPreferences.Editor collection = db.edit();
            Gson gson = new Gson();
            String arrayList1 = gson.toJson(result);

            collection.putString("mylatlon", arrayList1);
            collection.commit();
            Log.d("mylatlon",""+result.size());
            PolylineOptions lineOptions = new PolylineOptions();
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {



                List<HashMap<String,String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#4885ed"));
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            spotsDialog.dismiss();
        }
    }


    private void drawRoute(String from_lat, String from_lng, String to_lat, String to_lng) {

        LatLng origin = new LatLng(Double.parseDouble(from_lat), Double.parseDouble(from_lng));
        LatLng destination = new LatLng(Double.parseDouble(to_lat), Double.parseDouble(to_lng));
        ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
        MarkerOptions options = new MarkerOptions();
        // Setting the position of the marker
        options.position(origin);

        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.location_green);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap firstMarker = Bitmap.createScaledBitmap(b, 70   , 70, false);


        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.location_red);
        Bitmap b2=bitmapdraw2.getBitmap();
        Bitmap firstMarker2 = Bitmap.createScaledBitmap(b2, 70   , 70, false);



        Marker amarker1=    map.addMarker(options.icon(BitmapDescriptorFactory.fromBitmap(firstMarker)));
        MarkerOptions options2 = new MarkerOptions();

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
        String url = getDirectionsUrl(origin, destination);
        TripBooked.DownloadTask downloadTask = new TripBooked.DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"&key=AIzaSyAlfxVsErR9EwJwhLaEmEJnm2kZMhMrovU";

        Log.d("theurl",url);
        return url;
    }



    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("Exceptiondata", data);

            JSONObject jsonObject=new JSONObject(""+data);
            JSONArray jsonArray=jsonObject.getJSONArray("routes");

            for (int i=0;i<jsonArray.length();i++)
            {

                JSONObject childObject=jsonArray.getJSONObject(i);

                JSONArray jsonArray1=childObject.getJSONArray("legs");
                for (int j=0;j<jsonArray1.length();j++)
                {
                    JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                    Log.d("Exceptiondataaa",jsonObject1.getJSONObject("distance").getString("text"));

                    // durationtxt=jsonObject1.getJSONObject("duration").getString("text");


                }

            }


            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void getSingleTripData() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getSingleTripData(historyData.getTrip_id(), new Callback<Response>() {
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

}

