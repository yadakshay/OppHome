package com.openhome.model;

import android.os.Build;

/**
 * Created by Bhargav on 3/23/2017.
 */

public class ErrorLog {

    private int appVersionCode;
    private String appVersionName;
    private String deviceModel;
    private int androidSdkInt;
    private String errorMessage;
    private String exceptionType;
    private String extractedLog;

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getAndroidSdkInt() {
        return androidSdkInt;
    }

    public void setAndroidSdkInt(int androidSdkInt) {
        this.androidSdkInt = androidSdkInt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getExtractedLog() {
        return extractedLog;
    }

    public void setExtractedLog(String extractedLog) {
        this.extractedLog = extractedLog;
    }
}
