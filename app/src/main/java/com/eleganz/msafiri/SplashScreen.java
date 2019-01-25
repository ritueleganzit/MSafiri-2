package com.eleganz.msafiri;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        logo=(ImageView) findViewById(R.id.logo);


                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
               /* ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashActivity.this, logo, "logo_transition");*/
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                        finish();


    }
}
