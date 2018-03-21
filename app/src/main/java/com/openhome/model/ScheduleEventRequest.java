package com.openhome.model;

/**
 * Created by Bhargav on 8/4/2016.
 */

public class ScheduleEventRequest {

    private String scheduleId;

    private String userId;

    private String agentUserId;

    private String propertyId;

    private String scheduledDateTime;

    private String status;

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAgentUserId() {
        return agentUserId;
    }

    public void setAgentUserId(String agentUserId) {
        this.agentUserId = agentUserId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(String scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
