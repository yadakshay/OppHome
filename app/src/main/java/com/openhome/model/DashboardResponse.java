package com.openhome.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhargav on 4/17/2016.
 */
public class DashboardResponse {

    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private String message;

    @SerializedName("myListingsCount")
    private String myListingsCount;

    @SerializedName("totalListingsCount")
    private String totalListingsCount;

    @SerializedName("watchlistCount")
    private String watchlistCount;

    @SerializedName("moreInfoRequestedCount")
    private String moreInfoRequestedCount;

    @SerializedName("visitorsCount")
    private String visitorsCount;

    @SerializedName("scannedQRCodesCount")
    private String scannedQRCodesCount;

    @SerializedName("archivedListingsCount")
    private String archivedListingsCount;

    public String getArchivedListingsCount() {
        return archivedListingsCount;
    }

    public void setArchivedListingsCount(String archivedListingsCount) {
        this.archivedListingsCount = archivedListingsCount;
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

    public String getMyListingsCount() {
        return myListingsCount;
    }

    public void setMyListingsCount(String myListingsCount) {
        this.myListingsCount = myListingsCount;
    }

    public String getTotalListingsCount() {
        return totalListingsCount;
    }

    public void setTotalListingsCount(String totalListingsCount) {
        this.totalListingsCount = totalListingsCount;
    }

    public String getWatchlistCount() {
        return watchlistCount;
    }

    public void setWatchlistCount(String watchlistCount) {
        this.watchlistCount = watchlistCount;
    }

    public String getMoreInfoRequestedCount() {
        return moreInfoRequestedCount;
    }

    public void setMoreInfoRequestedCount(String moreInfoRequestedCount) {
        this.moreInfoRequestedCount = moreInfoRequestedCount;
    }

    public String getVisitorsCount() {
        return visitorsCount;
    }

    public void setVisitorsCount(String visitorsCount) {
        this.visitorsCount = visitorsCount;
    }

    public String getScannedQRCodesCount() {
        return scannedQRCodesCount;
    }

    public void setScannedQRCodesCount(String scannedQRCodesCount) {
        this.scannedQRCodesCount = scannedQRCodesCount;
    }
}
