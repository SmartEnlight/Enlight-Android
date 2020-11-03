package com.david0926.enlight.api;


import com.david0926.enlight.Auth.LoginBody;
import com.david0926.enlight.Auth.RegisterBody;
import com.david0926.enlight.Main1.AlertBody;
import com.david0926.enlight.Main2.CountBody;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/signin")
    @FormUrlEncoded
    Call<LoginBody> signIn(
            @Field("email") String email,
            @Field("password") String password,
            @Field("deviceId") String deviceId
    );

    @POST("auth/signup")
    @FormUrlEncoded
    Call<RegisterBody> signUp(
            @Field("email") String email,
            @Field("password") String password,
            @Field("username") String username,
            @Field("deviceId") String deviceId
    );

    @POST("device/get")
    @FormUrlEncoded
    Call<CountBody> getCount(
            @Header("Authorization") String token,
            @Field("data") String date
    );

    @POST("device/update")
    @FormUrlEncoded
    Call<AlertBody> sendAlert(
            @Field("key") String token,
            @Field("data") String date,
            @Field("db") Integer db
    );
}
