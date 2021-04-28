package com.example.drivermanagement;

/*
    CLASS FOR GETTING GROUPS FROM FIREBASE DATABASE AND LOADING INTO FIREBASE RECYCLER ADAPTOR
 */

public class Groups {

    public String getGroupname() {
        return groupname;
    }

    public String getCreatedon() {
        return createdon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setGroupname(String groupname, String image, String createdon) {
        this.groupname = groupname;
        this.createdon = createdon;
        this.image = image;
    }

    public String groupname, createdon, image;

    public Groups() {

    }
}
