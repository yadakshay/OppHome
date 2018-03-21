package com.openhome.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhargav on 9/27/2016.
 */
public class ChangePasswordRequest {

    @SerializedName("oldPassword")
    private String oldPassword;

    @SerializedName("newPassword")
    private String newPassword;

    @SerializedName("userId")
    private int userId;

    public ChangePasswordRequest(String oldPassword, String newPassword, int userId) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.userId = userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
