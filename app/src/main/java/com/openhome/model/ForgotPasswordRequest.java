package com.openhome.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhargav on 9/29/2016.
 */

public class ForgotPasswordRequest {

    @SerializedName("userEmailAddress")
    private String userEmailAddress;

    public String getUserEmailAddress() {
        return userEmailAddress;
    }

    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }
}
