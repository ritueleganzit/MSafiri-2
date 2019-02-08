package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.Constant;
import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VerficationCodeActivity extends AppCompatActivity {

    Button submit_verification;
    PinView pinView;
    String user_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_verfication_code);
        submit_verification=findViewById(R.id.submit_verification);
        pinView=findViewById(R.id.firstPinView);
        pinView.setAnimationEnable(true);
        user_email=getIntent().getStringExtra("user_email");
        submit_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((pinView.getText().toString().equalsIgnoreCase("") || (pinView.getText().toString().length()<4)))
                {
                    Toast.makeText(VerficationCodeActivity.this, "Please enter valid OTP", Toast.LENGTH_SHORT).show();
                }
                else {
                    userSentcode();
                }
            }
        });


    }

    private void userSentcode() {
        final StringBuilder builder=new StringBuilder();
        RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(Constant.BASEURL).build();
        ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
        apiInterface.userSentcode(user_email, pinView.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;

                    while ((line=bufferedReader.readLine())!=null)
                    {
                        builder.append(line);

                    }

                    JSONObject jsonObject=new JSONObject(""+builder);
                    if (jsonObject!=null)
                    {
                        if (jsonObject.getString("status").equalsIgnoreCase("1"))
                        {

                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject childObject=jsonArray.getJSONObject(i);
                                Toast.makeText(VerficationCodeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(VerficationCodeActivity.this,ResetPasswordActivity.class).putExtra("user_id",childObject.getString("user_id")));

                            }



                        }
                        else {
                            Toast.makeText(VerficationCodeActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();


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

            }
        });
    }
}
