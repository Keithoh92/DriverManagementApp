package com.example.drivermanagement;

//RETROFIT CLASS FOR RETRIEVING DISTANCES AND DURATIONS OF ROUTES

public class StoreModel {

    public String name, address, distance, duration;

    public StoreModel(String name, String address, String distance, String duration){
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.duration = duration;

    }
}
