package com.example.drivermanagement;

public class Contacts {

    public String username, image, email;

    public Contacts()
    {

    }

    public Contacts(String username, String image, String email) {
        this.username = username;
        this.image = image;
        this.email = email;
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
