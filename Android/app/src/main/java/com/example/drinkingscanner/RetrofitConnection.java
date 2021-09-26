//package com.example.drinkingscanner;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;

package com.example.drinkingscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

