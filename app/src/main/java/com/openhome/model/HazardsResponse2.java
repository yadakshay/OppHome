package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bhargav on 8/8/2016.
 */

public class HazardsResponse2 {

    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private List<Hazards> message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Hazards> getMessage() {
        return message;
    }

    public void setMessage(List<Hazards> message) {
        this.message = message;
    }
}
