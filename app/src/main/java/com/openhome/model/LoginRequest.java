package com.openhome.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhargav on 12/7/2015.
 */
public class LoginRequest {

    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("logintoken")
    private String loginToken;

    public LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
        this.loginToken = "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
}
