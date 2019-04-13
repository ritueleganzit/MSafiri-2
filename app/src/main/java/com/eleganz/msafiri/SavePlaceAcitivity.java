package com.eleganz.msafiri;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class SavePlaceAcitivity extends AppCompatActivity {

    EditText place_address,otherhome;
    private static final int PLACE_PICKER_REQUEST = 1000;
    private GoogleApiClient mClient;
    String lat, lng;
    LinearLayout savedata;
    SessionManager sessionManager;
    String user_id;
    private String TAG="SavePlaceActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_place_acitivity);
        place_address=findViewById(R.id.place_address);
        otherhome=findViewById(R.id.otherhome);
        savedata=findViewById(R.id.savedata);
        sessionManager=new SessionManager(SavePlaceAcitivity.this);

        sessionManager.checkLogin();
        HashMap<String,String> hashMap=sessionManager.getUserDetails();
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        user_id=hashMap.get(SessionManager.USER_ID);
        place_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(SavePlaceAcitivity.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            }
        });

        savedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otherhome.getText().toString().trim().isEmpty() && place_address.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(otherhome);
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(place_address);

                    //password.requestFocus();
                }
                else if (otherhome.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(otherhome);

                    otherhome.setError("Please enter valid title");

                } else if (place_address.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(place_address);
                    ///place_address.setError("Please enter your address");
                }
                else {
                    saveData();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SavePlaceAcitivity.this,SettingsActivity.class));

        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        finish();
    }

    private void saveData() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.saveAddresss(user_id, otherhome.getText().toString(), lat, lng, place_address.getText().toString(), new Callback<Response>() {
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
                        startActivity(new Intent(SavePlaceAcitivity.this,SettingsActivity.class));
                        finish();
                    }
                    if (jsonObject.getString("message").equalsIgnoreCase("Data already exist"))
                    {

                       // Toast.makeText(SavePlaceAcitivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SavePlaceAcitivity.this,SettingsActivity.class));
                        finish();
                        //id requrired to update in response
                        //updatemyAddress();


                    }


                    Log.d(TAG,"save"+stringBuilder);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG,"save"+error.getMessage());

            }
        });
    }



    private void updatemyAddress(String id) {final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.updatemyAddress(id, user_id, otherhome.getText().toString(), lat, lng, place_address.getText().toString(), new Callback<Response>() {
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
                        startActivity(new Intent(SavePlaceAcitivity.this,SettingsActivity.class));
                        finish();
                    }

                    Log.d(TAG,"update"+stringBuilder);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG,"update"+error.getMessage());
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("%s", place.getName());
                LatLng latLng=place.getLatLng();
                lat=""+latLng.latitude;
                lng=""+latLng.longitude;
                place_address.setText(toastMsg);


            }
            else {
               // Toast.makeText(this, "Close", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
