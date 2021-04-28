package com.example.drivermanagement;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

////////////// GET DIRECTIONS FROM GOOGLE ROUTES API FOR USERS CHOSEN DESTINATIONS////////////////////////////

public class ResultDirectionsApi {
    //API Call

    private static String BASE_URL;
    String url;


    //REMOVE API KEY FROM HERE WHEN USING VERSION CONTROL
    private static String APPID = "";


    String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            data = stringBuffer.toString();
            br.close();
        } catch (Exception e) {
            Log.d("TAG", "Exception" + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //CALLED FROM MAPS FRAGMENT ON ROUTES ACTIVITY WHEN ONLY ONE DESTINATION IS CHOSEN
    public String ConstructAPISingleDestination(LatLng origin, LatLng destination) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_destination = "destination=" + destination.latitude + "," + destination.longitude;
        String mode = "mode=driving";

        String parameters = str_origin + "&" + str_destination;

        String output = "json";


        BASE_URL= "https://maps.googleapis.com/maps/api/directions/";
        Log.d("testing", "Reset base url: " +BASE_URL);
        BASE_URL = BASE_URL + output + "?" + parameters + "&key=" + APPID;

        return BASE_URL;
    }

    //CALLED FROM MAPS FRAGMENT ON ROUTES ACTIVITY WHEN MORE THAN ONE DESTINATION IS CHOSEN
    //THE ORIGIN IS THE USERS LOCATION
    //THE DESTINATION IS THE FINAL STOP
    //AND THE LIST HAS ALL THE DESTINATIONS BUT IS PICKED APART TO GET THE STOPOVER LOCATIONS (WAYPOINTS)
    // TO CALCULATE THE ROUTES ALONG THE WAY TO THE FINAL DESTINATION FROM THE USERS LOCATION
    public String ConstructAPIMultipleDestinations(LatLng origin, LatLng destination, List<String> destinationsList) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_destination = "destination=" + destination.latitude + "," + destination.longitude;

        String parameters = str_origin + "&" + str_destination;//origin and destination part of url
        String output = "json?";

        StringBuilder sb = new StringBuilder(parameters + "&waypoints=");
        String barSeperator = "";

        StringBuilder startMapsUrl = new StringBuilder(origin.latitude+","+origin.longitude+"/");
        String sepereator = "";

        //Get new waypoints
        for (int i = 0; i < destinationsList.size()-1; i++) {
            //Get the list item at i
            String destinations = destinationsList.get(i);
            //split the list item into tokens
            String[] arr = destinations.split(",");
            //latitiude is always the token 2 from last
            //longitude is always the token 1 from last
            Double waypointLat = Double.parseDouble(arr[arr.length - 2]);
            Log.d("testing", "Getting Waypoint latitude at " + i + " from list: " + waypointLat);
            Double waypointLng = Double.parseDouble(arr[arr.length - 1]);
            Log.d("testing", "Getting Waypoint longitude at " + i + " from list: " + waypointLng);

            sb.append(barSeperator + waypointLat + "," + waypointLng);
            barSeperator = "|";

            startMapsUrl.append(sepereator + waypointLat +","+ waypointLng);
            sepereator = "/";

        }
        startMapsUrl.append("/"+destination.latitude+","+destination.longitude);


        BASE_URL= "https://maps.googleapis.com/maps/api/directions/";
        Log.d("testing", "Reset base url: " +BASE_URL);
        BASE_URL = BASE_URL + output + sb + "&key=" + APPID;
        Log.d("testing", "New url with waypoints: " + BASE_URL);


        return BASE_URL;
    }


}


