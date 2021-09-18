// 서버 통신 - 서버 응답 형태
package com.example.drinkingscanner;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

// {
// 'status': 'success'
// 'x' : [1,2,3,4,5]
// 'y' : [10,20,30,40,50]
// }

public class ServerXYResult {
    @SerializedName("status")
    private String status;

    @SerializedName("x")
    private ArrayList<Integer> x;

    @SerializedName("y")
    private ArrayList<Integer> y;

    public String getStatus() {
        return status;
    }
    public ArrayList<Integer> getX() {
        return x;
    }
    public ArrayList<Integer> getY() {
        return y;
    }
}
