package com.example.sojal.staysafe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sojal on 18-May-17.
 */

public class CrimeClass {
    private String time;
    private String date;
    private String title;
    private String post_description;
    private String location;
    private double latitude;
    private double longitude;
    private String police_station;
    private double police_station_latitude;
    private double police_station_longitude;
    private String current_time;
    private String status;
    private String imei;
    public void setTime(String time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPolice_station(String police_station) {
        this.police_station = police_station;
    }

    public void setPost_description(String post_description) {
        this.post_description = post_description;
    }

    public void setPolice_station_latitude(double police_station_latitude) {
        this.police_station_latitude = police_station_latitude;
    }

    public void setPolice_station_longitude(double police_station_longitude) {
        this.police_station_longitude = police_station_longitude;
    }

    public void setCurrent_time() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        this.current_time = dateFormat.format(date).toString();
    }

    public String getImei() {
        return imei;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getPolice_station() {
        return police_station;
    }

    public String getPost_description() {
        return post_description;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public double getPolice_station_latitude() {
        return police_station_latitude;
    }

    public double getPolice_station_longitude() {
        return police_station_longitude;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public String getStatus() {
        return status;
    }
}
