package com.eleganz.msafiri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.eleganz.msafiri.session.SessionManager;

import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class HelpActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ImageView back=findViewById(R.id.back);
        sessionManager=new SessionManager(HelpActivity.this);

        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

        finish();
    }
}
