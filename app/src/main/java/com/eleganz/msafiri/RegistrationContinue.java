package com.eleganz.msafiri;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.lib.RobotoMediumTextView;
import com.eleganz.msafiri.session.SessionManager;
import com.eleganz.msafiri.updateprofile.CallAPiActivity;
import com.eleganz.msafiri.updateprofile.GetResponse;
import com.eleganz.msafiri.utils.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.eleganz.msafiri.utils.Constant.BASEURL;

public class RegistrationContinue extends AppCompatActivity {
    SessionManager sessionManager;
    String user_id, password, image ,name,login_type,emailtxt,lnametxt;
    EditText email, fname, lname, phone;
    String mediapath = "";
    CallAPiActivity callAPiActivity;
    ImageView profile_pic;
    File file;
    RobotoMediumTextView updateData;
    int GALLERY_CODE = 100;
    int CAMERA_CODE = 2;
    EditText ch_password;
    SharedPreferences sh_imagePreference;
    SharedPreferences.Editor imagePreference;
    String URLCHANGEPASSWORD = " http://itechgaints.com/M-safiri-API/userChangepassword";
    private String TAG = "EditProfile";
    SpotsDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_profile);

        sessionManager = new SessionManager(RegistrationContinue.this);
        sh_imagePreference = getSharedPreferences("imagepref", MODE_PRIVATE);
        imagePreference = sh_imagePreference.edit();


        sessionManager.checkLogin();
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        password = hashMap.get(SessionManager.PASSWORD);
image=hashMap.get(SessionManager.PHOTO);
name=hashMap.get(SessionManager.FNAME);
emailtxt=hashMap.get(SessionManager.EMAIL);
        lnametxt=hashMap.get(SessionManager.LNAME);
        login_type=hashMap.get(SessionManager.LOGIN_TYPE);
        user_id = hashMap.get(SessionManager.USER_ID);
        callAPiActivity = new CallAPiActivity(this);
        email = findViewById(R.id.email);
        profile_pic = findViewById(R.id.profile_pic);
        fname = findViewById(R.id.fname);
        ch_password = findViewById(R.id.ch_password);
        lname = findViewById(R.id.lname);
        phone = findViewById(R.id.phone);
        updateData = findViewById(R.id.updateData);
        ImageView back = findViewById(R.id.back);
        if (login_type.equalsIgnoreCase("social")) {
            ch_password.setVisibility(View.GONE);
            email.setText(emailtxt);
            lname.setText(lnametxt);

        }
        else {
            ch_password.setVisibility(View.VISIBLE);
            ch_password.setText(password);
        }

        if (image != null && !image.isEmpty())
        {
            Glide.with(getApplicationContext()).load(image).apply(RequestOptions.circleCropTransform()).into(profile_pic);

        }
        dialog = new SpotsDialog(RegistrationContinue.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        getUserData();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {
                    if (mediapath != null && !mediapath.isEmpty())
                    {
                        dialog.show();
                        editDataWithImage();
                    }
                    else {
                        dialog.show();
                        editData();
                    }
                }

            }
        });
        Log.d("imagecheck",""+image);

        ch_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(RegistrationContinue.this);
                dialog.setContentView(R.layout.dialog_layout);

                final EditText old = dialog.findViewById(R.id.oldpassword);
                final EditText neww = dialog.findViewById(R.id.newpassword);
                final EditText confirm = dialog.findViewById(R.id.cpassword);
                TextView ok = dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if ((old.getText().toString().trim().equalsIgnoreCase("")))
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(old);
                            old.setError("Please enter old Password");
                            old.requestFocus();
                        }
                        else if (!(old.getText().toString().trim().equalsIgnoreCase(password)))
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(old);
                            old.setError("Wrong Password");
                            old.requestFocus();
                        }
                        else if (neww.getText().toString().trim().isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(neww);
                            neww.setError("Please enter new Password");
                            neww.requestFocus();
                        }
                        else if (!(confirm.getText().toString().equalsIgnoreCase(neww.getText().toString())))
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(0)
                                    .playOn(confirm);
                            confirm.setError("Password does not match");
                            confirm.requestFocus();
                        }
                        else {
                            changePassword(confirm.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    if (ContextCompat.checkSelfPermission(RegistrationContinue.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                        openImageChooser();
                    }
                }

            }
        });
    }

    public void changePassword(String password)
    {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://itechgaints.com/M-safiri-API/").build();
        final ApiInterface myInterface = restAdapter.create(ApiInterface.class);
        myInterface.userChangepassword(user_id,password, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                final StringBuilder stringBuilder = new StringBuilder();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d("stringBuilder", "" + stringBuilder);
                    //Toast.makeText(RegistrationActivity.this, "sssss" + stringBuilder, Toast.LENGTH_SHORT).show();

                    if (stringBuilder != null || !stringBuilder.toString().equalsIgnoreCase("")) {

                        JSONObject jsonObject = new JSONObject("" + stringBuilder);
                        String status = jsonObject.getString("status");
                        JSONArray jsonArray = null;
                        if(status.equalsIgnoreCase("1"))
                        {
                            jsonArray = jsonObject.getJSONArray("data");
                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                String password = jsonObject1.getString("password");
                                ch_password.setText(password);
                                sessionManager.updatePassword(password);



                            }
                        }
                        else
                        {

                        }

                    }
                    else
                    {

                    }


                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public boolean isValid() {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email.getText().toString());

        if (fname.getText().toString().equals("")) {
            fname.setError(""+getResources().getString(R.string.Please_enter_fname));
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(fname);
            fname.requestFocus();
            return false;
        }
      else   if (email.getText().toString().equals("")) {
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
        else  if (ch_password.getText().toString().equals("")) {
            ch_password.setError(""+getResources().getString(R.string.Please_Enter_Password));
            YoYo.with(Techniques.Shake).duration(700).repeat(0).playOn(ch_password);
            ch_password.requestFocus();
            return false;
        }




        return true;
    }
    private void openImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.d("file_size", "mediapath : " + "onactivityres" + " ---- " + "");

            if (requestCode == GALLERY_CODE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int clumnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediapath = cursor.getString(clumnIndex);
                file = new File(mediapath);
                int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                Log.d("file_size", "" + file_size);


                cursor.close();
                Glide.with(RegistrationContinue.this).load(mediapath).apply(RequestOptions.circleCropTransform()).into(profile_pic);

                Log.d("file_size", "mediapath : " + mediapath + " ---- " + file_size);


            }
            if (requestCode == CAMERA_CODE) {

            }

        } else if (requestCode == RESULT_CANCELED) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void editData(){
       /* HashMap<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("mobile_number", phone.getText().toString());
        map.put("gender", "");
        map.put("fname", fname.getText().toString());
        map.put("lname", lname.getText().toString());
        map.put("country", "");
        map.put("user_email", email.getText().toString());*/

        final StringBuilder stringBuilder=new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.updateProfile(user_id, phone.getText().toString(), "", fname.getText().toString(), lname.getText().toString(), "", email.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                dialog.dismiss();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.d(TAG, "Success " + stringBuilder);

                    if(stringBuilder != null || !(stringBuilder.toString().equals("")))
                    {
                        JSONObject result=new JSONObject(""+stringBuilder);
                        String message = result.getString("message");
                        if (message.equalsIgnoreCase("success"))
                        {
                            JSONArray jsonArray = result.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Log.d("mobile_number", ""+jsonObject.getString("mobile_number"));

                                if (jsonObject.getString("mobile_number") != null && !jsonObject.getString("mobile_number").isEmpty())
                                {
                                    phone.setText(jsonObject.getString("mobile_number"));
                                }



                                    sessionManager.updateImage(jsonObject.getString("photo"));
                                    HashMap<String, String> hashMap = sessionManager.getUserDetails();
                                    image = hashMap.get(SessionManager.PHOTO);

                                    Glide.with(getApplicationContext())
                                            .load(image)

                                            .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.pr))


                                            .into(profile_pic);

                                    startActivity(new Intent(RegistrationContinue.this,HomeActivity.class));
                                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                                    finish();

                            }
                        }

                    }


                }
                catch (Exception e)
                {
                    dialog.dismiss();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.dismiss();
            }
        });


    }

    private void editDataWithImage() {


        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("mobile_number", phone.getText().toString());
        map.put("gender", "");
        map.put("fname", fname.getText().toString());
        map.put("lname", lname.getText().toString());
        map.put("country", "");
        map.put("user_email", email.getText().toString());
        callAPiActivity.doPostWithFiles(this, map, "http://itechgaints.com/M-safiri-API/updateProfile", mediapath, "photo", new GetResponse() {
            @Override
            public void onSuccessResult(JSONObject result) throws JSONException {
                String message = result.getString("message");
                dialog.dismiss();
                Log.d("messageimage", ""+result);
                if (message.equalsIgnoreCase("success")) {

                    JSONArray jsonArray = result.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Log.d("mobile_number", ""+jsonObject.getString("mobile_number"));

                        if (jsonObject.getString("mobile_number") != null && !jsonObject.getString("mobile_number").isEmpty())
                        {
                            phone.setText(jsonObject.getString("mobile_number"));
                        }

                        if (mediapath!=null) {

                            Log.d("messageimage", ""+jsonObject.getString("photo"));

                            sessionManager.updateImage(jsonObject.getString("photo"));
                            HashMap<String, String> hashMap = sessionManager.getUserDetails();
                            image = hashMap.get(SessionManager.PHOTO);

                            Glide.with(getApplicationContext())
                                    .load(image)

                                    .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.pr))

                                    .into(profile_pic);
                        }
                        else {
                            Glide.with(getApplicationContext())
                                    .load(image)

                                    .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.pr))

                                    .into(profile_pic);
                        }

                        startActivity(new Intent(RegistrationContinue.this,HomeActivity.class));
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        finish();
                    }
                }


            }

            @Override
            public void onFailureResult(String message) throws JSONException {
                Log.d("message failue", message);
                dialog.dismiss();
            }
        });


    }

    private void getUserData() {

        final StringBuilder stringBuilder = new StringBuilder();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASEURL).build();
        final ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getUserData(user_id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    JSONObject jsonObject = new JSONObject("" + stringBuilder);
                    if (jsonObject.getString("message").equalsIgnoreCase("success")) {

                        dialog.dismiss();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);



                            if (login_type.equalsIgnoreCase("manual")) {
                                sessionManager.updateImage(jsonObject1.getString("photo"));

                                Glide.with(getApplicationContext()).load(jsonObject1.getString("photo")).apply(RequestOptions.circleCropTransform()).into(profile_pic);
                                email.setText(jsonObject1.getString("user_email"));
                                fname.setText(jsonObject1.getString("fname"));
                                lname.setText(jsonObject1.getString("lname"));
                                phone.setText(jsonObject1.getString("mobile_number"));

                            }

                            else {
                                if (jsonObject1.getString("mobile_number").equalsIgnoreCase(""))
                                {

                                }
                                else
                                {
                                    phone.setText(jsonObject1.getString("mobile_number"));
                                }if (jsonObject1.getString("lname").equalsIgnoreCase(""))
                                {

                                }
                                else
                                {
                                    lname.setText(jsonObject1.getString("lname"));
                                }
                                fname.setText(name);
                            }
                        }

                        Log.d(TAG, "Success " + stringBuilder + "");

                    }


                    Log.d(TAG, "Success " + stringBuilder + "");


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(RegistrationContinue.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationContinue.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.support.v7.app.AlertDialog.Builder(RegistrationContinue.this)
                        .setTitle("Gallery Permission")
                        .setMessage("Allow app to use this permission to upload image")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(RegistrationContinue.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(RegistrationContinue.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean isValideEmail(String Email) {

        String Email_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_PATTERN);
        Matcher matcher = pattern.matcher(Email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
