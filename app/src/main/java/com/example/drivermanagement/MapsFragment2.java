package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

public class MapsFragment2 extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap map;
//    Activity listener;
    double lat1 = 53.3498;
    double lon1 = -6.266155;
    String lastTimeUploaded;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        if(context instanceof ProfileActivity){
//            this.listener = (ProfileActivity) context; //MainActivity listener
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment2 =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map2);
        if (mapFragment2 != null) {
            mapFragment2.getMapAsync(this);
        }
    }



    @Override
    public void onMapLoaded() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            double lat1 = getArguments().getDouble("usersLat", -33);
            double lon1 = getArguments().getDouble("usersLng", 131);
            String lastTimeUploaded = getArguments().getString("time");
            Log.d("testing", "received from profile activity fragment: " + lon1 + ", " + lat1);
            LatLng newLocation1 = new LatLng(lat1, lon1);
            map.addMarker(new MarkerOptions()
                    .position(newLocation1)
                    .title("I was here at: "+lastTimeUploaded));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(newLocation1).zoom(14.0f).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapLoadedCallback(this);
    }

//    @Override
//    public void comm(double lat, double lng, String time) {
//        lat1 = lat;
//        lon1 = lng;
//        lastTimeUploaded = time;
//    }
}