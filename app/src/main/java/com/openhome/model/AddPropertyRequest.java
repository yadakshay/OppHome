package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bhargav on 12/7/2015.
 */
public class AddPropertyRequest {

    @SerializedName("agentId")
    private String agentId;
    @SerializedName("shortDescription")
    private String shortDescription;
    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type;
    @SerializedName("address")
    private String address;
    @SerializedName("price")
    private String price;

    @SerializedName("bathrooms")
    private String bathrooms;
    @SerializedName("bedrooms")
    private String bedrooms;
    @SerializedName("floorArea")
    private String floorArea;

    @SerializedName("landArea")
    private String landArea;
    @SerializedName("parking")
    private String parking;
    @SerializedName("viewingTime")
    private String viewingTime;

    @SerializedName("hot")
    private int isHot;

    @SerializedName("methodOfSale")
    private String methodOfSale;

    @SerializedName("methodOfSaleValue")
    private String methodOfSaleValue;

    @SerializedName("propertyImage")
    private String propertyImage;

    @SerializedName("hazards")
    List<Hazards> propertyHazards;


    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLandArea() {
        return landArea;
    }

    public void setLandArea(String landArea) {
        this.landArea = landArea;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getViewingTime() {
        return viewingTime;
    }

    public void setViewingTime(String viewingTime) {
        this.viewingTime = viewingTime;
    }

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public List<Hazards> getPropertyHazards() {
        return propertyHazards;
    }

    public void setPropertyHazards(List<Hazards> propertyHazards) {
        this.propertyHazards = propertyHazards;
    }

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

    public String getPropertyImage() {
        return propertyImage;
    }

    public void setPropertyImage(String propertyImage) {
        this.propertyImage = propertyImage;
    }
}
