package com.example.sojal.staysafe;

/**
 * Created by Sojal on 19-Apr-17.
 */

public class PhoneLocation {
    private String phoneNo;
    private double latitude;
    private double longitude;
    private String lastTime;
    private double distance;

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String  getLastTime() {
        return lastTime;
    }

    public double getDistance() {
        return distance;
    }
}
