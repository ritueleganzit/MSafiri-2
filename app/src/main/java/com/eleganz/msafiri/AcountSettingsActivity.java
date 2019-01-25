package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AcountSettingsActivity extends AppCompatActivity {

    LinearLayout head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount_settings);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        head=findViewById(R.id.head);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AcountSettingsActivity.this, "ccccccc", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AcountSettingsActivity.this,EditProfile.class));
            }
        });
    }
}
