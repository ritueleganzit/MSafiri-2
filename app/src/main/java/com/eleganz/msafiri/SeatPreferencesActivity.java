package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.eleganz.msafiri.session.SessionManager;

import java.util.HashMap;

public class SeatPreferencesActivity extends AppCompatActivity {
Button nextbtn;
    SessionManager sessionManager;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_preferences);
        ImageView back=findViewById(R.id.back);
        sessionManager=new SessionManager(SeatPreferencesActivity.this);

        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        nextbtn=findViewById(R.id.nextbtn);
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SeatPreferencesActivity.this,ConfirmationActivity.class));
            }
        });

    }
}
