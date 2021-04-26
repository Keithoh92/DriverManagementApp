package com.example.drivermanagement;

public class GroupMessages {
    private String from;
    private String message;
    private String type;
    private String username;

    public GroupMessages()
    {

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public GroupMessages(String from, String message, String type, String username) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.username = username;
    }


}
