package com.example.drivermanagement;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
//    Activity listener;


//    public MapsFragment() {
//        // Required empty public constructor
//    }


//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if(context instanceof RoutesActivity){
//            this.listener = (RoutesActivity) context; //MainActivity listener
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            //String cityName1 = getArguments().getString("cityname");
//            String cityName = "Dublin";
////            String latlng = getArguments().getString("latlng");
//
//            double lat1 = getArguments().getDouble("myLat", 53.3498);
//            double lon1 = getArguments().getDouble("myLon", -6.266155);
//            Log.d("testing", "received from fragment" + lon1 + ", " + lat1);

        //Receive latlng from userslocation
        Bundle usersLocation = this.getArguments();
        if (usersLocation != null) {
            String cityName = "My Location";
            double lat1 = getArguments().getDouble("usersLat", 53.3498);
            double lon1 = getArguments().getDouble("usersLon", -6.266155);
            Log.d("testing", "received users location" + lon1 + ", " + lat1);
            LatLng usersLatLon = new LatLng(lat1, lon1);
//            LatLng newLocation1 = new LatLng(lat1, lon1);
            map.addMarker(new MarkerOptions()
                    .position(usersLatLon)
                    .title(cityName));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(usersLatLon).zoom(14.0f).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        }else {
            //Receive latlng from places autocomplete selection
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                String cityName = "Dublin";
                double lat1 = getArguments().getDouble("myLat", 53.3498);
                double lon1 = getArguments().getDouble("myLon", -6.266155);
                Log.d("testing", "received from fragment" + lon1 + ", " + lat1);
//            LatLng newLocation1 = new LatLng(lat1, lon1);

                LatLng newLocation1 = new LatLng(lat1, lon1);
                map.addMarker(new MarkerOptions()
                        .position(newLocation1)
                        .title(cityName));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(newLocation1).zoom(14.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.moveCamera(cameraUpdate);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}