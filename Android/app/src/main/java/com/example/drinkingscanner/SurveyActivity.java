package com.example.drinkingscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyActivity extends AppCompatActivity {

    // 레이아웃 친구들
    RadioGroup satisfyRadio;
    RadioGroup drunkRadio;
    EditText moneyInput;
    RadioGroup whoRadio;
    Button saveButton;
    TextView alcoholText;
    //TextView amountText;

    // 데이터 친구들
    int drunk;              // 취기 정도
    int satisfy;            // 만족도
    String alcohol;         // 술 종류
    String who;             // 누구랑
    int money;              // 돈
    //int amount;             // 마신 양

    // 서버 통신 친구들
    RetrofitService service;
    String user,date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Log.d("Survey", "Hi Survey Activity");

        satisfyRadio = findViewById(R.id.satisfyRadio);
        drunkRadio = findViewById(R.id.drunkRadio);
        moneyInput = findViewById(R.id.moneyInput);
        whoRadio = findViewById(R.id.whoRadio);
        saveButton = findViewById(R.id.saveButton);
        alcoholText = findViewById(R.id.alcoholText);
        //amountText = findViewById(R.id.amountText);

        //서버 통신 친구들
        service = RetrofitConnection.getInstance().getService();
        user = getUser();
        date = getDate();

        // 저장하기 버튼 클릭
        View.OnClickListener clickSaveButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int satisfyId = satisfyRadio.getCheckedRadioButtonId();
                RadioButton satisfyResult = findViewById(satisfyId);

                int drunkId = drunkRadio.getCheckedRadioButtonId();
                RadioButton drunkResult = findViewById(drunkId);

                int whoId = whoRadio.getCheckedRadioButtonId();
                RadioButton whoResult = findViewById(whoId);

                satisfy = Integer.parseInt(satisfyResult.getText().toString());
                drunk = Integer.parseInt(drunkResult.getText().toString());
                money = Integer.parseInt(moneyInput.getText().toString());
                who = whoResult.getText().toString();

                // TODO: 아래 5개 데이터를 데이터베이스에 정보 보내기
                survey(user,date,drunk,satisfy,alcohol,who,money);
                Intent intent = new Intent(getApplicationContext(), BeforeConnectSelectActivity.class);
                startActivity(intent);
                finish();
            }
        };
        saveButton.setOnClickListener(clickSaveButton);

        // TODO: 캘린더에서 마신 양 받아오기
        // amount = 300;         // 임시 값

        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        alcohol = sharedPreferences.getString("주종","");

        //amountText.setText(amount + "ml");
        alcoholText.setText(alcohol);
    }
    
    // 서버 통신 - 서버로 survey 요청
    public void survey(String user,String date,Integer drunk,Integer satisfy,String alcohol,String who,Integer money) {
        //디버그
        Log.d("Survey", "send 취기 정도(int): " + drunk);
        Log.d("Survey", "send 만족도(int): "    + satisfy);
        Log.d("Survey", "send 술종류(string): " + alcohol);
        Log.d("Survey", "send 누구랑(string): " + who);
        Log.d("Survey", "send 돈(int): "       + money);
        // 요청시 보내는 데이터
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("user",user);
        hm.put("date",date);
        hm.put("drunkenness",drunk);
        hm.put("satisfaction",satisfy);
        hm.put("alcohol",alcohol);
        hm.put("who",who);
        hm.put("money",money);

        // 요청
        Call<ServerResult> call = service.survey(hm);

        // 비동기 처리
        call.enqueue(new Callback<ServerResult>() {
            @Override
            public void onResponse(Call<ServerResult> call, Response<ServerResult> response) {
                if(!response.isSuccessful()) {
                    Log.d("testText","On Response Error {" +
                            "code: " + response.code() + ", " +
                            "status: " + response.body().getStatus() + "}\n");
                    return;
                }

                Log.d("testText","On Response {" +
                        "code: " + response.code() + ", " +
                        "status: " + response.body().getStatus() + "}\n");
            }
            @Override
            public void onFailure(Call<ServerResult> call, Throwable t) {
                Log.d("testText","On Failure: \n"+ call + "\n" + t + "\n");
            }
        });
    }

    // 서버 통신 - 사용자 이름 구하기
    public String getUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        return sharedPreferences.getString("닉네임","");
    }

    //서버 통신 - 오늘 날짜 구하기
    public String getDate() {
        long now = System.currentTimeMillis();
        Date myDate = new Date(now);
        SimpleDateFormat myFormat = new SimpleDateFormat("MMdd");
        return myFormat.format(myDate);
    }
}

