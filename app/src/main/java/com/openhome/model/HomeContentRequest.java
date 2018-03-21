package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bhargav
 */
public class HomeContentRequest {

    private String userId;

    public HomeContentRequest(String userId){
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
