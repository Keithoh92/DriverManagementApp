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


public class ResultDirectionsApi {
    //API Call
//    String api_key = getString(R.string.api_key);
    private static String BASE_URL= "https://maps.googleapis.com/maps/api/directions/";
    //REMOVE API KEY FROM HERE WHEN USING VERSION CONTROL
    private static String APPID = "";



    String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null){
                stringBuffer.append(line);
            }
            data = stringBuffer.toString();
            br.close();
        }catch(Exception e){
            Log.d("TAG", "Exception" +e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public String ResultDirectionsApi (LatLng origin, LatLng destination) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_destination = "destination=" + destination.latitude + "," + destination.longitude;
        String mode = "mode=driving";

        String parameters = str_origin + "&" +str_destination;

//        StringBuilder sb = new StringBuilder(parameters+"&waypoints=via:");
//        if(waypoints.length != 0){
//            Log.d("testing", "Waypoints url processing : " +waypoints);
//            for(int i = 0; i < waypoints.length; i++){
//                sb.append(waypoints[i].latitude+ "%" +waypoints[i].longitude);
////                Log.d("testing", "new url with waypoints: " +sb);
//            }
//        }
        String output = "json";
//        if(waypoints.length != 0){
//            BASE_URL= "https://maps.googleapis.com/maps/api/directions/";
//            Log.d("testing", "Waypoints not null, reset base url: " +BASE_URL);
//            BASE_URL = BASE_URL + output+ "?" + sb + "&key="+APPID;
//            Log.d("testing", "new base url with waypoints: " +BASE_URL);
//        }
//        else{
            BASE_URL = BASE_URL + output+ "?" + parameters + "&key="+APPID;
//        }
        return BASE_URL;
    }

//    public String ResultDirectionsApiWaypoint (LatLng origin, LatLng destination, LatLng... waypoints) {
//        Log.d("testing", "Waypoint directions method called");
//
//        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
//        String str_destination = "destination=" + destination.latitude + "," + destination.longitude;
//        String mode = "mode=driving";
//
//        String parameters = str_origin + "&" +str_destination;
//
//        StringBuilder sb = new StringBuilder(parameters+"&waypoints=");
//        if(waypoints.length != 0){
//            Log.d("testing", "Waypoints url processing : " +waypoints);
//            for(int i = 0; i < waypoints.length; i++){
//                    sb.append(waypoints[i].latitude + "," + waypoints[i].longitude);
//            }
//        }
//        String output = "json";
//            BASE_URL= "https://maps.googleapis.com/maps/api/directions/";
//            Log.d("testing", "Waypoints not null, reset base url: " +BASE_URL);
//            BASE_URL = BASE_URL + output+ "?" + sb + "&key="+APPID;
//        return BASE_URL;
//    }
}

//
//        DirectionsResult result = DirectionsApi.newRequest(context)
//                .mode(TravelMode.DRIVING)
//                .origin(new LatLng(-7.372732, 110.50824))
//                .waypoints(new LatLng(-7.272732, 110.508244), new LatLng(-7.172732, 110.508244))
//                .optimizeWaypoints(true)
//                .destination(new LatLng(-7.372732, 110.508244))
//                .awaitIgnoreError();
//    }

