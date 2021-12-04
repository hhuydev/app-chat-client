package com.example.myappchat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtpAuth implements Serializable {
    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("remaining")
    @Expose
    private int remaining;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
