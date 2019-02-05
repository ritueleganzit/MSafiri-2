package com.eleganz.msafiri;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class TellYourDriverActivity extends AppCompatActivity {
    private static final String TAG = "TellYourDriverActivity";
    SessionManager sessionManager;
    CurrentTripSession currentTripSession;
    String user_id, trip_id, driver_id;
    EditText musicpref, medicalhistory;
    Button btnsubmitpref;
    SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tell_your_driver);
        ImageView back = findViewById(R.id.back);
        musicpref = findViewById(R.id.musicpref);
        medicalhistory = findViewById(R.id.medicalhistory);
        btnsubmitpref = findViewById(R.id.btnsubmitpref);
        sessionManager = new SessionManager(TellYourDriverActivity.this);
        currentTripSession = new CurrentTripSession(TellYourDriverActivity.this);
        sessionManager.checkLogin();
        spotsDialog = new SpotsDialog(TellYourDriverActivity.this);

        HashMap<String, String> userData = sessionManager.getUserDetails();
        user_id = userData.get(SessionManager.USER_ID);
        currentTripSession = new CurrentTripSession(TellYourDriverActivity.this);
        HashMap<String, String> tripData = currentTripSession.getTripDetails();
        trip_id = tripData.get(CurrentTripSession.TRIP_ID);
        driver_id = tripData.get(CurrentTripSession.DRIVER_ID);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnsubmitpref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotsDialog.show();
                addPreferences();
            }
        });
        getPreferencesData();

    }

    private void getPreferencesData() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getPreferences(trip_id, user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject = new JSONObject("" + stringBuilder);
                    if (jsonObject!=null)
                    {
                        if (jsonObject.getString("status").equalsIgnoreCase("1"))
                        {
                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject childObject=jsonArray.getJSONObject(i);
                                musicpref.setText(""+childObject.getString("music"));
                                medicalhistory.setText(""+childObject.getString("medical"));
                            }
                        }
                    }




                    Log.d(TAG, "" + stringBuilder);








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

    public void addPreferences() {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.addPreferences(driver_id, trip_id, user_id, musicpref.getText().toString(), medicalhistory.getText().toString(), new Callback<Response>() {
            @SuppressLint("LongLogTag")
            @Override
            public void success(Response response, Response response2) {
                final StringBuilder stringBuilder = new StringBuilder();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject = new JSONObject("" + stringBuilder);


                    Log.d(TAG, "" + stringBuilder);
                    finish();
                    spotsDialog.dismiss();
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
