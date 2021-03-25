package com.example.drivermanagement;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DirectionsService extends IntentService {

    private int result1 = Activity.RESULT_CANCELED;
    ResultDirectionsApi DirectionsApiClient = new ResultDirectionsApi();
    String data = "";
//    PolylineOptions lineOptions;
    public DirectionsService() {
        super("DirectionsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("testing", "*********Directions Service Started***********");
            String routeApiUrl = intent.getStringExtra("DirectionsApiUrl");
            Log.d("testing", "Received url from intent= " +routeApiUrl);
            Log.d("testing", "Attempting to connect to API for response");

            try {
                data = ((new ResultDirectionsApi().downloadUrl(routeApiUrl)));
                Log.d("testing", "Downloading response from API");
            } catch (IOException e) {
                Log.d("Background Task", e.toString());
                e.printStackTrace();
            }

            try{
                Log.d("testing", "Parsing response from API");
                ParserResponse parserTask = new ParserResponse();
                parserTask.execute(data);
            }catch(Exception e){
                Log.d("API parse task", e.toString());
                e.printStackTrace();
            }
        }
    }
    private class ParserResponse extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            Log.d("testing", "processing parse operation");

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                Log.d("testing", "Getting JSON response and decoding polyline points");

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
            }
//        publishResults(lineOptions, result1);
            Bundle routeDirections = new Bundle();
            routeDirections.putParcelable("polyLinesResult", lineOptions);

            RoutesActivity ra = new RoutesActivity();
//            FragmentManager fm = ra.getSupportFragmentManager()
            MapsFragment mf = new MapsFragment();
            assert mf != null;
            mf.setArguments(routeDirections);
            FragmentManager fm = ra.getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.maps_fragment_routes_activity, mf)
                    .commit();
            Log.d("testing", "Sending new route directions to maps");
        }
    }

//    private void publishResults(PolylineOptions lineOptions, int result) {
//    Log.d("testing", "Publish JSON results got called");
//
//    Intent processedDirections = new Intent("MyBroadcast");
//    processedDirections.putExtra("polylines", lineOptions.PARCELABLE_WRITE_RETURN_VALUE);
//    sendBroadcast(processedDirections);
//    Log.d("testing", "Sending broadcast");
//
//    }

}
