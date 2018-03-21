package com.openhome.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 07-11-2017.
 */

public class VisitorResponse {

    @SerializedName("Status")
    private String status;

  //  @SerializedName("Message")
  //  private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

 //   public String getMessage() {
  //      return message;
 //   }

  //  public void setMessage(String message) {
  //      this.message = message;
 //   }
}
