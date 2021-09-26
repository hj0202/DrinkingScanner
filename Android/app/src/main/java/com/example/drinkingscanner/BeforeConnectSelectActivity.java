package com.example.drinkingscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class BeforeConnectSelectActivity extends Activity {

    private Button btnSelectSoju;
    private Button btnSelectBeer;
    private Button btnSelectWine;
    private Button btnSelectMakkulli;
    private Button btnSelectLiquor;
    private Button btnSelectElse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_connect_select);

        btnSelectSoju = (Button) findViewById(R.id.btn_sel_soju);
        btnSelectBeer = (Button) findViewById(R.id.btn_sel_beer);
        btnSelectWine = (Button) findViewById(R.id.btn_sel_wine);
        btnSelectMakkulli = (Button) findViewById(R.id.btn_sel_makkulli);
        btnSelectLiquor = (Button) findViewById(R.id.btn_sel_liquor);
        btnSelectElse = (Button) findViewById(R.id.btn_sel_else);

        // Button 클릭(기타 제외) -> SharedPreference 저장 후 기기 연결 페이지 이동
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Survey", "Click");
                // SharedPreference 변수 선언
                switch (v.getId()){
                    case R.id.btn_sel_soju:
                        Toast.makeText(getApplicationContext(), "소주 선택!", Toast.LENGTH_SHORT).show();
                        SetSharedPreference("Soju");
                        break;

                    case R.id.btn_sel_beer:
                        Toast.makeText(getApplicationContext(), "맥주 선택!", Toast.LENGTH_SHORT).show();
                        SetSharedPreference("Beer");
                        break;

                    case R.id.btn_sel_wine:
                        Toast.makeText(getApplicationContext(), "와인 선택!", Toast.LENGTH_SHORT).show();
                        SetSharedPreference("Wine");

                        break;

                    case R.id.btn_sel_makkulli:
                        Toast.makeText(getApplicationContext(), "막걸리 선택!", Toast.LENGTH_SHORT).show();
                        SetSharedPreference("Makgeolli");
                        break;

                    case R.id.btn_sel_liquor:
                        Toast.makeText(getApplicationContext(), "양주 선택!", Toast.LENGTH_SHORT).show();
                        SetSharedPreference("Liquor");
                        break;
                }
                // 기기 연결 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), ConnectBluetoothActivity.class);
                startActivity(intent);
            }
        };

        btnSelectSoju.setOnClickListener(onClickListener);
        btnSelectBeer.setOnClickListener(onClickListener);
        btnSelectWine.setOnClickListener(onClickListener);
        btnSelectMakkulli.setOnClickListener(onClickListener);
        btnSelectLiquor.setOnClickListener(onClickListener);
        btnSelectElse.setOnClickListener(onClickListener);

        // 기타 Button 클릭 -> 술 정보 입력 팝업 생성 후 입력하면 기기 연결 페이지 이동
        btnSelectElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "팝업 입력 후, 기기 연결로 갑시다!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SetSharedPreference(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("주종", value);
        editor.commit();
    }
}
