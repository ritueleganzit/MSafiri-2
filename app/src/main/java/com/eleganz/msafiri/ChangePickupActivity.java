package com.eleganz.msafiri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.eleganz.msafiri.adapter.ChangePickupAdapter;
import com.eleganz.msafiri.model.GetPickup;
import com.eleganz.msafiri.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ChangePickupActivity extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id;

    RecyclerView recyclerView;
    ArrayList<GetPickup> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pickup);

        ImageView back=findViewById(R.id.back);
        sessionManager=new SessionManager(ChangePickupActivity.this);

        sessionManager.checkLogin();


        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.change_pickup);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(ChangePickupActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ChangePickupAdapter(arrayList,ChangePickupActivity.this));

    }
}
