package com.openhome.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bhargav
 */
public class SearchResponse {
    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private List<PropertyInfo> message;

    public List<PropertyInfo> getMessage() {
        return message;
    }

    public void setMessage(List<PropertyInfo> message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
