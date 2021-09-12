package com.example.drinkingscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_name;
    private Button btn_reg;
    private String shared = "file";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_name = (EditText) findViewById(R.id.et_name);
        btn_reg = (Button) findViewById(R.id.btn_register);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                else {
                    SharedPreferences sharedPreferences = getSharedPreferences(shared, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String name = et_name.getText().toString();
                    editor.putString("닉네임", name);
                    editor.commit();

                    Toast.makeText(getApplicationContext(), name+"님 환영합니다!", Toast.LENGTH_SHORT).show();

                    finish();
                }

            }
        });

    }
}
