package com.eleganz.msafiri.updateprofile;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by mahadev on 7/3/17.
 */
public class CallAPiActivity extends AppCompatActivity {
    public Context context;
    public AppDialogs appDialogs;
    public Activity activity;



    public CallAPiActivity(Context context)
    {
        this.context=context;
        appDialogs = new AppDialogs(context);
        activity= (Activity) context;

    }




    public void doPostWithFiles(final Activity activity, final HashMap<String, String> map, final String url , final String mFiles, final String fileParamName, final GetResponse getApiResult) {

        if(appDialogs.isNetworkAvailable(context)) {
            appDialogs.showProgressDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final JSONObject result = JSONParser.doPostRequestWithFile(map, url, mFiles, fileParamName);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.e("Result Do Get", result.length() + "");

                            try {
                                appDialogs.hideProgressDialog();
                                getApiResult.onSuccessResult(result);

                                if (result.getString("status").equalsIgnoreCase("true")) {
                                    getApiResult.onSuccessResult(result);
                                } else {

                                    getApiResult.onFailureResult(result.getString("message"));
                                }
                            } catch (JSONException e) {
                                appDialogs.hideProgressDialog();
                                e.printStackTrace();
                                try {
                                    getApiResult.onFailureResult(e.getLocalizedMessage());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }).start();
        }
        else {

            appDialogs.showNetworkDialog(activity);
        }

    }





}
