package com.example.drinkingscanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DayReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_report);

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        setTitle(date);
    }
}
