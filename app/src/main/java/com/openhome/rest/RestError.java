package com.openhome.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhargav
 */
public class RestError {
    @SerializedName("Status")
    private String error;
    @SerializedName("Message")
    private String errorMessage;

    public RestError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


}
