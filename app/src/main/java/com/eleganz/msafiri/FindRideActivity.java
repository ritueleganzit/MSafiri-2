package com.eleganz.msafiri;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganz.msafiri.fragment.MySampleFabFragment;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.model.DriverData;
import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import spencerstudios.com.bungeelib.Bungee;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class FindRideActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks,MySampleFabFragment.OnCompleteListener {
    ImageView filterimg;
    int img[]={R.drawable.kriti,R.drawable.kriti,R.drawable.kriti,R.drawable.kriti,R.drawable.kriti,R.drawable.kriti,R.drawable.kriti,R.drawable.kriti,R.drawable.kriti};
    ListView findridelist;
    FloatingActionButton fab;
    String price,rating;
    TextView txtno_data;
    private ShimmerFrameLayout shimmerFrameLayout;
    SpotsDialog dialog;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    SessionManager sessionManager;
    String user_id,from_title,to_title,get_date,seats;
    MySampleFabFragment dialogFrag;
    ArrayList<DriverData> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                Bungee.swipeRight(FindRideActivity.this);
            }
        });
        sessionManager=new SessionManager(FindRideActivity.this);

        sessionManager.checkLogin();
        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        dialog = new SpotsDialog(FindRideActivity.this);
        dialog.show();
        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
from_title=getIntent().getStringExtra("from_title");
        to_title=getIntent().getStringExtra("to_title");
get_date=getIntent().getStringExtra("get_date");
        seats=getIntent().getStringExtra("seats");
        /*filterimg= (ImageView) findViewById(R.id.filterimg);*/
        findridelist=findViewById(R.id.findridelist);
        txtno_data=findViewById(R.id.txtno_data);
        fab=findViewById(R.id.fab1);
        dialogFrag  = MySampleFabFragment.newInstance();
        dialogFrag.setParentFab(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
               /* final Dialog dialog = new Dialog(FindRideActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.filterdialog);
                // Set dialog title

                // set values for custom dialog components - text, image and button


                dialog.show();*/
            }
        });

                   getdriverTrips();



    }
    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }
    public void getSortPriceTrip(){
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);


apiInterface.getSortByPriceTrip(user_id,from_title, to_title,get_date,seats,price, new Callback<Response>() {
    @Override
    public void success(Response response, Response response2) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }


            JSONObject jsonObject=new JSONObject(""+stringBuilder);

            Log.d("mydata",""+stringBuilder);
            Log.d("mydata",""+user_id+""+from_title+""+get_date+seats+" "+price);
            if (jsonObject.getString("message").equalsIgnoreCase("success"))
            {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                JSONArray jsonArray=jsonObject.getJSONArray("data");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject childObjct=jsonArray.getJSONObject(i);
                    DriverData driverData=new DriverData(
                            childObjct.getString("id"),
                            childObjct.getString("driver_id"),

                            childObjct.getString("photo")
                            ,childObjct.getString("vehicle_name")+" "+childObjct.getString("vehicle_number")
                            ,childObjct.getString("from_title")
                            ,childObjct.getString("to_title")
                            ,childObjct.getString("datetime")
                            ,childObjct.getString("ratting")
                            ,childObjct.getString("trip_price"));
                    arrayList.add(driverData);
                }
                MyAdapter myAdapter=new MyAdapter(arrayList,FindRideActivity.this);

                findridelist.setAdapter(myAdapter);
                dialog.dismiss();
            }

            if (jsonObject.getString("status").equalsIgnoreCase("0"))

            {
                dialog.dismiss();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                txtno_data.setVisibility(View.VISIBLE);
            }
            else {
                dialog.dismiss();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                txtno_data.setVisibility(View.GONE);
            }
            Log.d("mydataaa",""+stringBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void failure(RetrofitError error) {
        shimmerFrameLayout.stopShimmer();
        dialog.dismiss();
        shimmerFrameLayout.setVisibility(View.GONE);
        txtno_data.setVisibility(View.VISIBLE);
    }
});


    }
    private void getdriverTrips() {

        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);

        apiInterface.getdriverTrips(user_id,from_title, to_title,get_date,seats, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }


                    JSONObject jsonObject=new JSONObject(""+stringBuilder);

                    Log.d("mydata",""+stringBuilder);
                    Log.d("mydata",""+user_id+""+from_title+""+get_date+seats);
if (jsonObject.getString("message").equalsIgnoreCase("success"))
{
    shimmerFrameLayout.stopShimmer();
    shimmerFrameLayout.setVisibility(View.GONE);

    JSONArray jsonArray=jsonObject.getJSONArray("data");
    for (int i=0;i<jsonArray.length();i++)
    {
        JSONObject childObjct=jsonArray.getJSONObject(i);
        DriverData driverData=new DriverData(
                childObjct.getString("id"),
                childObjct.getString("driver_id"),

                childObjct.getString("photo")
        ,childObjct.getString("vehicle_name")+" "+childObjct.getString("vehicle_number")
        ,childObjct.getString("from_title")
        ,childObjct.getString("to_title")
        ,childObjct.getString("datetime")
        ,childObjct.getString("ratting")
        ,childObjct.getString("trip_price"));
        arrayList.add(driverData);
    }
    MyAdapter myAdapter=new MyAdapter(arrayList,FindRideActivity.this);

    findridelist.setAdapter(myAdapter);
dialog.dismiss();
}

if (jsonObject.getString("status").equalsIgnoreCase("0"))

{
    dialog.dismiss();
    shimmerFrameLayout.stopShimmer();
    shimmerFrameLayout.setVisibility(View.GONE);
    txtno_data.setVisibility(View.VISIBLE);
}
else {
    dialog.dismiss();
    shimmerFrameLayout.stopShimmer();
    shimmerFrameLayout.setVisibility(View.GONE);
    txtno_data.setVisibility(View.GONE);
}
                    Log.d("mydataaa",""+stringBuilder);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

                @Override
            public void failure(RetrofitError error) {
                    shimmerFrameLayout.stopShimmer();
                    dialog.dismiss();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    txtno_data.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResult(Object result) {

    }

    @Override
    public void onComplete(String time) {
        Toast.makeText(this, "answer"+time, Toast.LENGTH_SHORT).show();

        Log.d("answer",""+time);


        if (time.equalsIgnoreCase("high"))
        {
         price="high";
         rating="";
         if (arrayList.size()>0)
         {
             arrayList.clear();
         }
         getSortPriceTrip();
        }  if (time.equalsIgnoreCase("low"))
        {
         price="low";
         rating="";
         if (arrayList.size()>0)
         {
             arrayList.clear();
         }
            getSortPriceTrip();        }

        if (time.equalsIgnoreCase("rating"))
        {
            rating="yes";
            price="";
            if (arrayList.size()>0)
            {
                arrayList.clear();
            }
            getSortRatingTrip();
        }
    }

    private void getSortRatingTrip() {


        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);


        apiInterface.getSortByRatingTrip(user_id,from_title, to_title,get_date,seats,rating, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }


                    JSONObject jsonObject=new JSONObject(""+stringBuilder);

                    Log.d("mydata",""+stringBuilder);
                    Log.d("mydata",""+user_id+""+from_title+""+get_date+seats);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject childObjct=jsonArray.getJSONObject(i);
                            DriverData driverData=new DriverData(
                                    childObjct.getString("id"),
                                    childObjct.getString("driver_id"),

                                    childObjct.getString("photo")
                                    ,childObjct.getString("vehicle_name")+" "+childObjct.getString("vehicle_number")
                                    ,childObjct.getString("from_title")
                                    ,childObjct.getString("to_title")
                                    ,childObjct.getString("datetime")
                                    ,childObjct.getString("ratting")
                                    ,childObjct.getString("trip_price"));
                            arrayList.add(driverData);
                        }
                        MyAdapter myAdapter=new MyAdapter(arrayList,FindRideActivity.this);

                        findridelist.setAdapter(myAdapter);
                        dialog.dismiss();
                    }

                    if (jsonObject.getString("status").equalsIgnoreCase("0"))

                    {
                        dialog.dismiss();
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        txtno_data.setVisibility(View.VISIBLE);
                    }
                    else {
                        dialog.dismiss();
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        txtno_data.setVisibility(View.GONE);
                    }
                    Log.d("mydataaa",""+stringBuilder);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                shimmerFrameLayout.stopShimmer();
                dialog.dismiss();
                shimmerFrameLayout.setVisibility(View.GONE);
                txtno_data.setVisibility(View.VISIBLE);
            }
        });


    }

    public class MyAdapter extends BaseAdapter
    {

        ArrayList<DriverData> arrayList;
        Context context;

        public MyAdapter(ArrayList<DriverData> arrayList, Context context) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            view=inflater.inflate(R.layout.selectride,null);

            CircleImageView imageView=view.findViewById(R.id.circleview);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            view.startAnimation(animation);
            final DriverData driverData=arrayList.get(i);
            Button book=view.findViewById(R.id.book);
            RatingBar rating=view.findViewById(R.id.rating);
            RobotoMediumTextView vehicle_name=view.findViewById(R.id.vehicle_name);
            RobotoMediumTextView pickup_destination=view.findViewById(R.id.pickup_destination);
            RobotoMediumTextView trip_price=view.findViewById(R.id.trip_price);
            RobotoMediumTextView datetime=view.findViewById(R.id.datetime);
            Glide.with(context).load(driverData.getPhoto()).apply(new RequestOptions().placeholder(R.drawable.pr).error(R.drawable.pr)).into(imageView);
            pickup_destination.setSelected(true);
            pickup_destination.setText(driverData.getPickup()+"-"+driverData.getDestination());
            vehicle_name.setText(driverData.getVehiclename());
            trip_price.setText("$ "+driverData.getPrice());
            if ((driverData.getRating()!=null))
            {
                if ((driverData.getRating().equalsIgnoreCase("null")))
                {

                }else {
                    Log.d("ratinggg",""+driverData.getRating());
                    rating.setRating(Float.parseFloat(driverData.getRating()));
                }



            }

            datetime.setText(""+parseDateToddMMyyyy(driverData.getTime()));
            book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Toast.makeText(FindRideActivity.this, ""+driverData.getDriver_id()+" ", Toast.LENGTH_SHORT).show();
                    //api call
                    bookRide(driverData.getDriver_id(),driverData.getTrip_id());
                   /* startActivity(new Intent(context,ConfirmationActivity.class).putExtra("trip_id",driverData.getTrip_id())
                    .putExtra("driver_id",driverData.getDriver_id()));*/

                }
            });
            return view;
        }
        public String parseDateToddMMyyyy(String time) {
            String inputPattern = "yyyy-MM-dd HH:mm:ss";
            String outputPattern = "dd MMM ,yyyy h:mm a";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date date = null;
            String str = null;

            try {
                date = inputFormat.parse(time);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return str;
        }
        private void bookRide(final String driver_id, final String trip_id) {
            RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(BASEURL).build();
            ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
            apiInterface.joinTrip(trip_id, user_id, driver_id, new Callback<Response>() {
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

                            CurrentTripSession currentTripSession=new CurrentTripSession(context);
                            currentTripSession.createTripSession(trip_id,driver_id,false);
                            startActivity(new Intent(context,ConfirmationActivity.class)
                                  );
                            finish();
                        }

                        else
                        {

                            Toast.makeText(context, "You cannot book this ride", Toast.LENGTH_SHORT).show();

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
            });
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dialogFrag.isAdded()) {
            dialogFrag.dismiss();
            dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
        }
    }

    public void initbottomsheet()
    {
        LayoutInflater inflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sheet=inflater.inflate(R.layout.filterdialog,null);

        final BottomSheetDialog dialog=new BottomSheetDialog(FindRideActivity.this);
        Button btn=sheet.findViewById(R.id.btn);
        dialog.setContentView(sheet);
        dialog.setCancelable(true);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

}
