package com.example.drivermanagement;

import android.icu.text.UFormat;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class DistanceJSONParser {

    public String parse(JSONObject jObject) throws JSONException{
        float dist = 0, dur = 0;
        double km;
        JSONArray jRoutes = null;
        jRoutes = jObject.getJSONArray("routes");
        JSONObject object = jRoutes.getJSONObject(0);
        JSONArray legs = object.getJSONArray("legs");

        for(int i =0; i< legs.length(); i++) {
            JSONObject legsObjects = legs.getJSONObject(0);

        //get the distance
            JSONObject distance = legsObjects.getJSONObject("distance");
            String distanceStr = distance.getString("value");
            dist = dist + Float.parseFloat(distanceStr);

        //get the time
            JSONObject time = legsObjects.getJSONObject("duration");
            String duration = time.getString("value");
            dur += Float.parseFloat(duration);
        }


        km = (double) dist/1000;
        DecimalFormat df = new DecimalFormat("#.##");
        km = Double.valueOf(df.format(km));

        dur = dur/60;

        int timeResult = Math.round(dur);
        String result = "Dist: " +km + " km | Duration: " +timeResult+" mins";
        Log.d("testing", "Distance and duration of trip : " +result);
        return result;
    }
}
