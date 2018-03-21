package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bhargav on 7/8/2016.
 */
public class StandartPropertyListResponse {

    @SerializedName("Status")
    private String status;

    @SerializedName("Message")
    private List<PropertyInfo> propertyInfoList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PropertyInfo> getPropertyInfoList() {
        return propertyInfoList;
    }

    public void setPropertyInfoList(List<PropertyInfo> propertyInfoList) {
        this.propertyInfoList = propertyInfoList;
    }
}
