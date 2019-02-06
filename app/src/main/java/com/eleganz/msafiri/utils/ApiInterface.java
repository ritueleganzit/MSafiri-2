package com.eleganz.msafiri.utils;

import android.telecom.Call;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by eleganz on 1/11/18.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("/addUser")
    public void registerUser(
            @Field("user_email") String user_email,
            @Field("password") String password,
            @Field("device_id") String device_id,
            @Field("device_token") String device_token,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST("/loginUser")
    public void loginUser(
            @Field("user_email") String user_email,
            @Field("password") String password,
            @Field("device_token") String device_token,
            Callback<Response> callback
    );


    @FormUrlEncoded
    @POST("/socialLogin")
    public void socialLogin(
            @Field("login_type") String login_type,
            @Field("user_email") String user_email,
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("device_id") String device_id,
            @Field("device_token") String device_token,
            @Field("token") String token,
            Callback<Response> callback
    );


    @FormUrlEncoded
    @POST("/getUser")
    public void getUserData(
            @Field("user_id") String user_id,
            Callback<Response> callback
    );



    @FormUrlEncoded
    @POST("/myAddress")
    public void saveAddresss(
            @Field("user_id") String user_id,
            @Field("title") String title,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("address") String address,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST("/addPreferences")
    public void addPreferences(
            @Field("driver_id") String driver_id,
            @Field("trip_id") String trip_id,
            @Field("user_id") String user_id,
            @Field("music") String music,
            @Field("medical") String medical,
            Callback<Response> callback
    );


    @FormUrlEncoded
    @POST("/getPreferences")
    public void getPreferences(
            @Field("trip_id") String trip_id,
            @Field("user_id") String user_id,
            Callback<Response> callback


    );

    @FormUrlEncoded
    @POST("/getmyAddress")
    void getmyAddress(
            @Field("user_id") String user_id,

            Callback<Response> callback

    );

    @FormUrlEncoded
    @POST("/getdriverTrips")
    void getdriverTrips(
            @Field("user_id") String user_id,

            @Field("from_title") String from_title,
            @Field("to_title") String to_title,
            @Field("get_date") String get_date,
            @Field("seats") String seats,

            Callback<Response> callback

    );

    @FormUrlEncoded
    @POST("/addReview")
    void addReview(
            @Field("user_id") String user_id,
            @Field("trip_id") String trip_id,

            @Field("ratting") String ratting,
            @Field("comments") String comments,

            Callback<Response> callback

    );
    @FormUrlEncoded
    @POST("/userTrips")
    void userTrips(
            @Field("user_id") String user_id,


            Callback<Response> callback

    );

    @FormUrlEncoded
    @POST("/singleTrip")
    void getSingleTripData(
            @Field("id") String id,
            @Field("user_id") String user_id,


            Callback<Response> callback

    );

    @FormUrlEncoded

    @POST("/allFromlist")
    void getAllTripData(
            @Field("") String s,

            Callback<Response> callback

    );


    @FormUrlEncoded
    @POST("/allTolist")
    void getallTolist(
            @Field("from_title") String s,

            Callback<Response> callback

    );

    @FormUrlEncoded
    @POST("/deletemyAddress")
    void deletemyAddress(
            @Field("id") String id,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST("/userChangepassword")
    void userChangepassword(
            @Field("user_id") String id,
            @Field("password") String password,
            Callback<Response> callback
    );


    @FormUrlEncoded
    @POST("/joinTrip")
    void joinTrip(
            @Field("trip_id") String trip_id,
            @Field("user_id") String user_id,
            @Field("driver_id") String driver_id,
            Callback<Response> callback

    );


  @FormUrlEncoded
    @POST("/confirmTrip")
    void confirmTrip(
            @Field("trip_id") String trip_id,
            @Field("user_id") String user_id,

            @Field("status") String status,
            Callback<Response> callback

    );


    @FormUrlEncoded
    @POST("/updatemyAddress")
    void updatemyAddress(
            @Field("id") String id,
            @Field("user_id") String user_id,
            @Field("title") String title,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("address") String address,
            Callback<Response> callback
    );

}
