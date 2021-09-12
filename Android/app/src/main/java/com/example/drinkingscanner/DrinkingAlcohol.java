package com.example.drinkingscanner;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DrinkingAlcohol extends AppCompatActivity {
    private String currentTime;         // 현재 시각
    private byte[] readBuffer;          //수신된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition;     // 버퍼 내 문자 저장 위치
    private ArrayList<String> sendData; // 서버에 보낼 데이터 (60개)

    private TextView testText;          // 테스트용. 화면 구성 시 삭제바람

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinking_alcohol);

        testText = (TextView)findViewById(R.id.textView);   // 테스트용

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
                while(!Thread.currentThread().isInterrupted()) {
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

                                            if (!currentTime.equals(second)) {
                                                testText.append("[" + second + ", ");   // 테스트용
                                                testText.append(text + "] ");           // 테스트용

                                                sendData.add(text);

                                                if (sendData.size() == 60) {
                                                    // TODO: 서버에 데이터를 보내야함
                                                    testText.setText("send data~\n");   // 테스트용
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

    String getSecond() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dataFormat = new SimpleDateFormat("ss");

        return dataFormat.format(date);
    }
}
