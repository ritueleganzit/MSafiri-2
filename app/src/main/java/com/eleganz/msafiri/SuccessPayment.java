package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.updateprofile.CallAPiActivity;
import com.eleganz.msafiri.updateprofile.GetResponse;
import com.eleganz.msafiri.utils.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class SuccessPayment extends AppCompatActivity {

    Button cntbtn;
    String id;
    SessionManager sessionManager;
    ArrayList<String> mypassenger=new ArrayList<>();
    String user_id,trip_id,driver_id,photoPath,joinid;
    CallAPiActivity callAPiActivity;
    SpotsDialog dialog;
    String URLCONFIRM = "http://itechgaints.com/M-safiri-API/confirmTrip";
    CurrentTripSession currentTripSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_payment);
        sessionManager=new SessionManager(SuccessPayment.this);

        sessionManager.checkLogin();
        mypassenger=getIntent().getStringArrayListExtra("mypassenger");

        Log.d("Successs",""+mypassenger);
        dialog = new SpotsDialog(SuccessPayment.this);
        callAPiActivity = new CallAPiActivity(this);
        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        currentTripSession=new CurrentTripSession(SuccessPayment.this);
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        photoPath=getIntent().getStringExtra("photoPath");
        joinid=getIntent().getStringExtra("joinid");
        trip_id=tripData.get(CurrentTripSession.TRIP_ID);
        driver_id=tripData.get(CurrentTripSession.DRIVER_ID);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cntbtn=findViewById(R.id.cntbtn);
        cntbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();
                //confirmTrip(trip_id);
                confirmTrips(trip_id);

            }
        });
    }

    @Override
    public void onBackPressed() {
finish();
    }

    private void confirmTrips(final String trip_id)
    {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        TypedFile typedFile=new TypedFile("multipart/form-data",new File(""+photoPath.trim()));
        apiInterface.confirmTrip(user_id, trip_id, joinid, mypassenger, "booked", typedFile, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                dialog.dismiss();
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    Log.d("SuccessPaymentScr",""+stringBuilder);

                    if (stringBuilder != null || !(stringBuilder.toString().equalsIgnoreCase(""))) {

                        Toast.makeText(SuccessPayment.this, "Saved", Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            if (jsonObject.getString("message").equalsIgnoreCase("success"))

                            {
                                CurrentTripSession currentTripSession=new CurrentTripSession(SuccessPayment.this);
                                currentTripSession.createTripSession(trip_id,driver_id,true);

                                JSONArray jsonArray=jsonObject.getJSONArray("data");
                                for (int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject child=jsonArray.getJSONObject(i);
                                    id=child.getString("id");
                                }
                                startActivity(new Intent(SuccessPayment.this,CurrentTrip.class).putExtra("id",id).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            }

                            else
                            {


                                Toast.makeText(SuccessPayment.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                }catch (Exception e)
                {
                    dialog.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();

                Toast.makeText(SuccessPayment.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void confirmTrip(final String trip_id) {
        HashMap map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("trip_id",trip_id);
        map.put("id",joinid);
        map.put("passanger_name[]",mypassenger);
        map.put("status", "booked");
        callAPiActivity.doPostWithFiles(SuccessPayment.this, map, URLCONFIRM, photoPath, "trip_screenshot", new GetResponse() {
            @Override
            public void onSuccessResult(JSONObject result) throws JSONException {
                String message = result.getString("message");
                dialog.dismiss();

                Log.d("messageimage", message+" "+joinid);
                Log.d("messageimage", photoPath);
                if (message.equalsIgnoreCase("success"))

                {
                    CurrentTripSession currentTripSession=new CurrentTripSession(SuccessPayment.this);
                    currentTripSession.createTripSession(trip_id,driver_id,true);

                    startActivity(new Intent(SuccessPayment.this,CurrentTrip.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

                }

                else
                {


                    Toast.makeText(SuccessPayment.this, ""+message, Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onFailureResult(String message) throws JSONException {
                dialog.dismiss();
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
                    Log.d("SuccessPayment",""+stringBuilder);
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))

                    {
                        startActivity(new Intent(SuccessPayment.this,PaymentActivity.class));
                        finish();
                    }

                    else
                    {

                        Toast.makeText(SuccessPayment.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        Log.d("SuccessPayment",""+jsonObject.getString("message"));

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
}
