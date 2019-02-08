package com.eleganz.msafiri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eleganz.msafiri.utils.ApiInterface;
import com.eleganz.msafiri.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText ednewpassword,edcnfnewpassword;
    Button forgotpwdcnf;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_reset_password);
        ednewpassword=findViewById(R.id.ednewpassword);
        edcnfnewpassword=findViewById(R.id.edcnfnewpassword);
        forgotpwdcnf=findViewById(R.id.forgotpwdcnf);
        user_id=getIntent().getStringExtra("user_id");
        forgotpwdcnf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ednewpassword.getText().toString().trim().isEmpty())
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(ednewpassword);
                    ednewpassword.setError("Please enter new Password");
                    ednewpassword.requestFocus();
                }
                else if (!(edcnfnewpassword.getText().toString().equalsIgnoreCase(edcnfnewpassword.getText().toString())))
                {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .repeat(0)
                            .playOn(edcnfnewpassword);
                    edcnfnewpassword.setError("Password does not match");
                    edcnfnewpassword.requestFocus();
                }
                else
                {
                    userResetpassword();
                }
            }
        });
    }


    public void userResetpassword()
    {
        Toast.makeText(this, ""+user_id, Toast.LENGTH_SHORT).show();
        final StringBuilder builder=new StringBuilder();
        RestAdapter restAdapter=new RestAdapter.Builder().setEndpoint(Constant.BASEURL).build();
        ApiInterface apiInterface=restAdapter.create(ApiInterface.class);
        apiInterface.userResetpassword(user_id, ednewpassword.getText().toString(), new Callback<Response>() {
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

                            Toast.makeText(ResetPasswordActivity.this, "Password has been reset", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ResetPasswordActivity.this,MainActivity.class));


                        }
                        else {
                            Toast.makeText(ResetPasswordActivity.this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();


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
