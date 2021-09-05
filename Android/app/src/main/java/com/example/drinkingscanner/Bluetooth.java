package com.example.drinkingscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

enum BluetoothConnectResult {
    BLUETOOTH_NOT_SUPPORT,
    BLUETOOTH_ON,
    BLUETOOTH_OFF
}

public class Bluetooth extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 10;    // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter;          // 블루투스 어댑터
    private BluetoothDevice bluetoothDevice;            // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null;     // 블루투스 소켓
    private Set<BluetoothDevice> devices;               // 블루투스 디바이스 데이터 셋
    public InputStream inputStream = null;             // 블루투스에 데이터를 입력하기 위한 스트림

    private static Bluetooth instance;
    public static Bluetooth getInstance() {
        if (instance == null) {
            instance = new Bluetooth();
        }
        return instance;
    }

    public BluetoothConnectResult startConnectingBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("BlueTooth", "블루투스 연결");
        if (bluetoothAdapter == null) {
            return BluetoothConnectResult.BLUETOOTH_NOT_SUPPORT;
        }
        else {
            if (bluetoothAdapter.isEnabled()) {
                return BluetoothConnectResult.BLUETOOTH_ON;
            }
            else {
                return BluetoothConnectResult.BLUETOOTH_OFF;
            }
        }
    }

    public List<String> getDevices() {
        devices = bluetoothAdapter.getBondedDevices();

        List<String> list = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : devices) {
            list.add(bluetoothDevice.getName());
        }
        list.add("취소");

        return list;
    }

    public void connectDevice(String deviceName) {
        for (BluetoothDevice tempDevice : devices) {
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }

        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            inputStream = bluetoothSocket.getInputStream();
            Log.d("BlueTooth", "데이터를 받을 수 있음");


        } catch (IOException e) {
            e.printStackTrace();
            Log.d("BlueTooth", "데이터를 받을 수 없음");
        }
    }
}
