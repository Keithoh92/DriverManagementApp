package com.example.drivermanagement;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraCharacteristics;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.maps.android.PolyUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private FragmentToActivity mCallback;
    LatLng sLat, sLon;
    String placeName, wayPlaceName;
    String data = "";
    ResultDirectionsApi resultDirectionsApi;
    LatLng usersLatLng, destinationLatLng, waypointLatLng;
    Polyline line;
    PolylineOptions lineOptions;
    String distanceDuration = "Dist: 0km | Duration: 0mins";
    String url;


//    public MapsFragment() {
//        // Required empty public constructor
//    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mCallback = (FragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentToActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resultDirectionsApi = new ResultDirectionsApi();


    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        //Get Users location from routes activity
        DataPasser myDataPasser = (DataPasser) getActivity();
        if (myDataPasser != null) {
            Log.d("testing", "Maps Fragment: Getting Users location");
            double usersLatitude = myDataPasser.getLat();
            double usersLongitude = myDataPasser.getLon();
            usersLatLng = new LatLng(usersLatitude, usersLongitude);

            if (usersLatLng != null) {
                map.addMarker(new MarkerOptions()
                        .position(usersLatLng)
                        .title("My Location"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(usersLatLng).zoom(14.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.moveCamera(cameraUpdate);
                Log.d("testing", "Maps Fragment: Retrieved users latlng: "+usersLatLng);
            } else {
                //If nno users location received, Set default latlng to dublin
                usersLatitude = 53.3498;
                usersLongitude = -6.266155;
                LatLng usersLatLon = new LatLng(usersLatitude, usersLongitude);
                map.addMarker(new MarkerOptions()
                        .position(usersLatLon)
                        .title("Default Location"));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(usersLatLon).zoom(14.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.moveCamera(cameraUpdate);
            }
        }
        ////////////////Get User Selected destination//////////////////////
        Bundle selectedLocation = this.getArguments();
        if (selectedLocation != null) {

            if (selectedLocation.containsKey("waypointsUrl")) {
                //Retrieve Waypoint
                Log.d("testing", "Bundle has waypoint url");
                url = getArguments().getString("waypointsUrl");

                Log.d("testing", "Bundle waypoints url retrieved: " +url);
                map.clear();

                //Retrieve New Destination - last address in list
                String placeName = getArguments().getString("newDestinationPlaceName");
                double sLat = getArguments().getDouble("newDestinationLat", 53.3498);
                double sLon = getArguments().getDouble("newDestinationLng", -6.266155);
                destinationLatLng = new LatLng(sLat, sLon);//DESTINATION


                LatLng bound1 = new LatLng(usersLatLng.latitude, usersLatLng.longitude);
                LatLng bound2 = new LatLng(destinationLatLng.latitude, destinationLatLng.longitude);
                float bear = getBearing(bound1, bound2);

//                map.addMarker(new MarkerOptions()
//                        .position(usersLatLng)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon))
//                        .title("MyLocation"));
//###########################################################################################
//###########################################################################################
//###########################################################################################
//############################    FIX THIS PART!!!!!!!!!!!!!#################################
//###########################################################################################
//###########################################################################################


                List<String> receivedDestinationsList = myDataPasser.getList();
                for(int i = 0; i < receivedDestinationsList.size()-1; i++) {
                   String destinations = receivedDestinationsList.get(i);
                   String[] arr = destinations.split(",");

                   String listWayPlaceName = arr[0];
                   Double listWayLat = Double.parseDouble(arr[arr.length-2]);
                   Double listWayLng = Double.parseDouble(arr[arr.length-1]);
                    LatLng listLatLng = new LatLng(listWayLat, listWayLng);
                    map.addMarker(new MarkerOptions()
                            .position(listLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1))
                            .title(listWayPlaceName));
                }

                map.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title(placeName));


                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(bound1)
                        .include(bound2)
                        .build();
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                map.animateCamera(CameraUpdateFactory.zoomIn());
                map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                //Get bearing fro camera orientation

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(bound1 )      // Sets the center of the map to Mountain View
                        .zoom(13)                   // Sets the zoom
                        .bearing(bear)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
//                sendUrl(url);
                Log.d("testing", "Sent url to Async task Download task");

//                LatLng newLocation2 = new LatLng(wayLat, wayLng);
//                map.addMarker(new MarkerOptions()
//                        .position(usersLatLng).title("My Location")
//                        .position(waypointLatLng).title(wayPlaceName)
//                        .position(destinationLatLng)
//                        .title(placeName));
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(destinationLatLng).zoom(20.0f).build();
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
//                map.moveCamera(cameraUpdate);

            } else {
                String placeName = getArguments().getString("placeName");
                double sLat = getArguments().getDouble("selectedLat", 53.3498);
                double sLon = getArguments().getDouble("selectedLon", -6.266155);
                destinationLatLng = new LatLng(sLat, sLon);
                Log.d("testing", "received selected Location LatLng" + sLat + ", " + sLon);

                url = resultDirectionsApi.ResultDirectionsApi(usersLatLng, destinationLatLng); //pass origin and destination to Directions client to return url
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
//                sendUrl(url);
                map.clear();
//                Drawable circleDrawable = ContextCompat.getDrawable(getContext(), R.drawable.maparrow);


                LatLng bound1 = new LatLng(usersLatLng.latitude, usersLatLng.longitude);
                LatLng bound2 = new LatLng(destinationLatLng.latitude, destinationLatLng.longitude);
                float bear = getBearing(bound1, bound2);
//                map.addMarker(new MarkerOptions()
//                        .position(usersLatLng)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon))
//                        .title("MyLocation"));


                map.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title(placeName));


                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(bound1)
                        .include(bound2)
                        .build();
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                map.animateCamera(CameraUpdateFactory.zoomIn());
                map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                //Get bearing fro camera orientation

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(bound1 )      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(bear)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        }
//        Bundle routes = this.getArguments();
//        if(routes)
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    private float getBearing(LatLng begin, LatLng end) {
        double dLon = (end.longitude - begin.longitude);
        double x = Math.sin(Math.toRadians(dLon)) * Math.cos(Math.toRadians(end.latitude));
        double y = Math.cos(Math.toRadians(begin.latitude))*Math.sin(Math.toRadians(end.latitude))
                - Math.sin(Math.toRadians(begin.latitude))*Math.cos(Math.toRadians(end.latitude)) * Math.cos(Math.toRadians(dLon));
        double bearing = Math.toDegrees((Math.atan2(x, y)));
        return (float) bearing;
    }

    private class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = resultDirectionsApi.downloadUrl(url[0]);
                Log.d("testing", "URL : " +url[0]);
//                Log.d("testing", "URL : " +url[1]);
                Log.d("testing", "API response: " +data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserResponse parserTask = new ParserResponse();

            parserTask.execute(result);
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
                Log.d("testing", "Getting json data: " +jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                Log.d("testing", "Getting JSON response and decoding polyline points");

                routes = parser.parse(jObject);
                Log.d("testing", "Parsing routes: " +routes);

                DistanceJSONParser parser2 = new DistanceJSONParser();
                distanceDuration = parser2.parse(jObject);
                sendData(distanceDuration);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
//            PolylineOptions lineOptions;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();


                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
//                    Log.d("testing", "Route Lat at "+i+":" +lat);
                    double lng = Double.parseDouble(point.get("lng"));
//                    Log.d("testing", "Route Lng at "+i+":" +lng);
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(15);
                lineOptions.color(Color.parseColor("#05b1fb"));
                lineOptions.geodesic(true);
//                lineOptions.setJointType(JointType.ROUND);


            }
            if(line != null){
                line.remove();
            }
            line = map.addPolyline(lineOptions);
            for(int i = 0; i < points.size()-1; i++){
                LatLng segmentP1 = (LatLng) points.get(i);
                LatLng segmentP2 = (LatLng) points.get(i+1);
                List<LatLng> segment = new ArrayList<>(2);
                segment.add(segmentP1);
                segment.add(segmentP2);
                if(PolyUtil.isLocationOnPath(usersLatLng, segment, true, 30)) {
                    LatLng snappedToSegment = getMarkerProjectionOnSegment(usersLatLng, segment, map.getProjection());
                    map.addMarker(new MarkerOptions()
                            .position(snappedToSegment)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon))
                            .title("MyLocation"));
                    break;
                }
            }


            map.addPolyline(lineOptions);
        }
    }

    private LatLng getMarkerProjectionOnSegment(LatLng carPos, List<LatLng> segment, Projection projection) {
        LatLng markerProjection = null;

        Point carPosOnScreen = projection.toScreenLocation(carPos);
        Point p1 = projection.toScreenLocation(segment.get(0));
        Point p2 = projection.toScreenLocation(segment.get(1));
        Point carPosOnSegment = new Point();

        float denominator = (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y);
        // p1 and p2 are the same
        if (Math.abs(denominator) <= 1E-10) {
            markerProjection = segment.get(0);
        } else {
            float t = (carPosOnScreen.x * (p2.x - p1.x) - (p2.x - p1.x) * p1.x
                    + carPosOnScreen.y * (p2.y - p1.y) - (p2.y - p1.y) * p1.y) / denominator;
            carPosOnSegment.x = (int) (p1.x + (p2.x - p1.x) * t);
            carPosOnSegment.y = (int) (p1.y + (p2.y - p1.y) * t);
            markerProjection = projection.fromScreenLocation(carPosOnSegment);
        }
        return markerProjection;
    }

    public interface FragmentToActivity{
        public void communicate(String distance);
        public void startDirections(String urlOnStart);
    }
    private void sendData(String distance){
        mCallback.communicate(distance);
    }
    private void sendUrl(String url) { mCallback.startDirections(url);}

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }
}