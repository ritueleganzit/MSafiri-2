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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class SuccessPayment extends AppCompatActivity {

    Button cntbtn;
    SessionManager sessionManager;
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
                confirmTrip(trip_id);

            }
        });
    }

    @Override
    public void onBackPressed() {
finish();
    }

    private void confirmTrip(final String trip_id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("trip_id",trip_id);
        map.put("id",joinid);
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
