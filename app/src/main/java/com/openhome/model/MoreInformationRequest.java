package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Bhargav on 12/9/2015.
 */
public class MoreInformationRequest  {

    @SerializedName("userID")
    private String userId;
    @SerializedName("propertyID")
    private String propertyId;
    private String requestType;

    public MoreInformationRequest(String userId, String propertyId, String requestType){
        this.userId = userId;
        this.propertyId = propertyId;
        this.requestType = requestType;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
}
