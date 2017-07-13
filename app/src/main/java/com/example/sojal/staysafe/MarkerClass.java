package com.example.sojal.staysafe;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Sojal on 05-Jun-17.
 */

public class MarkerClass {
    private int markerId;
    private String title;
    private String description;
    private String date;
    private String time;
    private Marker marker;
    private String location;

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMarkerId(int markerId) {
        this.markerId = markerId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMarkerId() {
        return markerId;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public Marker getMarker() {
        return marker;
    }

    public String getLocation() {
        return location;
    }
}
