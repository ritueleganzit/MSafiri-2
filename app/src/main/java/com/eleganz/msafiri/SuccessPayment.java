package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;

import java.util.HashMap;

public class SuccessPayment extends AppCompatActivity {

    Button cntbtn;
    SessionManager sessionManager;
    String user_id,trip_id,driver_id;
    CurrentTripSession currentTripSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_payment);
        sessionManager=new SessionManager(SuccessPayment.this);

        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        currentTripSession=new CurrentTripSession(SuccessPayment.this);
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
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
                CurrentTripSession currentTripSession=new CurrentTripSession(SuccessPayment.this);
                currentTripSession.createTripSession(trip_id,driver_id,true);

                startActivity(new Intent(SuccessPayment.this,CurrentTrip.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
