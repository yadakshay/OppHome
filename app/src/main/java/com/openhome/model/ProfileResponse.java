package com.openhome.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bhargav
 */
public class ProfileResponse {
    @SerializedName("Status")
    private String status;
    @SerializedName("Message")
    private UserDetails message;

    public UserDetails getMessage() {
        return message;
    }

    public void setMessage(UserDetails message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class UserDetails {
        String userId;
        String firstName;
        String lastName;
        String emailAddress;
        String username;
        String password;
        String loginToken;
        String userType;
        String qrcodeURL;
        @SerializedName("licenceNumber")
        String liscNumber;
        @SerializedName("licenceType")
        String liscType;
        @SerializedName("licenceClass")
        String liscClass;
        String agency;
        String branch;
        String expiryDate;

        public String getLiscClass() {
            return liscClass;
        }

        public void setLiscClass(String liscClass) {
            this.liscClass = liscClass;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getLoginToken() {
            return loginToken;
        }

        public void setLoginToken(String loginToken) {
            this.loginToken = loginToken;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getQrcodeURL() {
            return qrcodeURL;
        }

        public void setQrcodeURL(String qrcodeURL) {
            this.qrcodeURL = qrcodeURL;
        }

        public String getLiscNumber() {
            return liscNumber;
        }

        public void setLiscNumber(String liscNumber) {
            this.liscNumber = liscNumber;
        }

        public String getLiscType() {
            return liscType;
        }

        public void setLiscType(String liscType) {
            this.liscType = liscType;
        }

        public String getAgency() {
            return agency;
        }

        public void setAgency(String agency) {
            this.agency = agency;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }
    }
}
