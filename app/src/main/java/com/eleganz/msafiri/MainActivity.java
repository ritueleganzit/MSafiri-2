package com.eleganz.msafiri;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.CurrentTripSession;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.KeyBoardEvent;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    RobotoMediumTextView loginforgot, loginregister, loginsigninwith;
    Button signbtn,login_facebook,google_btn;
    LoginButton loginButton ;
    EditText email, password;
    LinearLayout bottom;
    SessionManager sessionManager;
    ImageView logo;
    private String user_trip_status;

    private static final int RC_SIGN_IN = 007;
    private String Token;
    private String device_token;
    CurrentTripSession currentTripSession;
    Animation flyout1, flyout2;
    private AnimationDrawable animationDrawable;
    private ImageView progress;
    LinearLayout progressBar;
    CallbackManager callbackManager;
    private String TAG = "MainActivityLog";
    String str_accessToken="",devicetoken="";
ProgressDialog progressDialog;
String user_id,fname;
    private String social_name,social_profile_pic,social_email;
    private String social_lname;
  /*  GoogleSignInOptions googleSignInOptions;
    public static GoogleSignInClient mGoogleSignInClient;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);


        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        sessionManager = new SessionManager(MainActivity.this);
        final Animation popin = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pop_in);

        final Animation flyin1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin1);
        final Animation flyin2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin2);
        final Animation flyin3 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin3);
        final Animation flyin4 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin4);
        final Animation flyin5 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin5);
        final Animation flyin6 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin6);
        final Animation flyin7 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin7);
        currentTripSession = new CurrentTripSession(MainActivity.this);

        flyout1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyout1);
        flyout2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyout2);
        final Animation flyout3 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyout3);
        final Animation flyout4 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyout4);
        final Animation flyout5 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyout5);
        final Animation flyout6 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyout6);
        final Animation flyout7 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyout7);


        if (sessionManager.isLoggedIn()) {

            Log.d(TAG, "isLoggedIn ");

            HashMap<String, String> userData = sessionManager.getUserDetails();
            user_id = userData.get(SessionManager.USER_ID);
            fname=userData.get(SessionManager.FNAME);

            if (fname != null && !fname.isEmpty())
            {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            }
            /*if (currentTripSession.hasTrip()) {

                Toast.makeText(this, "dfgdg", Toast.LENGTH_SHORT).show();
                getSingleTripData();
            }
            else {*/

           // }

        }
        initViews();


        email.startAnimation(flyin1);

        password.startAnimation(flyin2);

        signbtn.startAnimation(flyin3);

        loginforgot.startAnimation(flyin4);

        loginregister.startAnimation(flyin5);

        loginsigninwith.startAnimation(flyin6);
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.eleganz.msafiri",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {    } catch (NoSuchAlgorithmException e) {    }*/
        bottom.startAnimation(flyin7);
      /*   googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);*/

        // [START initialize_auth]
        // Initialize Firebase Auth

        /*googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(MainActivity.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } *//* OnConnectionFailedListener *//*)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();*/
        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent signInIntent = mGoogleSignInClient.getSignInIntent();

                startActivityForResult(signInIntent,RC_SIGN_IN);*/
            }
        });
        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyBoardEvent.hideKeyboard(MainActivity.this);

                if (!isValideEmail(email.getText().toString()) && password.getText().toString().trim().isEmpty()) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(email);
                    YoYo.with(Techniques.Shake)
                            .duration(750)
                            .repeat(0)
                            .playOn(password);
                    email.setError("Please enter valid Email");
                    email.requestFocus();
                    password.setError("Please enter your Password");
                    //password.requestFocus();
                } else if (!isValideEmail(email.getText().toString())) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(email);
                    email.setError("Please enter valid Email");
                    email.requestFocus();

                }

                else if (password.getText().toString().trim().isEmpty()) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(password);
                    password.setError("Please enter your Password");
                    password.requestFocus();
                } else {

                    email.startAnimation(flyout1);

                    password.startAnimation(flyout2);

                    signbtn.startAnimation(flyout3);

                    loginforgot.startAnimation(flyout4);

                    loginregister.startAnimation(flyout5);

                    loginsigninwith.startAnimation(flyout6);

                    bottom.startAnimation(flyout7);

                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.startAnimation(flyin1);

                    flyout3.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            YoYo.with(Techniques.Bounce)
                                    .duration(700)
                                    .repeat(8)
                                    .playOn(logo);
                            /*progress.startAnimation(flyin1);
                            progress.setVisibility(View.VISIBLE);
                            animationDrawable.start();*/

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {



                            getUserLogin();


                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


                }

            }
        });


        loginforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyBoardEvent.hideKeyboard(MainActivity.this);

                startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                finish();
            }
        });
        loginregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyBoardEvent.hideKeyboard(MainActivity.this);

                logo.startAnimation(flyout1);

                email.startAnimation(flyout1);

                password.startAnimation(flyout2);

                signbtn.startAnimation(flyout3);

                loginforgot.startAnimation(flyout4);

                loginregister.startAnimation(flyout5);

                loginsigninwith.startAnimation(flyout6);

                bottom.startAnimation(flyout7);

                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });


        callbackManager = CallbackManager.Factory.create();
        final List< String > permissionNeeds = Arrays.asList("user_photos", "email", "public_profile");

        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                str_accessToken=loginResult.getAccessToken().getToken();
                progressDialog.show();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {@Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.d("fbobjecttt",object.toString());
                            Log.d("fbobjecttt",str_accessToken);
                            getFacebookData(object,permissionNeeds);
                            Log.i("LoginActivityyyyyyyyyy",
                                    response.toString());

                        }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"Data"+error.toString());
            }
        });


        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                Token= FirebaseInstanceId.getInstance().getToken();
                if (Token!=null)
                {
                    Log.d("mytokenn", Token);

                    device_token=Token;
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
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });t.start();
    }
    private Bundle getFacebookData(final JSONObject object, final List<String> permissionNeeds) {
        Log.d("whereeeeeee"," innnnnnnnn getFacebookData");

        final URL profile_pic;
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");

                Log.d("profile_picccccc", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
                social_profile_pic= profile_pic.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name")) {
                social_name=object.getString("first_name");
                Log.d("profile_data", "has " + object.getString("first_name") + "");
                bundle.putString("first_name", object.getString("first_name"));
            }
            if (object.has("last_name")) {
                social_lname=object.getString("last_name");
                Log.d("profile_data", "has " + object.getString("last_name") + "");
                bundle.putString("last_name", object.getString("last_name"));
            }

            if (object.has("profile_pic")) {
                Log.d("cchas", "has " + profile_pic.toString() + "");
                bundle.putString("profile_pic", profile_pic.toString());
            }
            if (object.has("gender")) {
                bundle.putString("gender", object.getString("gender"));
            }
            if (object.has("email")) {
                social_email=object.getString("email");
                Log.d("profile_data", "has " + object.getString("email") + "");
                bundle.putString("email", object.getString("email"));
                Log.d("profile_data", "a token "+str_accessToken);


                socialLogin("fblogin",social_email,social_name,social_lname,str_accessToken);
            }

            return bundle;
        }
        catch(JSONException e) {
            Log.d(TAG,"Error parsing JSON");
        }
        return null;
    }
    public void onfbClick(View v) {

        loginButton.performClick();

    }

    private void getSingleTripData() {
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        HashMap<String,String> hashMap=currentTripSession.getTripDetails();
        apiInterface.getSingleTripData(hashMap.get(CurrentTripSession.TRIP_ID), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d(TAG,""+stringBuilder);
                    JSONObject jsonObject=new JSONObject(""+stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success"))
                    {
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        for (int i=0;i<jsonArray.length();i++) {
                            JSONObject childObjct = jsonArray.getJSONObject(i);
                            user_trip_status=""+childObjct.getString("user_trip_status");

                            Log.d("jyity8u",""+user_trip_status);

                            if (user_trip_status.equalsIgnoreCase("onboard")) {

                                Log.d("jyity8u",""+user_trip_status);

                                startActivity(new Intent(MainActivity.this, CurrentTrip.class));
                            }
                            if (user_trip_status.equalsIgnoreCase("booked")) {

                                Log.d("jyity8u",""+user_trip_status);

                                startActivity(new Intent(MainActivity.this, CurrentTrip.class));
                            }
                            if (user_trip_status.equalsIgnoreCase("completed")) {

                                Log.d("jyity8u",""+user_trip_status);

                                startActivity(new Intent(MainActivity.this, ReviewActivity.class));
                            }



                        }

                    }
                    else
                    {

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
   /* private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }*/

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String splitdata[]=user.getDisplayName().split(" ");
            social_name=splitdata[0];
            social_lname=splitdata[1];
            final String idtoken=user.getUid();
            social_profile_pic=String.valueOf(user.getPhotoUrl());
            social_email=user.getEmail();
           Log.d("csd",user.getEmail());
           Log.d("csd",user.getUid());
           Log.d("csd",social_name);
           Log.d("csd",social_lname);
           Log.d("csd", String.valueOf(user.getPhotoUrl()));



            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    String Token= FirebaseInstanceId.getInstance().getToken();
                    if (Token!=null)
                    {

                        Log.d("thisismytoken", Token);
                        devicetoken=Token;
                        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().build();
                        StrictMode.setThreadPolicy(threadPolicy);
                        // getGoogleLogin(str_email,fname,lname,idtoken);

                        socialLogin("glogin",social_email,social_name,social_lname,idtoken);

                    }
                    else
                    {
                        Log.d("thisismytoken", "No token"+Token);

                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });t.start();

        } else {

        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode    , data);
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                progressDialog.show();
                firebaseAuthWithGoogle(account);
                Log.d("tokenid","requestCode == RC_SIGN_IN");
                Log.d("tokenid",""+data.getData());

               *//* if (result.isSuccess()){
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();
                    GoogleSignInAccount googleSignInAccount = result.getSignInAccount();

                    Log.d("tokenid",""+googleSignInAccount.getId()+" "+googleSignInAccount.getDisplayName()+" "+googleSignInAccount.getFamilyName()+" "+googleSignInAccount.getGivenName());
                    social_email = googleSignInAccount.getEmail();
                    //str_password=googleSignInAccount.getId();
                    final String idtoken=googleSignInAccount.getId();


                    social_name=googleSignInAccount.getGivenName();
                    social_lname=googleSignInAccount.getFamilyName();
                    social_profile_pic=googleSignInAccount.getPhotoUrl().toString();


                    //  editor.putString("profile_pic",profile_pic);
                    //tid=googleSignInAccount.getId();

                    Log.d("dataaaaaa: "," "+social_name+" "+social_lname+" "+social_profile_pic);
                    //FirebaseMessaging.getInstance().subscribeToTopic("test");
                    //FirebaseInstanceId.getInstance().getToken();
                    Thread t=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String Token= FirebaseInstanceId.getInstance().getToken();
                            if (Token!=null)
                            {

                                Log.d("thisismytoken", Token);
                                devicetoken=Token;
                                StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().build();
                                StrictMode.setThreadPolicy(threadPolicy);
                                // getGoogleLogin(str_email,fname,lname,idtoken);
                                socialLogin("glogin",social_email,social_name,social_lname,idtoken);

                            }
                            else
                            {
                                Log.d("thisismytoken", "No token"+Token);

                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });t.start();
                    //FirebaseUserAuth(googleSignInAccount);
                }
                else
                {
                    Log.d("tokenid","else");
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                }*//*
            } catch (ApiException e) {
                e.printStackTrace();
            }

            //handleSignInResult(result);
        }
*/
    }

    private void socialLogin(String login_type,String email,String fname,String lname,String token)
    {

        Log.d("sociallogin","dsfsdfs");
        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        Log.d(TAG, "" + social_lname);
        Log.d(TAG, "" + social_email);
        Log.d(TAG, "--" + social_profile_pic);

        apiInterface.socialLogin(login_type, email, fname, lname, "android", Token, token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    Log.d("sociallogin","success"+stringBuilder);

                    Log.d("stringBuilder", "" + stringBuilder);
                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("1")) {



                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            for (int i=0;i<jsonArray.length();i++)

                            {
                                JSONObject childJson=jsonArray.getJSONObject(i);
                                sessionManager.createLoginSession("social",social_email,social_lname,childJson.getString("user_id"), social_name, "", social_profile_pic);
                                Log.d(TAG, "" + childJson.getString("photo"));
                                Log.d(TAG, "" + childJson.getString("fname"));

                            }
                            progressDialog.dismiss();
                            logo.startAnimation(flyout1);

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                        }
                        else
                        {
                            LoginManager.getInstance().logOut();
                            sessionManager.logoutUser();
                            progressDialog.dismiss();
                        }


                    }
                    } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.d("sociallogin", "" + error.getMessage());
            }
        });

    }

    private void getUserLogin() {

        final StringBuilder stringBuilder = new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);

        apiInterface.loginUser(email.getText().toString(), password.getText().toString(),Token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject = new JSONObject("" + stringBuilder);
                    if (jsonObject != null) {
                        if (jsonObject.getString("message").equalsIgnoreCase("success")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                sessionManager.createLoginSession("manual",email.getText().toString(),jsonObject1.getString("lname"),jsonObject1.getString("user_id"), jsonObject1.getString("fname"), password.getText().toString(), jsonObject1.getString("photo"));
                                logo.startAnimation(flyout1);


                                progressBar.setVisibility(View.GONE);
                                progressBar.startAnimation(flyout2);

                                Log.d(TAG, "" + jsonObject1.getString("photo"));
                                Log.d(TAG, "" + jsonObject1.getString("fname"));

                                String fname=jsonObject1.getString("fname");

                                if (fname != null && !fname.isEmpty())
                                {
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                }
                                else
                                {
                                    Intent intent = new Intent(MainActivity.this, RegistrationContinue.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                }


                            }

                        } else {
                            Toast.makeText(MainActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            logo.startAnimation(flyout1);


                            progressBar.setVisibility(View.GONE);
                            progressBar.startAnimation(flyout2);
                            Intent i = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failure " + error.getMessage() + "");

                logo.startAnimation(flyout1);
                Toast.makeText(MainActivity.this, "Try Again", Toast.LENGTH_SHORT).show();

                                        /*progress.startAnimation(flyout1);
                                        animationDrawable.stop();*/
                progressBar.setVisibility(View.GONE);
                progressBar.startAnimation(flyout2);
               /* Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();*/
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });


    }

    public void initViews() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION

                        )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progressBar);
        progress = findViewById(R.id.main_progress);
        progress.setBackgroundResource(R.drawable.loader);
        animationDrawable = (AnimationDrawable) progress.getBackground();
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        signbtn = findViewById(R.id.login_signbtn);
        loginforgot = findViewById(R.id.login_forgot);
        loginregister = findViewById(R.id.login_register);
        loginsigninwith = findViewById(R.id.login_signinwith);
        bottom = findViewById(R.id.bottom);
        loginButton=findViewById(R.id.login_button);
        google_btn=findViewById(R.id.google_btn);
        login_facebook=findViewById(R.id.login_facebook);

    }


    @Override
    protected void onResume() {
        super.onResume();
        final Animation flyin1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin1);
        final Animation flyin2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin2);
        final Animation flyin3 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin3);
        final Animation flyin4 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin4);
        final Animation flyin5 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin5);
        final Animation flyin6 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin6);
        final Animation flyin7 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.flyin7);
        email.startAnimation(flyin1);

        logo.startAnimation(flyin1);

        password.startAnimation(flyin2);

        signbtn.startAnimation(flyin3);

        loginforgot.startAnimation(flyin4);

        loginregister.startAnimation(flyin5);

        loginsigninwith.startAnimation(flyin6);

        bottom.startAnimation(flyin7);

    }

    public boolean isValideEmail(String Email) {

        String Email_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_PATTERN);
        Matcher matcher = pattern.matcher(Email);
        return matcher.matches();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {

                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
