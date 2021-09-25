// 서버 통신 - 서버 응답 형태
package com.example.drinkingscanner;

import com.google.gson.annotations.SerializedName;

// {'status': 'success' or 'request error' or 'result error'}

public class ServerResult {
    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }
}