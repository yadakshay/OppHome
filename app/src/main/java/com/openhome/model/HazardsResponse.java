package com.openhome.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bhargav on 8/8/2016.
 */

public class HazardsResponse {

    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private PropertyDetailsWrapper message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PropertyDetailsWrapper getMessage() {
        return message;
    }

    public void setMessage(PropertyDetailsWrapper message) {
        this.message = message;
    }
}
