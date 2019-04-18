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
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganz.msafiri.fragment.MySampleFabFragment;
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
    LinearLayout txtno_data;
    private ShimmerFrameLayout shimmerFrameLayout;
    SpotsDialog dialog;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    SessionManager sessionManager;
    String user_id,from_title,to_title,get_date,seats;
    BottomSheetDialog dialogFrag;
    String data="";

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
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogFrag  = new BottomSheetDialog(FindRideActivity.this);
                dialogFrag.setContentView(R.layout.layout_filter);
                RadioGroup rg;

                rg=dialogFrag.findViewById(R.id.rg);
                RelativeLayout rl_content = (RelativeLayout) dialogFrag.findViewById(R.id.rl_content);
                LinearLayout ll_buttons = (LinearLayout) dialogFrag.findViewById(R.id.ll_buttons);
                dialogFrag.findViewById(R.id.imgbtn_apply)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogFrag.dismiss();
                                Log.d("answer",""+data);

                                if(data != null && !data.isEmpty()){
                                    if (data.equalsIgnoreCase("high"))
                                    {
                                        price="high";
                                        rating="";
                                        if (arrayList.size()>0)
                                        {
                                            arrayList.clear();
                                        }
                                        getSortPriceTrip();
                                    }  if (data.equalsIgnoreCase("low"))
                                    {
                                        price="low";
                                        rating="";
                                        if (arrayList.size()>0)
                                        {
                                            arrayList.clear();
                                        }
                                        getSortPriceTrip();        }

                                    if (data.equalsIgnoreCase("rating"))
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

                            }
                        });

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (R.id.rd_pricehigh==checkedId)
                        {

                            data="high";
                        }if (R.id.rd_pricelow==checkedId)
                        {

                            data="low";
                        }
                        else if (R.id.rd_rating==checkedId)
                        {
                            data="Rating";

                        }
                    }
                });
                dialogFrag.show();
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
        Log.d("mydata",""+user_id+" fghf "+from_title+" yfy "+to_title+" zsdfs "+get_date+seats);

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
                Log.d("mydata",""+error.getMessage());
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
        //Toast.makeText(this, "answer"+time, Toast.LENGTH_SHORT).show();

        Log.d("answer",""+time);

        if(time != null && !time.isEmpty()){
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

            view=inflater.inflate(R.layout.booking_row,null);

            CircleImageView imageView=view.findViewById(R.id.circleview);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_fall_down);
            view.startAnimation(animation);
            final DriverData driverData=arrayList.get(i);

          //  RatingBar rating=view.findViewById(R.id.rating);
            TextView vehicle_name=view.findViewById(R.id.vehicle_name);
            TextView pickup_address=view.findViewById(R.id.pickup_address);
            TextView pickup_destination=view.findViewById(R.id.pickup_destination);
            final TextView trip_price=view.findViewById(R.id.trip_price);
            TextView datetime=view.findViewById(R.id.datetime);
            Glide.with(context).load(driverData.getPhoto()).apply(new RequestOptions().placeholder(R.drawable.pr).error(R.drawable.pr)).into(imageView);
            pickup_destination.setSelected(true);
            pickup_address.setText(driverData.getPickup());
            pickup_destination.setText(driverData.getDestination());
            vehicle_name.setText(driverData.getVehiclename());

            if (driverData.getPrice().equalsIgnoreCase("null"))
            {
                trip_price.setText("$ 0");

            }
            else
            {
                trip_price.setText("$ "+driverData.getPrice());
            }


            vehicle_name.setSelected(true);
            if ((driverData.getRating()!=null))
            {
                if ((driverData.getRating().equalsIgnoreCase("null")))
                {

                }else {
                    Log.d("ratinggg",""+driverData.getRating());
                    //rating.setRating(Float.parseFloat(driverData.getRating()));
                }



            }

            datetime.setText(""+parseDateToddMMyyyy(driverData.getTime()));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Toast.makeText(FindRideActivity.this, ""+driverData.getDriver_id()+" ", Toast.LENGTH_SHORT).show();
                    //api call

                    Log.d("find",""+driverData.getTrip_id());

dialog.show();
                    bookRide(driverData.getDriver_id(),driverData.getTrip_id());

                   /* startActivity(new Intent(context,FindRideActivity.class).putExtra("trip_id",driverData.getTrip_id())
                    .putExtra("driver_id",driverData.getDriver_id()));*/

                }
            });
            return view;
        }

        private void bookRide(final String driver_id, final String trip_id) {
            RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(BASEURL).build();
            ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
            apiInterface.joinTrip(trip_id, user_id, driver_id,"0", new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    try {
                        final StringBuilder stringBuilder=new StringBuilder();
                        dialog.dismiss();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        Log.d("FindRideActivity",""+stringBuilder);
                        JSONObject jsonObject=new JSONObject(""+stringBuilder);
                        if (jsonObject.getString("message").equalsIgnoreCase("success"))

                        {

                            String joinid="";

                            JSONArray jsonArray=jsonObject.getJSONArray("data");

                            for (int i=0;i<jsonArray.length();i++)
                            {

                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                joinid=jsonObject1.getString("id");

                            }

                            CurrentTripSession currentTripSession=new CurrentTripSession(FindRideActivity.this);
                            currentTripSession.createTripSession(trip_id,driver_id,false);
                            startActivity(new Intent(context,ConfirmationActivity.class).putExtra("joinid",joinid)
                            .putExtra("seats",seats));

                        }

                        else
                        {

                            Toast.makeText(FindRideActivity.this, "You cannot book this ride", Toast.LENGTH_SHORT).show();

                            Log.d("FindRideActivity",""+jsonObject.getString("message"));

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
