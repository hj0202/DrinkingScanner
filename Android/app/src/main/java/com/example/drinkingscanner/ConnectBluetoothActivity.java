package com.example.drinkingscanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ConnectBluetoothActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_bluetooth);

        BluetoothConnectResult result =
                Bluetooth.getInstance().startConnectingBluetooth();

        if (result == BluetoothConnectResult.BLUETOOTH_NOT_SUPPORT) {
            Toast.makeText(getApplicationContext(),
                    "해당 기기는 블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (result == BluetoothConnectResult.BLUETOOTH_OFF) {
            Toast.makeText(getApplicationContext(),
                    "블루투스를 켜주세요.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (result == BluetoothConnectResult.BLUETOOTH_ON) {
            selectDevice();
        }
    }

    public void selectDevice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");

        List<String> devices = Bluetooth.getInstance().getDevices();

        final CharSequence[] charSequences = devices.toArray(new CharSequence[devices.size()]);
        devices.toArray(new CharSequence[devices.size()]);

        // 해당 아이템을 눌렀을 때 호출되는 이벤트 리스너
        builder.setItems(charSequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String click = charSequences[which].toString();
                if (click == "취소") {
                    Log.d("BlueTooth", "취소 버튼 클릭");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Log.d("BlueTooth", "장치 버튼 클릭");
                    Bluetooth.getInstance().connectDevice(click);
                    Intent intent = new Intent(getApplicationContext(), DrinkingAlcoholActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 뒤로가기 버튼을 누를 때 창이 안닫히도록 설정
        builder.setCancelable(false);

        // 다이얼 로그 생성
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
