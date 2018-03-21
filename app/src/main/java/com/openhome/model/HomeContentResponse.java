package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bhargav
 */
public class HomeContentResponse {
    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private String message;

    @SerializedName("hot")
    private List<PropertyInfo> hotPropertiesList;

    @SerializedName("listedByAgent")
    private List<PropertyInfo> agentPropertiesList;

    @SerializedName("primary")
    private List<PropertyInfo> primaryPropertiesList;

    public List<PropertyInfo> getPrimaryPropertiesList() {
        return primaryPropertiesList;
    }

    public void setPrimaryPropertiesList(List<PropertyInfo> primaryPropertiesList) {
        this.primaryPropertiesList = primaryPropertiesList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PropertyInfo> getHotPropertiesList() {
        return hotPropertiesList;
    }

    public void setHotPropertiesList(List<PropertyInfo> hotPropertiesList) {
        this.hotPropertiesList = hotPropertiesList;
    }


    public List<PropertyInfo> getAgentPropertiesList() {
        return agentPropertiesList;
    }

    public void setAgentPropertiesList(List<PropertyInfo> agentPropertiesList) {
        this.agentPropertiesList = agentPropertiesList;
    }

}
