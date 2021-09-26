package com.example.drinkingscanner;

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

public class SurveyActivity extends AppCompatActivity {

    // 레이아웃 친구들
    RadioGroup satisfyRadio;
    RadioGroup drunkRadio;
    EditText moneyInput;
    RadioGroup whoRadio;
    Button saveButton;
    TextView alcoholText;
    TextView amountText;

    // 데이터 친구들
    String user;            // 사용자
    int drunk;              // 취기 정도
    int satisfy;            // 만족도
    String alcohol;         // 술 종류
    String who;             // 누구랑
    int money;              // 돈
    int amount;             // 마신 양

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
        amountText = findViewById(R.id.amountText);

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
                Log.d("Survey", "send 취기 정도(int): " + drunk);
                Log.d("Survey", "send 만족도(int): "    + satisfy);
                Log.d("Survey", "send 술종류(string): " + alcohol);
                Log.d("Survey", "send 누구랑(string): " + who);
                Log.d("Survey", "send 돈(int): "       + money);
            }
        };
        saveButton.setOnClickListener(clickSaveButton);

        // TODO: 캘린더에서 마신 양 받아오기
        amount = 300;         // 임시 값

        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        alcohol = sharedPreferences.getString("주종","");

        amountText.setText(amount + "ml");
        alcoholText.setText(alcohol);
    }
}

