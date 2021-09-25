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
    private ArrayList<Float> beforeAmount;
    private ArrayList<Float> bestSpeed;

    public String getStatus() {
        return status;
    }
    public Integer getBeforeAmount() { return Math.round(beforeAmount.get(0)); }
    public Integer getBestSpeed() { return Math.round(bestSpeed.get(0)); }
}