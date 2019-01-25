package com.eleganz.msafiri.updateprofile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by codesture18 on 7/4/2017.
 */

public interface GetResponse {
    void onSuccessResult(JSONObject result) throws JSONException;
    void onFailureResult(String message) throws JSONException;
}
