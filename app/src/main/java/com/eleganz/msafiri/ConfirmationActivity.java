package com.eleganz.msafiri;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ahmadrosid.lib.drawroutemap.DrawMarker;
import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.updateprofile.CallAPiActivity;
import com.eleganz.msafiri.updateprofile.GetResponse;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.DirectionsJSONParser;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class ConfirmationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG ="ConfirmationActivity" ;
    MapView mapView;
    RatingBar ratingBar;
    GoogleMap map;
    String photoPath;
    SpotsDialog spotsDialog;
    Button cnf;
    CallAPiActivity callAPiActivity;
    CircleImageView photo;
    SessionManager sessionManager;
    String user_id,id,driver_id;
    SpotsDialog dialog;
    RelativeLayout  cnfrel;
    RelativeLayout dummyrel;
    CurrentTripSession currentTripSession;
    ArrayList markerArraylist=new ArrayList();
    RobotoMediumTextView fullname,vehicle_number,to_address,from_address,duration,cnf_trip_price;
    private String durationtxt;
    boolean isVisible=true;
    String URLCONFIRM = "http://itechgaints.com/M-safiri-API/confirmTrip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        ImageView back=findViewById(R.id.back);
        dialog = new SpotsDialog(ConfirmationActivity.this);
        dialog.show();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        spotsDialog = new SpotsDialog(ConfirmationActivity.this);
        mapView= (MapView) findViewById(R.id.map);
        ratingBar= (RatingBar) findViewById(R.id.ratingBar);
        fullname= (RobotoMediumTextView) findViewById(R.id.fullname);
        vehicle_number= (RobotoMediumTextView) findViewById(R.id.vehicle_number);
        from_address= (RobotoMediumTextView) findViewById(R.id.from_address);
        to_address= (RobotoMediumTextView) findViewById(R.id.to_address);
        duration= (RobotoMediumTextView) findViewById(R.id.duration);
        cnf_trip_price= (RobotoMediumTextView) findViewById(R.id.cnf_trip_price);
         currentTripSession=new CurrentTripSession(ConfirmationActivity.this);
        callAPiActivity = new CallAPiActivity(this);
        cnf= (Button) findViewById(R.id.cnf);
        sessionManager=new SessionManager(ConfirmationActivity.this);
        cnfrel=findViewById(R.id.cnfrel);
        sessionManager.checkLogin();
        photo=findViewById(R.id.photo);
        dummyrel=findViewById(R.id.dummyrel);

        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);



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
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        id=tripData.get(CurrentTripSession.TRIP_ID);

        cnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CaptureMapScreen();

               /* startActivity(new Intent(ConfirmationActivity.this,PaymentActivity.class));*/
            }
        });



    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(ConfirmationActivity.this)
                .setTitle("Are you sure you want to cancel trip?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                        currentTripSession.clearTripData();
                        spotsDialog.dismiss();
                         finish();
                    }

                    else
                    {
                        spotsDialog.dismiss();
                        Toast.makeText(ConfirmationActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

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
    private void confirmTrip(String trip_id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("trip_id",trip_id);
        map.put("status", "booked");
callAPiActivity.doPostWithFiles(ConfirmationActivity.this, map, URLCONFIRM, photoPath, "trip_screenshot", new GetResponse() {
    @Override
    public void onSuccessResult(JSONObject result) throws JSONException {
        String message = result.getString("message");

        Log.d("messageimage", message);
        if (message.equalsIgnoreCase("success"))

        {
            startActivity(new Intent(ConfirmationActivity.this,PaymentActivity.class));

        }

        else
        {

            Toast.makeText(ConfirmationActivity.this, ""+message, Toast.LENGTH_SHORT).show();


        }

    }

    @Override
    public void onFailureResult(String message) throws JSONException {

    }
});
       /* RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(BASEURL).build();
        ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
        apiInterface.confirmTrip(trip_id, user_id, "booked", new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    final StringBuilder stringBuilder=new StringBuilder();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("ConfirmationActivity",""+stringBuilder);
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))

                    {
                        startActivity(new Intent(ConfirmationActivity.this,PaymentActivity.class));
                        finish();
                    }

                    else
                    {

                        Toast.makeText(ConfirmationActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        Log.d("ConfirmationActivity",""+jsonObject.getString("message"));

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
        });*/
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
            Log.d("ConfirmationScreen",""+stringBuilder);
            JSONObject jsonObject=new JSONObject(""+stringBuilder);
            if (jsonObject.getString("message").equalsIgnoreCase("success"))
            {
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for (int i=0;i<jsonArray.length();i++) {
                     JSONObject childObjct = jsonArray.getJSONObject(i);
                    fullname.setText(childObjct.getString("fullname"));
                    from_address.setText(childObjct.getString("from_address"));
                    to_address.setText(childObjct.getString("to_address"));
                    duration.setText(childObjct.getString("calculate_time"));
                    cnf_trip_price.setText("$ "+childObjct.getString("trip_price"));
                    vehicle_number.setText(""+childObjct.getString("vehicle_name")+" "+childObjct.getString("vehicle_number"));
                    Glide.with(ConfirmationActivity.this)
                            .load(childObjct.getString("photo"))
                    .apply(new RequestOptions().placeholder(R.drawable.pr).error(R.drawable.pr))
                            .into(photo);
                    drawRoute(childObjct.getDouble("from_lat"),childObjct.getDouble("from_lng"),childObjct.getDouble("to_lat"),childObjct.getDouble("to_lng"));







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

    private void createMarker(double from_lat, double from_lng, double to_lat, double to_lng) {
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

        drawRoute(from_lat,from_lng,to_lat,to_lng);
    }

    private void drawRoute(double from_lat, double from_lng, double to_lat, double to_lng) {

        LatLng origin = new LatLng(from_lat, from_lng);
        LatLng destination = new LatLng(to_lat, to_lng);
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
        String url = getDirectionsUrl(origin, destination);
        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        googleMap.getUiSettings().setAllGesturesEnabled(true);

        map=googleMap;
        boolean success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getApplicationContext(), R.raw.style_json));

        if (!success) {
            Log.e("MainAct", "Style parsing failed.");
        }
        Log.e("ddddddd", "Style parsing failed.");


       /* map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);*/
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

            ParserTask parserTask = new ParserTask();


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
            dialog.dismiss();
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });*/
        }
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

                     durationtxt=jsonObject1.getJSONObject("duration").getString("text");


                }

            }


            Log.d("Exceptiondataaao",durationtxt);
           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    duration.setText(""+durationtxt+", away");
                 //   CaptureMapScreen();
                }
            });*/

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    public void CaptureMapScreen()
    {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                try {
                  //  Bitmap resizedbitmap1=Bitmap.createBitmap(bitmap, 0,140,500, 300);

                    print2(bitmap);
                    /*File defaultFile = new File(Environment.getExternalStorageDirectory().toString()+"/MSafiriUserMap");
                    if (!defaultFile.exists())
                        defaultFile.mkdirs();

                    String filename = System.currentTimeMillis()+".jpg";
                    File file = new File(defaultFile,filename);
                    if (file.exists()) {
                        file.delete();
                        file = new File(defaultFile,filename);
                    }


                  *//*  Log.d(TAG,photoPath);
                    Log.d(TAG,""+file);*//*
                    FileOutputStream output = new FileOutputStream(file);


                    // above "/mnt ..... png" => is a storage path (where image will be stored) + name of image you can customize as per your Requirement
               Bitmap bitmap1=    rotateBitmap(bitmap,ExifInterface.ORIENTATION_ROTATE_90);
                    bitmap1.compress(Bitmap.CompressFormat.PNG, 90, output);
                    output.flush();
                    output.close();*/
                   // rotateBitmap(bitmap,2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        Log.d("Exceptiondataaao","done");

        map.snapshot(callback);

        // myMap is object of GoogleMap +> GoogleMap myMap;
        // which is initialized in onCreate() =>
        // myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_pass_home_call)).getMap();
    }

    private void print2(Bitmap bitmapp){
        ProgressDialog dialog = new ProgressDialog(ConfirmationActivity.this);

        dialog.setMessage("Saving...");
        dialog.show();

        //bitmapp = getBitmapFromView(native_resit,native_resit.getChildAt(0).getHeight(),native_resit.getChildAt(0).getWidth());
        try {
            File defaultFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MSafiriData");
            if (!defaultFile.exists()){
                defaultFile.mkdirs();

            }

            String filename = user_id+""+ System.currentTimeMillis()+".png";
            File file = new File(defaultFile,filename);
            if (file.exists()) {
                file.delete();
                file = new File(defaultFile,filename);
            }

            FileOutputStream output = new FileOutputStream(file);
            bitmapp.compress(Bitmap.CompressFormat.PNG, 100, output);

            output.flush();
            output.close();
           // String photoPath = Environment.getExternalStorageDirectory() + "/SoberSense/" + myfile;//<---

            photoPath=""+file;

            Log.d(TAG,photoPath);

            dialog.dismiss();
            confirmTrip(id);
            Toast.makeText(ConfirmationActivity.this, "Saved!!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
            Toast.makeText(ConfirmationActivity.this, "Failed!!", Toast.LENGTH_SHORT).show();
        }
    }
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Bitmap  dstBmp;
        if (bitmap.getWidth() >= bitmap.getHeight()){

            dstBmp = Bitmap.createBitmap(
                  bitmap,
                  bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                  bitmap.getHeight(),
                  bitmap.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
        return dstBmp;
    }
}
