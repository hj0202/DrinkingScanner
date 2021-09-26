package com.example.drinkingscanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DrinkingAlcoholActivity extends AppCompatActivity{
    private String currentTime;         // 현재 시각
    private byte[] readBuffer;          //수신된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition;     // 버퍼 내 문자 저장 위치
    private ArrayList<String> sendData; // 서버에 보낼 데이터 (60개)

    private TextView testText;          // 테스트용. 화면 구성 시 삭제바람
    private Button stopSendButton;      // 데이터 수집 종료 버튼

    private RetrofitService service;    // 서버 통신 - 통신 함수를 가지고 있는 Retrofit 객체
    private Boolean sendDataState = true;       // 서버 통신 - sendData를 하고 있냐 안하고 있냐
    private String user,date;           // 서버 통신 - user,date
    private Integer syncTime = 5;      // 서버 통신 - 동기화 함수 간격
    private Integer beforeAmount;       // 서버 통신 - 동기화 함수 이전 값
    private Integer bestSpeed;          // 서버 통신 - 동기화 함수 최고 속도


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinking_alcohol);

        testText = (TextView)findViewById(R.id.textView);   // 테스트용
        stopSendButton = (Button)findViewById(R.id.stopSendData);

        // 데이터 수집 종료 버튼 이벤트
        View.OnClickListener stopListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSendData();
            }
        };
        stopSendButton.setOnClickListener(stopListener);

        // 서버 통신 - Gson, Retrofit 객체 생성
//        Gson gson = new GsonBuilder().setLenient().create();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://172.21.152.63:8000/apiserver/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//        service = retrofit.create(RetrofitService.class);

        //서버 통신 - 통신에 필요한 Retrofit Service 생성 (RetrofitConnection 이용)
        service = RetrofitConnection.getInstance().getService();

        // 서버 통신 - user,date
        user = getUser();
        date = getDate();
        // 서버 통신 - 이전 누적량, 최고 속도 저장
        beforeAmount = 0;
        bestSpeed = 0;

        receiveData();
    }

    public void receiveData() {
        final Handler handler = new Handler();

        currentTime = getSecond();
        readBuffer = new byte[1024];
        readBufferPosition = 0;
        sendData = new ArrayList<>();

        // 데이터를 수신하기 위한 쓰레드 생성
        Thread workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()&&sendDataState) {
                    try {
                        int byteAvailable = Bluetooth.getInstance().inputStream.available();

                        if (byteAvailable > 0) {
                            byte[] bytes = new byte[byteAvailable];
                            Bluetooth.getInstance().inputStream.read(bytes);

                            for(int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];

                                if(tempByte == '\n') {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    final String text = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            String second = getSecond();

                                            if (!currentTime.equals(second) && sendDataState==true) {
                                                testText.append("[" + second + "s,");          // 테스트용
                                                testText.append(text + "ml] ");           // 테스트용
                                                // 서버 통신 - 데이터 쌓기
                                                sendData.add(text);

                                                if (sendData.size() == 5) {
                                                    // 서버 통신 - 60개씩 데이터를 CSV로 저장
                                                    saveData(user,date,sendData);
                                                    // 서버 통신 - 누적량, 속도 위험도 체크를 위한 동기화
//                                                    syncTime = syncTime-1;
//                                                    if(syncTime == 0) {
//                                                        syncData(user,date);
//                                                        syncTime = 10;
//                                                    }

                                                    testText.setText("send data : ");   // 테스트용
                                                    testText.append(user + " " );       // 테스트용
                                                    testText.append(date + " " );       // 테스트용
                                                    testText.append(sendData + "/n");   // 테스트용
                                                    sendData.clear();
                                                }
                                                currentTime = second;
                                            }
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();
    }

    // 서버 통신 - (종료버튼을 누르거나 or 블루투스 끊기면) sendData 중단하고 preData 호출
    public void stopSendData() {
        sendDataState = false;
        preData(user,date);
        testText.setText("start pre data : "+user+date);  // 테스트용
    }

    // 서버 통신 - 서버로 saveData 요청
    public void saveData(String user, String date, ArrayList<String> data) {

        String[] values = data.toArray(new String[0]); // List를 Array로 변경

        // 요청시 보내는 데이터
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("user",user);
        hm.put("date",date);
        hm.put("data",data);

        Log.d("Server Reqeust","데이터 저장 : "+hm.toString() + "\n");
        Call<ServerResult> call = service.saveData(hm);

        // 비동기 처리
        call.enqueue(new Callback<ServerResult>() {
            @Override
            public void onResponse(Call<ServerResult> call, Response<ServerResult> response) {
                if(!response.isSuccessful()) {
                    //테스트용
                    testText.append("\n On Response Error {" +
                            "code: " + response.code() + ", " +
                            "status: " + response.body().getStatus() + "}\n");
                    return;
                }

                testText.append("\n On Response {" +
                        "code: " + response.code() + ", " +
                        "status: " + response.body().getStatus() + "}\n");
            }

            @Override
            public void onFailure(Call<ServerResult> call, Throwable t) {
                testText.setText("On Failure: \n"+ call + "\n" + t + "\n");
            }
        });
    }

    // 서버 통신 - 서버로 preData 요청
    public void preData(String user, String date) {
        // 디버그
        Log.d("Server Request","데이터 전처리 : " + user + date + "\n");

        // 요청시 보내는 데이터 (최고 속도도 함께 보내준다)
        Call<ServerResult> call = service.preData(user,date,bestSpeed);

        // 비동기 처리
        call.enqueue(new Callback<ServerResult>() {
            @Override
            public void onResponse(Call<ServerResult> call, Response<ServerResult> response) {
                if(!response.isSuccessful()) {
                    testText.append("On Response Error {" +
                            "code: " + response.code() + ", " +
                            "status: " + response.body().getStatus() + "}\n");
                    return;
                }

                String resultString = "On Response {" +
                        "code: " + response.code() + ", " +
                        "status: " + response.body().getStatus() + "}\n";
                testText.setText(resultString);
            }

            @Override
            public void onFailure(Call<ServerResult> call, Throwable t) {
                testText.setText("On Failure: \n"+ call + "\n" + t + "\n");
            }
        });
    }

    // 서버 통신 - 서버로 syncData 요청
    public void syncData(String user, String date) {

        HashMap<String,Object> hm = new HashMap<>();
        hm.put("user",user);
        hm.put("date",date);
        hm.put("beforeAmount",beforeAmount);
        hm.put("bestSpeed",bestSpeed);

        Log.d("Server Reqeust","데이터 동기화 : "+user+date+beforeAmount+bestSpeed+"\n");
        Call<ServerSyncResult> call = service.syncData(hm);

        // 비동기 처리
        call.enqueue(new Callback<ServerSyncResult>() {
            @Override
            public void onResponse(Call<ServerSyncResult> call, Response<ServerSyncResult> response) {
                if(!response.isSuccessful()) {
                    //테스트용
                    testText.append("\n On Response Error {" +
                            "code: " + response.code() + ", " +
                            "status: " + response.body().getStatus() + ", " +
                            "BeforeAmount: " + response.body().getBeforeAmount() + ", " +
                            "BestSpeed: " + response.body().getBestSpeed() + "}\n");

                    // 이전 누적 량, 최고 속도 저장
                    beforeAmount = response.body().getBeforeAmount();
                    bestSpeed = response.body().getBestSpeed();

                    //위험 알림
                    if(response.body().getStatus() == "danger")
                        Log.d("ServerReqeust","Danger!!!");

                    return;
                }

                testText.append("\n On Response {" +
                        "code: " + response.code() + ", " +
                        "status: " + response.body().getStatus() + "}\n");
            }

            @Override
            public void onFailure(Call<ServerSyncResult> call, Throwable t) {
                testText.setText("On Failure: \n"+ call + "\n" + t + "\n");
            }
        });
    }

    // 서버 통신 - 서버로 survey 요청
    public void survey(String user,HashMap<String,Object> hm) {
        // 디버그
        Log.d("Server Request","사용자 정보 등록 : " + user + hm + "\n");

        // 요청시 보내는 데이터
        hm.put("user",user);
        hm.put("date",date);

        Call<ServerResult> call = service.survey(hm);

        // 비동기 처리
        call.enqueue(new Callback<ServerResult>() {
            @Override
            public void onResponse(Call<ServerResult> call, Response<ServerResult> response) {
                if(!response.isSuccessful()) {
                    testText.append("On Response Error {" +
                            "code: " + response.code() + ", " +
                            "status: " + response.body().getStatus() + "}\n");
                    return;
                }

                String resultString = "On Response {" +
                        "code: " + response.code() + ", " +
                        "status: " + response.body().getStatus() + "}\n";
                testText.setText(resultString);
            }
            @Override
            public void onFailure(Call<ServerResult> call, Throwable t) {
                testText.setText("On Failure: \n"+ call + "\n" + t + "\n");
            }
        });
    }

    String getSecond() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dataFormat = new SimpleDateFormat("ss");

        return dataFormat.format(date);
    }

    // 서버 통신 - 사용자 이름 구하기
    String getUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("file", MODE_PRIVATE);
        return sharedPreferences.getString("닉네임","");
    }

    //서버 통신 - 오늘 날짜 구하기
    String getDate() {
        long now = System.currentTimeMillis();
        Date myDate = new Date(now);
        SimpleDateFormat myFormat = new SimpleDateFormat("MMdd");
        return myFormat.format(myDate);
    }
}