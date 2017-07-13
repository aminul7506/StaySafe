package com.example.sojal.staysafe;

import android.content.SharedPreferences;

import java.util.*;
import java.text.*;
/**
 * Created by Sojal on 14-Apr-17.
 */

public class SignInClass {
    private String firstName;
    private String surName;
    private String dateOfBirth;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
    private String gender;
    private String imei;
    private String uid;
    private String currentTime;
    private double latitude;
    private double longitude;
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        this.currentTime = dateFormat.format(date).toString();
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurName() {
        return surName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getImei() {
        return imei;
    }

    public String getUid() {
        return uid;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
