package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Bhargav on 7/8/2016.
 */
public class PropertyInfo implements Serializable {

    private String propertyId;
    private String shortDescription;
    private String agentId;
    private String watchlistId;
    private String type;
    private String description;
    private String address;
    private String price;
    private String price_unit;
    private String active;
    private String createdOn;
    private String bathrooms;
    private String bedrooms;
    private String floorArea;
    private String parking;
    private String landArea;
    private String viewingTime;
    private boolean watching;
    private String homeImageURL;
    private String distance;
    private String lattitude;
    private String longitude;
    private int hot;
    private int primary;
    @SerializedName("isarchived")
    int isArchived;
    String methodOfSale;
    String methodOfSaleValue;

    public String getMethodOfSale() {
        return methodOfSale;
    }

    public void setMethodOfSale(String methodOfSale) {
        this.methodOfSale = methodOfSale;
    }

    public String getMethodOfSaleValue() {
        return methodOfSaleValue;
    }

    public void setMethodOfSaleValue(String methodOfSaleValue) {
        this.methodOfSaleValue = methodOfSaleValue;
    }

    @SerializedName("visitors")
    String visitorsCount;
    @SerializedName("infoRequested")
    String infoRequestedCount;

    public int getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }

    public int getPrimary() {
        return primary;
    }

    public void setPrimary(int primary) {
        this.primary = primary;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(String watchlistId) {
        this.watchlistId = watchlistId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_unit() {
        return price_unit;
    }

    public void setPrice_unit(String price_unit) {
        this.price_unit = price_unit;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(String bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getFloorArea() {
        return floorArea;
    }

    public void setFloorArea(String floorArea) {
        this.floorArea = floorArea;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getLandArea() {
        return landArea;
    }

    public void setLandArea(String landArea) {
        this.landArea = landArea;
    }

    public String getViewingTime() {
        return viewingTime;
    }

    public void setViewingTime(String viewingTime) {
        this.viewingTime = viewingTime;
    }

    public boolean isWatching() {
        return watching;
    }

    public void setWatching(boolean watching) {
        this.watching = watching;
    }

    public String getHomeImageURL() {
        return homeImageURL;
    }

    public void setHomeImageURL(String homeImageURL) {
        this.homeImageURL = homeImageURL;
    }

    public String getVisitorsCount() {
        return visitorsCount;
    }

    public void setVisitorsCount(String visitorsCount) {
        this.visitorsCount = visitorsCount;
    }

    public String getInfoRequestedCount() {
        return infoRequestedCount;
    }

    public void setInfoRequestedCount(String infoRequestedCount) {
        this.infoRequestedCount = infoRequestedCount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
