package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eleganz.msafiri.session.SessionManager;

import java.util.HashMap;

import spencerstudios.com.bungeelib.Bungee;

public class LegalActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id;
    LinearLayout copyright,terms,privacy_policy,data_providers,software_lic,location_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal2);
        sessionManager=new SessionManager(LegalActivity.this);

        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        copyright=findViewById(R.id.copyright);
        terms=findViewById(R.id.terms);
        privacy_policy=findViewById(R.id.privacy_policy);
        data_providers=findViewById(R.id.data_providers);
        software_lic=findViewById(R.id.software_lic);
        location_info=findViewById(R.id.location_info);


        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LegalActivity.this,LegalActivities.class).putExtra("title","Copyright"));

            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LegalActivity.this,LegalActivities.class).putExtra("title","Terms & Conditions"));

            }
        });
        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LegalActivity.this,LegalActivities.class).putExtra("title","Privacy Policy"));

            }
        });
        data_providers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LegalActivity.this,LegalActivities.class).putExtra("title","Data Provider"));

            }
        });
        software_lic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LegalActivity.this,LegalActivities.class).putExtra("title","Software Licenses"));

            }
        });
        location_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LegalActivity.this,LegalActivities.class).putExtra("title","Location Information"));

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
