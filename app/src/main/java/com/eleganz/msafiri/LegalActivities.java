package com.eleganz.msafiri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eleganz.msafiri.session.SessionManager;

import java.util.HashMap;

public class LegalActivities extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id;
    TextView title,content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_activities);
        sessionManager=new SessionManager(LegalActivities.this);

        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);
        title=findViewById(R.id.title);
        content=findViewById(R.id.content);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        String txt_title=getIntent().getStringExtra("title");
        title.setText(txt_title);
        content.setText(txt_title);

    }
}
