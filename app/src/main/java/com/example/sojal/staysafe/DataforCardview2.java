package com.example.sojal.staysafe;

/**
 * Created by Sojal on 03-Jun-17.
 */

public class DataforCardview2 {
    private String problem_name;
    private String time;
    private String status;
    private String location;
    private String imei;
    public void setStatus(String status) {
        this.status = status;
    }

    public void setProblem_name(String problem_name) {
        this.problem_name = problem_name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public String getImei() {
        return imei;
    }

    public String getProblem_name() {
        return problem_name;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }
}
