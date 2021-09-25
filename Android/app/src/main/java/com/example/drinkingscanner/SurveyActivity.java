package com.example.drinkingscanner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SurveyActivity extends AppCompatActivity {

    // 레이아웃 친구들
    RadioGroup satisfyRadio;
    RadioGroup drunkRadio;
    EditText moneyInput;
    EditText personInput;
    Button saveButton;

    // 보내야 하는 데이터
    String user;            // 사용자
    int drunk;              // 취기 정도
    int satisfy;            // 만족도
    String alcohol;         // 술 종류
    String person;          // 누구랑
    int money;              // 돈

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Log.d("Survey", "Hi Survey Activity");

        satisfyRadio = findViewById(R.id.satisfyRadio);
        drunkRadio = findViewById(R.id.drunkRadio);
        moneyInput = findViewById(R.id.moneyInput);
        personInput = findViewById(R.id.personInput);
        saveButton = findViewById(R.id.saveButton);

        View.OnClickListener clickSaveButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int satisfyId = satisfyRadio.getCheckedRadioButtonId();
                RadioButton satisfyResult = findViewById(satisfyId);

                int drunkId = drunkRadio.getCheckedRadioButtonId();
                RadioButton drunkResult = findViewById(drunkId);

                satisfy = Integer.parseInt(satisfyResult.getText().toString());
                drunk = Integer.parseInt(drunkResult.getText().toString());
                money = Integer.parseInt(moneyInput.getText().toString());
                person = personInput.getText().toString();

                // TODO: 아래 5개 데이터를 데이터베이스에 정보 보내기 
                Log.d("Survey", "send 취기 정도(int): " + drunk);
                Log.d("Survey", "send 만족도(int): " + satisfy);
                Log.d("Survey", "send 술종류(string): " + alcohol);
                Log.d("Survey", "send 누구랑(string): " + person);
                Log.d("Survey", "send 돈(int): " + money);
            }
        };

        saveButton.setOnClickListener(clickSaveButton);

        // TODO: 유저 이름 받아오기
        user = "현지";                // 임시 값

        // TODO: 캘린더에서 마신 양, 주종 받아오기
        int allAmount = 300;         // 임시 값
        alcohol = "소주";             // 임시 값
    }
}

