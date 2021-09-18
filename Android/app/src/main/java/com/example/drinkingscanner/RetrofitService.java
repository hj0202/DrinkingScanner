// 서버 통신
package com.example.drinkingscanner;

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
        //Call<Result> saveData(@Body PostRequest pr);
    Call<ServerResult> saveData(@FieldMap HashMap<String,Object> hm);

    @GET("preData")
    Call<ServerResult> preData(@Query("user") String user, @Query("date") String date);

    @GET("toTimeWeight")
    Call<ServerXYResult> toTimeWeight(@Query("user") String user, @Query("date") String date);

    @GET("toTimeAmount")
    Call<ServerXYResult> toTimeAmount(@Query("user") String user, @Query("date") String date);
}