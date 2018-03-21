package com.openhome.adapter;

import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Bhargav on 8/4/2016.
 */

public class CustomInfoWindowAdapter implements InfoWindowAdapter{


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}