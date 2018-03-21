package com.openhome.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhargav on 12/7/2015.
 */
public class ResponseData {

    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
