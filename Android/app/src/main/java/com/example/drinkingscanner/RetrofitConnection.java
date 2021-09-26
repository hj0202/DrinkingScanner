package com.example.drinkingscanner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConnection extends AppCompatActivity {
    private static Gson gson = new GsonBuilder().setLenient().create();
    private static Retrofit retrofit  = new Retrofit.Builder()
            .baseUrl("http://172.21.152.63:8000/apiserver/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    private static RetrofitService service = retrofit.create(RetrofitService.class);
    private static RetrofitConnection instance = null;

    public static RetrofitConnection getInstance() {
        if (instance == null) { instance= new RetrofitConnection(); }
        return instance;
    }

    public RetrofitService getService() {
        return service;
    }
}

