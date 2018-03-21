package com.openhome.model;

/**
 * Created by Bhargav on 12/7/2015.
 */
public class MyProfileRequest {

    private String propertyType;
    private String propertyDesc;
    private String price;
    private String createdOn;
    private String agentId;
    private String userId;
    private String watchlistId;
    private String propertyId;
    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(String watchlistId) {
        this.watchlistId = watchlistId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyDesc() {
        return propertyDesc;
    }

    public void setPropertyDesc(String propertyDesc) {
        this.propertyDesc = propertyDesc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
