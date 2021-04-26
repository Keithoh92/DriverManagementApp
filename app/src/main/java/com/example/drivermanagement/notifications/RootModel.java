package com.example.drivermanagement.notifications;

import com.google.gson.annotations.SerializedName;

public class RootModel {

    @SerializedName("to")
    private String token;

    @SerializedName("notification")
    private NotificationModel notification;


    public String getToken1() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }


    public RootModel(String token, NotificationModel notification) {
        this.token = token;
        this.notification = notification;
    }
}
