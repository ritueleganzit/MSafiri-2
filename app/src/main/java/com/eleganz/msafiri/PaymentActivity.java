package com.eleganz.msafiri;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;

import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id,trip_id;
    CurrentTripSession currentTripSession;
    Button confirmbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ImageView back=findViewById(R.id.back);
        sessionManager=new SessionManager(PaymentActivity.this);
        currentTripSession=new CurrentTripSession(PaymentActivity.this);
        HashMap<String, String> tripData=currentTripSession.getTripDetails();
        trip_id=tripData.get(CurrentTripSession.TRIP_ID);
        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        confirmbtn=findViewById(R.id.confirmbtn);
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(PaymentActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.alert);
                // Set dialog title
                dialog.setTitle("Custom Dialog");
                Button btncnf=dialog.findViewById(R.id.btncnf);
                btncnf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startActivity(new Intent(PaymentActivity.this,SuccessPayment.class));
                        finish();
                    }
                });
                // set values for custom dialog components - text, image and button


                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();

    }
}
