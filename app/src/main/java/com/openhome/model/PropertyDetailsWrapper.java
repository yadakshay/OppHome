package com.openhome.model;

import java.util.List;

/**
 * Created by Bhargav on 2/22/2017.
 */

public class PropertyDetailsWrapper {

    private List<Hazards> hazards;

    private int isMoreInfoRequested;

    public List<Hazards> getHazards() {
        return hazards;
    }

    public void setHazards(List<Hazards> hazards) {
        this.hazards = hazards;
    }

    public int getIsMoreInfoRequested() {
        return isMoreInfoRequested;
    }

    public void setIsMoreInfoRequested(int isMoreInfoRequested) {
        this.isMoreInfoRequested = isMoreInfoRequested;
    }
}
