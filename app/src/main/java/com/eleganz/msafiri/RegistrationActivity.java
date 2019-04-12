package com.eleganz.msafiri;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class RegistrationActivity extends AppCompatActivity {
    RobotoMediumTextView signtxt;
    CheckBox terms_cb;
    Button signup;
    SessionManager sessionManager;
    ProgressDialog progressDialog;

    EditText email,password,cpassword;
    ImageView logo;
    private AnimationDrawable animationDrawable;
    private ImageView progress;
    LinearLayout progressBar;
    private String TAG="RegistrationActivity";
    private String Token;
    private String devicetoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sessionManager=new SessionManager(RegistrationActivity.this);

        setContentView(R.layout.activity_registeration);
        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar);
        /*progress=findViewById(R.id.main_progress);
        progress.setBackgroundResource(R.drawable.loader);*/
//        animationDrawable = (AnimationDrawable)progress.getBackground();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword = findViewById(R.id.cpassword);
        signup = findViewById(R.id.signup);
        terms_cb = findViewById(R.id.terms_cb);
        progressDialog=new ProgressDialog(RegistrationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        signtxt=findViewById(R.id.signtxt);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        if (isValid())
                        {
                            registerUser();

                        }


            }
        });

        signtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
/*
                final String [] items = new String[] {"From Gallery", "From Camera"};
                final Integer[] icons = new Integer[] {R.drawable.envelope, R.drawable.ic_action_name};
                ListAdapter adapter = new ArrayAdapterWithIcon(getApplicationContext(), items, icons);

                new AlertDialog.Builder(RegistrationActivity.this).setTitle("Select Image")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item ) {
                                Toast.makeText(getApplicationContext(), "Item Selected: " + item, Toast.LENGTH_SHORT).show();
                            }
                        }).setCancelable(false).show();*/





            }
        });
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                Token= FirebaseInstanceId.getInstance().getToken();
                if (Token!=null)
                {
                    Log.d("mytokenn", Token);

                    devicetoken=Token;
                    StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().build();
                    StrictMode.setThreadPolicy(threadPolicy);
                    try {
                        JSONObject jsonObject=new JSONObject(Token);
                        Log.d("mytoken", jsonObject.getString("token"));
                        //devicetoken=jsonObject.getString("token");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //getLoginBoth(Token);

                }
                else
                {
                    Toast.makeText(RegistrationActivity.this, "No token", Toast.LENGTH_SHORT).show();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });t.start();
    }

    private void registerUser() {
        progressDialog.show();
        Log.d("mytokenr", Token);
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.registerUser(email.getText().toString(), password.getText().toString(), "android", Token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

progressDialog.dismiss();
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line=bufferedReader.readLine())!=null)
                    {
                        stringBuilder.append(line);
                    }
                    Log.d(TAG,"Success "+stringBuilder);

                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("status").equalsIgnoreCase("1"))
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++)
                        {

                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            sessionManager.createLoginSession("manual",email.getText().toString(),"",jsonObject1.getString("user_id"),"",password.getText().toString(),"");
                        }




                        startActivity(new Intent(RegistrationActivity.this,RegistrationContinue.class));
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                        finish();

                    }
                    else
                    {
                        Toast.makeText(RegistrationActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }




                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG,"Error "+error.getMessage());

                progressDialog.dismiss();

                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);



            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        final Animation flyin1 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyin1);
        final Animation flyin2 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyin2);
        final Animation flyin3 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyin3);
        final Animation flyin4 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyin4);
        final Animation flyin5 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyin5);
        final Animation flyin6 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyin6);
        final Animation flyin7 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyin7);
        email.startAnimation(flyin1 );

        logo.startAnimation(flyin1);

        password.startAnimation(flyin2);

        cpassword.startAnimation(flyin3);

        terms_cb.startAnimation(flyin4);

        signup.startAnimation(flyin5);

        signtxt.startAnimation(flyin6);

    }

    @Override
    public void onBackPressed() {

        final Animation flyout1 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyout1);
        final Animation flyout2 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyout2);
        final Animation flyout3 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyout3);
        final Animation flyout4 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyout4);
        final Animation flyout5 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyout5);
        final Animation flyout6 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyout6);
        final Animation flyout7 = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.flyout7);

        email.startAnimation(flyout1);

        logo.startAnimation(flyout1);

        password.startAnimation(flyout2);

        cpassword.startAnimation(flyout3);

        terms_cb.startAnimation(flyout4);

        signup.startAnimation(flyout5);

        signtxt.startAnimation(flyout6);

        flyout2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public boolean isValid() {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email.getText().toString());

          if (email.getText().toString().equals("")) {
            email.setError(""+getResources().getString(R.string.Please_enter_email));
              YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(email);
            email.requestFocus();
            return false;
        }
        else if (!matcher.matches()) {
            email.setError(""+getResources().getString(R.string.Please_Enter_Valid_Email));
              YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(email);
            email.requestFocus();
            return false;
        }
          else  if (password.getText().toString().equals("")) {
            password.setError(""+getResources().getString(R.string.Please_Enter_Password));
              YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(password);
            password.requestFocus();
            return false;
        }
        else if(password.getText().toString().trim().length() < 6)
          {
              password.setError(""+getResources().getString(R.string.Please_Enter_Password_length));
              YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(password);
              password.requestFocus();
              return false;
          }
        else if (!(password.getText().toString().equals(cpassword.getText().toString()))) {
            cpassword.setError(""+getResources().getString(R.string.Password_doesnt_match));
              YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(cpassword);

            cpassword.requestFocus();
            return false;
        }
        else if (!terms_cb.isChecked())
        {
            Toast.makeText(this, "Please agree the terms and condition", Toast.LENGTH_SHORT).show();
        return false;
        }



        return true;
    }
}
