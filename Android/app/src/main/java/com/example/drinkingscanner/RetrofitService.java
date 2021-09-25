// 서버 통신
package com.example.drinkingscanner;

import com.google.gson.annotations.JsonAdapter;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {
    @FormUrlEncoded
    @POST("saveData")
    Call<ServerResult> saveData(@FieldMap HashMap<String,Object> hm);

    @GET("preData")
    Call<ServerResult> preData(@Query("user") String user, @Query("date") String date, @Query("bestSpeed") Integer bestSpeed);

    @FormUrlEncoded
    @POST("syncData")
    Call<ServerSyncResult> syncData(@FieldMap HashMap<String,Object> hm);

    @GET("toTimeWeight")
    Call<ServerXYResult> toTimeWeight(@Query("user") String user, @Query("date") String date);

    @GET("toTimeAmount")
    Call<ServerXYResult> toTimeAmount(@Query("user") String user, @Query("date") String date);
}