package com.openhome.model;

/**
 * Created by Bhargav on 4/17/2016.
 */
public class
ScannedCodesRequest {

    private String userId;
    private String scannedCodes;

    public ScannedCodesRequest(String userId, String scannedCodes){
        this.userId = userId;
        this.scannedCodes = scannedCodes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScannedCodes() {
        return scannedCodes;
    }

    public void setScannedCodes(String scannedCodes) {
        this.scannedCodes = scannedCodes;
    }
}
