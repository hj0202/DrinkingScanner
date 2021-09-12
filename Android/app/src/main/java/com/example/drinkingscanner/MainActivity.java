package com.example.drinkingscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// 서비스 선택 화면이 main 화면입니다:D
public class MainActivity extends AppCompatActivity {

    private Button btnMoveConnect;
    private Button btnMoveReport;
    private String shared = "file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMoveConnect = (Button) findViewById(R.id.btn_move_connect_page);
        btnMoveReport = (Button) findViewById(R.id.btn_move_report_page);

        // 만약, 등록된 이름이 없다면, RegisterActivity 띄우기
        SharedPreferences sharedPreferences = getSharedPreferences(shared, MODE_PRIVATE);
        String name = sharedPreferences.getString("닉네임", "");

        if (name == "") {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }

        // 의문점 : 해당 페이지로 갔다가 뒤로 넘겼을 때 해당 페이지로 다시 넘어오는 거 어떻게 구현하지?
        // 이거 그대로 생성하고 끝내 버리면 계속 화면 생성된 채로 있는 거니까 최적화가 안 되는 거 아님?
        // -> 일단 구현부터 하고 생각하겠음.
        // Button 클릭 -> 새 액티비티(ConnectActivity) 생성
        btnMoveConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BeforeConnectSelectActivity.class);
                startActivity(intent);
            }
        });

        // Button 클릭 -> 새 액티비티(ReportActivity) 생성
        btnMoveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "당신의 음주 습관 액티비티를 띄웁니다.", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                //tartActivity(intent);
            }
        });
    }
}