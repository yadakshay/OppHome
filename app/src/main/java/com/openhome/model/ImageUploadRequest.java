package com.openhome.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhargav on 3/13/2016.
 */
public class ImageUploadRequest {

    @SerializedName("image")
    private String imageString;

    @SerializedName("fileName")
    private String imageFileName;

    public ImageUploadRequest(String imageString, String imageFileName){
        this.imageFileName = imageFileName;
        this.imageString = imageString;
    }


    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }
}
