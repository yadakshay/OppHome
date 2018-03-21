package com.openhome.model;

/**
 * Created by Bhargav on 12/9/2015.
 */
public class WatchlistRequest {

    private String userId;
    private String propertyId;

    public WatchlistRequest(String userId, String propertyId){
        this.userId = userId;
        this.propertyId = propertyId;
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
