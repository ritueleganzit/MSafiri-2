package com.eleganz.msafiri;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eleganz.msafiri.adapter.FavoritesAdapter;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.model.Addressdata;
import com.eleganz.msafiri.model.Home;
import com.eleganz.msafiri.model.Work;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;



import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import spencerstudios.com.bungeelib.Bungee;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG ="SettingsActivity" ;
    ImageView back;
    LinearLayout head;
    Button add_more_place;
    RobotoMediumTextView fname,phone,htitle,haddress,homedelete,wtitle,waddress,workdelete;
    SessionManager sessionManager;
    String user_id,image;
    private static final int PLACE_PICKER_REQUEST = 1000;
    private static final int PLACE_PICKER_REQUEST2 = 1001;
    SharedPreferences sh_imagePreference;
    SharedPreferences.Editor imagePreference;


    Home home;
    Work work;
    String lat, lng,login_type,user;
    CircleImageView profile_image;
    RecyclerView fav;
    LinearLayout addhome,addwork;
RobotoMediumTextView othersaved;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_settings);
        sharedPreferences=getSharedPreferences("address",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        sessionManager=new SessionManager(SettingsActivity.this);

        sessionManager.checkLogin();

        HashMap<String,String> hashMap=sessionManager.getUserDetails();

        user_id=hashMap.get(SessionManager.USER_ID);
        image=hashMap.get(SessionManager.PHOTO);
        login_type=hashMap.get(SessionManager.LOGIN_TYPE);
        user = hashMap.get(SessionManager.USERNAME);
        ImageView back=findViewById(R.id.back);
        fav=findViewById(R.id.fav);
        fname=findViewById(R.id.fname);
        othersaved=findViewById(R.id.othersaved);
        htitle=findViewById(R.id.htitle);
        wtitle=findViewById(R.id.wtitle);
        waddress=findViewById(R.id.waddress);
        haddress=findViewById(R.id.haddress);
        homedelete=findViewById(R.id.homedelete);
        workdelete=findViewById(R.id.workdelete);
        addhome=findViewById(R.id.addhome);
        addwork=findViewById(R.id.addwork);
        add_more_place=findViewById(R.id.add_more_place);
        profile_image=findViewById(R.id.profile_image);
        phone=findViewById(R.id.phone);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });
        head=findViewById(R.id.first);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(SettingsActivity.this,EditProfile.class);

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SettingsActivity.this,
                                profile_image,
                                ViewCompat.getTransitionName(profile_image));
                startActivity(intent, options.toBundle());

            }
        });
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(SettingsActivity.this,LinearLayoutManager.VERTICAL,false);
        fav.setLayoutManager(layoutManager);
        fav.setNestedScrollingEnabled(false);

      //
         dialog = new SpotsDialog(SettingsActivity.this);


        add_more_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,SavePlaceAcitivity.class));
finish();
            }
        });

        addwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addwork.setEnabled(false);
                Intent i = new Intent(SettingsActivity.this, SearchActivity.class);
                startActivityForResult(i, 2);
              /*  Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(SettingsActivity.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_PICKER_REQUEST2);*/
            }
        });
        addhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addhome.setEnabled(false);
                /*Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(SettingsActivity.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_PICKER_REQUEST)*/;

                Intent i = new Intent(SettingsActivity.this, SearchActivity.class);
                startActivityForResult(i, 1);
            }
        });


        homedelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, ""+sharedPreferences.getString("home",""), Toast.LENGTH_SHORT).show();

                final StringBuilder stringBuilder=new StringBuilder();
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
                final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
                apiInterface.deletemyAddress(sharedPreferences.getString("home",""),  new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        try {
                            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                            String line;
                            while ((line=bufferedReader.readLine())!=null)
                            {
                                stringBuilder.append(line);
                            }

                            JSONObject jsonObject=new JSONObject(""+stringBuilder);
                            if (jsonObject.getString("message").equalsIgnoreCase("success delete."))
                            {
                                haddress.setVisibility(View.GONE);
                                htitle.setText("Add Home");
                                homedelete.setVisibility(View.GONE);
                            }

                            Log.d(TAG,""+stringBuilder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG,""+error.getMessage());

                    }
                });

            }
        });
        workdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SettingsActivity.this, sharedPreferences.getString("work",""), Toast.LENGTH_SHORT).show();


                final StringBuilder stringBuilder=new StringBuilder();
                RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
                final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
                apiInterface.deletemyAddress(sharedPreferences.getString("work",""),  new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        try {
                            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                            String line;
                            while ((line=bufferedReader.readLine())!=null)
                            {
                                stringBuilder.append(line);
                            }

                            JSONObject jsonObject=new JSONObject(""+stringBuilder);
                            if (jsonObject.getString("message").equalsIgnoreCase("success delete."))
                            {
                                waddress.setVisibility(View.GONE);
                                wtitle.setText("Add Work");
                                workdelete.setVisibility(View.GONE);
                            }

                            Log.d(TAG,""+stringBuilder);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG,""+error.getMessage());

                    }
                });
            }
        });
        getUserData();
        getAddress();

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (image != null && !image.isEmpty())
        {
            Glide.with(getApplicationContext()).load(image).apply(RequestOptions.circleCropTransform()).into(profile_image);

        }
        addhome.setEnabled(true);
        addwork.setEnabled(true);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                lat=data.getStringExtra("resultlat");
                lng=data.getStringExtra("resultlng");
                addhome.setEnabled(true);
                haddress.setVisibility(View.VISIBLE);
                htitle.setText("Home");
                haddress.setText(result);

                saveAddress();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }  if (requestCode == 2) {
            Toast.makeText(this, "kkk", Toast.LENGTH_SHORT).show();
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                lat=data.getStringExtra("resultlat");
                lng=data.getStringExtra("resultlng");
                addwork.setEnabled(true);
                waddress.setVisibility(View.VISIBLE);
                wtitle.setText("Work");
                waddress.setText(result);
                saveworkAddress();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }


        /*if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                addhome.setEnabled(true);
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("%s", place.getName());
                LatLng latLng=place.getLatLng();
                lat=""+latLng.latitude;
                lng=""+latLng.longitude;
                Log.d(TAG,""+place.getAddress());
                Log.d(TAG,""+lng);

                if (toastMsg.equalsIgnoreCase(""))
                {

                }
                else {


                    haddress.setVisibility(View.VISIBLE);
                    htitle.setText("Home");
                    haddress.setText(toastMsg);
                    saveAddress();
                }

               *//* Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                //  List<Integer> list=place.getPlaceTypes();*//*

            }
            else {
                addhome.setEnabled(true);
                Toast.makeText(this, "Close", Toast.LENGTH_SHORT).show();
            }
        }if (requestCode == PLACE_PICKER_REQUEST2) {
            if (resultCode == RESULT_OK) {
                addwork.setEnabled(true);
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("%s", place.getName());
                LatLng latLng=place.getLatLng();
                lat=""+latLng.latitude;
                lng=""+latLng.longitude;
                Log.d(TAG,""+lat);
                Log.d(TAG,""+lng);
                if (toastMsg.equalsIgnoreCase(""))
                {

                }
                else {
                    waddress.setVisibility(View.VISIBLE);
                    wtitle.setText("Work");
                    waddress.setText(toastMsg);
                    saveworkAddress();
                }

               *//* Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                //  List<Integer> list=place.getPlaceTypes();*//*

            }
            else {
                addwork.setEnabled(true);
                Toast.makeText(this, "Close", Toast.LENGTH_SHORT).show();
            }
        }*/
    }

    private void saveworkAddress() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);

        Log.d("data",""+user_id);
        Log.d("data",""+wtitle.getText().toString());
        Log.d("data",""+waddress.getText().toString());
        Log.d("data",""+lat);
        Log.d("data",""+lng);
        apiInterface.saveAddresss(user_id, wtitle.getText().toString(), lat, lng, waddress.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line=bufferedReader.readLine())!=null)
                    {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {
                        getAddress();
                        workdelete.setVisibility(View.VISIBLE);
                    }
                    if (jsonObject.getString("status").equalsIgnoreCase("0"))


                    {
                        Toast.makeText(SettingsActivity.this, "called", Toast.LENGTH_SHORT).show();
                        //call update
                        updatemyworkAddress(sharedPreferences.getString("work",""));

                    }


                    Log.d(TAG+"-->","--->"+stringBuilder);
                    Log.d(TAG+"-->","--->"+sharedPreferences.getString("work",""));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG+"-->","--->"+error.getMessage());

            }
        });

    }

    private void getUserData() {
dialog.show();

        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getUserData(user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line=bufferedReader.readLine())!=null)
                    {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {
                        dialog.dismiss();

                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++) {

                            JSONObject jsonObject1=jsonArray.getJSONObject(i);

                            if (login_type.equalsIgnoreCase("social"))
                            {
                                Glide.with(SettingsActivity.this).load(image).apply(RequestOptions.circleCropTransform()).into(profile_image);

                            }
                            if ((jsonObject1.getString("fname")) != null && !(jsonObject1.getString("fname")).isEmpty()) {

                                {
                                    fname.setText(jsonObject1.getString("fname"));
                                    phone.setText(jsonObject1.getString("mobile_number"));

                                }
                            }else
                            {
                                fname.setText(user);

                            }


                        }

                        Log.d(TAG,"Success "+stringBuilder+"");

                    }


                    Log.d(TAG,"Success "+stringBuilder+"");



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

    private void saveAddress()
    {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
apiInterface.saveAddresss(user_id, htitle.getText().toString(), lat, lng, haddress.getText().toString(), new Callback<Response>() {
    @Override
    public void success(Response response, Response response2) {
        try {
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
            String line;
            while ((line=bufferedReader.readLine())!=null)
            {
                stringBuilder.append(line);
            }

            JSONObject jsonObject=new JSONObject(""+stringBuilder);
            if (jsonObject.getString("message").equalsIgnoreCase("success"))
            {
                getAddress();
                homedelete.setVisibility(View.VISIBLE);
            }
            if (jsonObject.getString("message").equalsIgnoreCase("Data already exist"))


            {
                //call update
                updatemyAddress(sharedPreferences.getString("home",""));
            }


            Log.d(TAG,""+stringBuilder);
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

    private void updatemyAddress(String id) {final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.updatemyAddress(id, user_id, htitle.getText().toString(), lat, lng, haddress.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line=bufferedReader.readLine())!=null)
                    {
                        stringBuilder.append(line);
                    }
                    getAddress();

                    Log.d(TAG,"update-->"+stringBuilder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void updatemyworkAddress(String id) {final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.updatemyAddress(id, user_id, wtitle.getText().toString(), lat, lng, waddress.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line=bufferedReader.readLine())!=null)
                    {
                        stringBuilder.append(line);
                    }

                    getAddress();

                    Log.d(TAG+"update",""+stringBuilder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }



    private void getAddress()
    {
        dialog.show();
        final ArrayList<Addressdata> arrayList=new ArrayList<>();

        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getmyAddress(user_id,  new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line=bufferedReader.readLine())!=null)
                    {
                        stringBuilder.append(line);
                    }


                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {

                        dialog.dismiss();
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject childObject=jsonArray.getJSONObject(i);
                            if (childObject.getString("title").replaceAll("\\'","").equalsIgnoreCase("HOME"))
                            {
                                homedelete.setVisibility(View.VISIBLE);

                                editor.putString("home",childObject.getString("id"));
                                editor.commit();
                                haddress.setVisibility(View.VISIBLE);
                                htitle.setText("Home");
                                haddress.setText(childObject.getString("address").replaceAll("\\'",""));
                            }
                            else if (childObject.getString("title").replaceAll("\\'","").equalsIgnoreCase("WORK")) {
                                workdelete.setVisibility(View.VISIBLE);
                                editor.putString("work",childObject.getString("id"));
                                editor.commit();
                                waddress.setVisibility(View.VISIBLE);
                                wtitle.setText("Work");
                                waddress.setText(childObject.getString("address").replaceAll("\\'",""));
                            }
                            else
                            {
                                Addressdata addressdata=new Addressdata(childObject.getString("title"),childObject.getString("address"),childObject.getString("id"));
                                arrayList.add(addressdata);

                            }



                        }
                        /*if (arrayList.size()>0) {
                            fav.setAdapter(new FavoritesAdapter(fav,arrayList, SettingsActivity.this));
                            othersaved.setVisibility(View.VISIBLE);
                        }
                        else {
                            othersaved.setVisibility(View.GONE);
                        }*/
                    }

                    else
                    {
                        dialog.dismiss();
                    }
                    Log.d(TAG+"rrr",""+stringBuilder +arrayList.size());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG,""+error.getMessage());
                dialog.dismiss();

                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this,HomeActivity.class));

        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        finish();
    }
}
