package com.example.drivermanagement;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser {
    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
//            final JSONObject jsonObject = new JSONObject()
            jRoutes = jObject.getJSONArray("routes");
            Log.d("testing", "Parsing routes: " +jRoutes.toString());



            JSONObject routes1 = jRoutes.getJSONObject(0);
            JSONObject overviewPolylines = routes1.getJSONObject("overview_polyline");
            Log.d("testing", "Getting overview polylines for waypoint routes: " +overviewPolylines);
            String encodedString = overviewPolylines.getString("points");
            List list = decodePoly(encodedString);


                /** Traversing all points */
                List path = new ArrayList<HashMap<String, String>>();
                for(int l=0;l <list.size();l++){
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                    path.add(hm);
                }
                routes.add(path);

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return routes;
    }

    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List decodePoly(String encoded) {
        //"a~l~Fjk~uOwHJy@P"
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
