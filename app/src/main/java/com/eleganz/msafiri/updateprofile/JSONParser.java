package com.eleganz.msafiri.updateprofile;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by codesture18 on 7/4/2017.
 */

public class JSONParser {

    private static Response response;




    public static JSONObject doPostRequestWithFile(HashMap<String, String> data,
                                                   String url,
                                                   String imageList,
                                                   String fileParamName) {



        try {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

            Log.e("Method", "=======");
            RequestBody requestBody;
            MultipartBuilder mBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

            for (String key : data.keySet()) {
                String value = data.get(key);
                Log.e("Key Values", key + "-----------------" + value);

                mBuilder.addFormDataPart(key, value);

            }

                File f = new File(imageList);

                Log.e("File Name 322", f.getName() + "===========");
                if (f.exists()) {
                    Log.e("File Exits", "===================");
                    mBuilder.addFormDataPart(fileParamName, f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
                }

            requestBody = mBuilder.build();


            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();


            String result = response.body().string();

            Log.e("Response", result + "");
            return new JSONObject(result);

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("API", "Error: " + e.getLocalizedMessage());
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("status", "false");
                jsonObject.put("message", e.getLocalizedMessage());
                return jsonObject;

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("API", "Other Error: " + e.getMessage());
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("status", "false");
                jsonObject.put("message", e.getLocalizedMessage());
                return jsonObject;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
