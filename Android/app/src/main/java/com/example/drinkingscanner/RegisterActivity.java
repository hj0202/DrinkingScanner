package com.example.drinkingscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_name;
    private EditText et_ability; //이채원 맘대로 수정 09/25
    private Button btn_reg;
    private String shared = "file";
    private RetrofitService service; // 이채원 맘대로 수정 09/25

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_name = (EditText) findViewById(R.id.et_name);
        et_ability = (EditText) findViewById(R.id.et_ability); //이채원 맘대로 수정 09/25
        btn_reg = (Button) findViewById(R.id.btn_register);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }

                // 이채원 맘대로 수정 09/25
                if (et_ability.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "주량을 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                // 이채원 맘대로 수정 09/25

                else {
                    SharedPreferences sharedPreferences = getSharedPreferences(shared, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String name = et_name.getText().toString();
                    editor.putString("닉네임", name);
                    
                    //이채원 맘대로 수정 09/25
                    Integer ability = Integer.parseInt(et_ability.getText().toString());
                    editor.putInt("주량", ability);
                    
                    register(name,ability); // 이름과 주량 UserInfo에 저장
                    // 이채원 맘대로 수정 09/25

                    editor.commit();

                    Toast.makeText(getApplicationContext(), name+"님 환영합니다!", Toast.LENGTH_SHORT).show();

                    finish();
                }

            }
        });

        //이채원 맘대로 수정 09/25
        service = RetrofitConnection.getInstance().getService();
        //이채원 맘대로 수정 09/25
    }

    // 서버 통신 - 서버로 register 요청
    public void register(String user, Integer ability) {
        // 디버그
        Log.d("Server Request","사용자 정보 등록 : " + user + ability + "\n");

        // 요청시 보내는 데이터
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("user",user);
        hm.put("ability",ability);

        Call<ServerResult> call = service.register(hm);

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
}
