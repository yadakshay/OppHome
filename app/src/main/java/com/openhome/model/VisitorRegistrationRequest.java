package com.openhome.model;

/**
 * Created by user on 07-11-2017.
 */

public class VisitorRegistrationRequest {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String visitorType;
    private String hazardsAccepted;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(String visitorType) {
        this.visitorType = visitorType;
    }

    public String getHazardsAccepted() {
        return hazardsAccepted;
    }

    public void setHazardsAccepted(String hazardsAccepted) {
        this.hazardsAccepted = hazardsAccepted;
    }
}
