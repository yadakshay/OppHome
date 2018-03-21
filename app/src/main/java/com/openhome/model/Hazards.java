package com.openhome.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bhargav on 9/25/2016.
 */

public class Hazards implements Parcelable {
    private int hazardId;
    private String hazardType;
    private String hazardCode;
    private String hazardScenario;
    private String hazardDescription;
    private int display;



    public int getHazardId() {
        return hazardId;
    }

    public void setHazardId(int hazardId) {
        this.hazardId = hazardId;
    }

    public String getHazardType() {
        return hazardType;
    }

    public void setHazardType(String hazardType) {
        this.hazardType = hazardType;
    }

    public String getHazardCode() {
        return hazardCode;
    }

    public void setHazardCode(String hazardCode) {
        this.hazardCode = hazardCode;
    }

    public String getHazardScenario() {
        return hazardScenario;
    }

    public void setHazardScenario(String hazardScenario) {
        this.hazardScenario = hazardScenario;
    }

    public String getHazardDescription() {
        return hazardDescription;
    }

    public void setHazardDescription(String hazardDescription) {
        this.hazardDescription = hazardDescription;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.hazardId);
        dest.writeString(this.hazardType);
        dest.writeString(this.hazardCode);
        dest.writeString(this.hazardScenario);
        dest.writeString(this.hazardDescription);
        dest.writeInt(this.display);
    }

    public Hazards() {
    }

    protected Hazards(Parcel in) {
        this.hazardId = in.readInt();
        this.hazardType = in.readString();
        this.hazardCode = in.readString();
        this.hazardScenario = in.readString();
        this.hazardDescription = in.readString();
        this.display = in.readInt();
    }

    public static final Creator<Hazards> CREATOR = new Creator<Hazards>() {
        @Override
        public Hazards createFromParcel(Parcel source) {
            return new Hazards(source);
        }

        @Override
        public Hazards[] newArray(int size) {
            return new Hazards[size];
        }
    };
}
