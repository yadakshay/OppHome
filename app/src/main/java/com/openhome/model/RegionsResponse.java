package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bhargav on 1/13/2016.
 */
public class RegionsResponse {

    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private List<Regions> message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Regions> getMessage() {
        return message;
    }

    public void setMessage(List<Regions> message) {
        this.message = message;
    }

    public static class Regions {

        @SerializedName("regionid")
        private int regionId;

        @SerializedName("active")
        private int active;

        @SerializedName("parentid")
        private int parentId;

        @SerializedName("level")
        private int level;

        @SerializedName("regionname")
        private String regionName;

        @SerializedName("listingscount")
        private int listingsCount;

        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

        public int getRegionId() {
            return regionId;
        }

        public void setRegionId(int regionId) {
            this.regionId = regionId;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public int getListingsCount() {
            return listingsCount;
        }

        public void setListingsCount(int listingsCount) {
            this.listingsCount = listingsCount;
        }
    }
}
