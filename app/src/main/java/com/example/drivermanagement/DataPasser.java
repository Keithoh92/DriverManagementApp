package com.example.drivermanagement;

import java.util.List;

//FOR SENDING THE USERS LOCATION TO THE MAPS FRAGMENT ON THE ROUTES ACTIVITY
//AND ALSO SENDING THE LIST OF LOCATIONS CHOSEN BY THE USER TO THE MAPS FRAGMENT
//WHICH IS THEN USED TO GET THE POLYLINES ON THE MAP AND ALSO CALCULATE THE ROUTE, DISTANCE AND TIME

public interface DataPasser {
    public Double getLat();
    public Double getLon();
    public List getList();
}
