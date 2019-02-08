package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.SessionManager;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    RobotoMediumTextView signtxt,forgotpwdtext;
    ImageView logo;
    EditText forgotpwdedemail;
    Button forgotpwdbtn;
    SessionManager sessionManager;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_forgot_password);
        signtxt=findViewById(R.id.signtxt);
        forgotpwdtext=findViewById(R.id.forgotpwdtext);
        forgotpwdedemail=findViewById(R.id.forgotpwdedemail);
        forgotpwdbtn=findViewById(R.id.forgotpwdbtn);
        logo=findViewById(R.id.logo);
        sessionManager=new SessionManager(ForgotPasswordActivity.this);



        HashMap<String, String> userData=sessionManager.getUserDetails();
        user_id=userData.get(SessionManager.USER_ID);


        signtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this,MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                finish();
            }
        });
        final Animation flyin1 = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.flyin1);
        final Animation flyin2 = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.flyin2);
        final Animation flyin3 = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.flyin3);
        final Animation flyin4 = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.flyin4);
        final Animation flyin5 = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.flyin5);
        logo.startAnimation(flyin1);
        forgotpwdtext.startAnimation(flyin2);
        forgotpwdedemail.startAnimation(flyin3);
        forgotpwdbtn.startAnimation(flyin4);
        signtxt.startAnimation(flyin5);


        forgotpwdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValideEmail(forgotpwdedemail.getText().toString()))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(forgotpwdedemail);
                    forgotpwdedemail.setError("Please enter valid Email");
                    forgotpwdedemail.requestFocus();
                }
                else
                {
                    startActivity(new Intent(ForgotPasswordActivity.this,VerficationCodeActivity.class).putExtra("user_email",forgotpwdedemail.getText().toString()));
                    finish();

                }
            }
        });

    }


    public boolean isValideEmail(String Email) {

        String Email_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_PATTERN);
        Matcher matcher = pattern.matcher(Email);
        return matcher.matches();
    }
}
