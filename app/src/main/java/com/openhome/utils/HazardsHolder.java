package com.openhome.utils;

import com.openhome.model.Hazards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Bhargav on 10/17/2016.
 */

public class HazardsHolder {

    private List<Hazards> list;

    public HazardsHolder(List<Hazards> hazardsList) {
        this.list = hazardsList;
    }

    public HashSet<String> getHazardTypeList() {
        HashSet<String> typeList = new HashSet<>();
        for (Hazards hazards : list) {
            typeList.add(hazards.getHazardType());
        }
        if (!typeList.contains("Outdoor Hazards"))
            typeList.add("Outdoor Hazards");
        if (!typeList.contains("Other Hazards"))
            typeList.add("Other Hazards");
        return typeList;
    }

    public HashSet<Hazards> getScenarioList(String type) {
        HashSet<Hazards> scenarioList = new HashSet<>();
        for (Hazards hazards : list) {
            if (hazards.getHazardType().equals(type) && !scenarioList.contains(hazards))
                scenarioList.add(hazards);
        }
        return scenarioList;
    }

    public HashSet<Hazards> getDescriptionList(String type, String scenario) {
        HashSet<Hazards> descList = new HashSet<>();
        for (Hazards hazards : list) {
            if (hazards.getHazardScenario().equals(scenario))
                descList.add(hazards);
        }
        return descList;
    }
}
