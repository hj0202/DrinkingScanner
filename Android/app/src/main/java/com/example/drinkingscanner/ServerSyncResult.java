// 서버 통신 - 서버 응답 형태
package com.example.drinkingscanner;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

// {
//  'status': 'danger' or 'no danger' or 'request error' or 'result error'
//  'beforeAmount' : 200
//  'bestSpeed' : 10
//  }

public class ServerSyncResult {
    @SerializedName("status")
    private String status;
    private Float beforeAmount;
    private Float bestSpeed;

    public String getStatus() {
        return status;
    }
    public Integer getBeforeAmount() { return Math.round(beforeAmount); }
    public Float getBestSpeed() { return bestSpeed; }
}