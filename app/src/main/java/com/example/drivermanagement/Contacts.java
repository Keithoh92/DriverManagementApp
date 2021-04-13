package com.example.drivermanagement;

public class Contacts {

    public String username, image, email, driverid;

    public Contacts()
    {

    }

    public Contacts(String username, String image, String email, String driverid) {
        this.username = username;
        this.image = image;
        this.email = email;
        this.driverid = driverid;
    }
    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
