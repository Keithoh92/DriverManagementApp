package com.example.drivermanagement;

public class UsersLocation {
    private Double lat;
    private Double lng;

    public UsersLocation(Double lat, Double lng, String time) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
    }

    private String time;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public UsersLocation()
    {

    }
}
