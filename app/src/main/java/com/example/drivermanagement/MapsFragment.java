package com.example.drivermanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener{

    private GoogleMap map;


//    public MapsFragment() {
//        // Required empty public constructor
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
        LatLng dublin = new LatLng(-6.266155, 53.3498);
        googleMap.addMarker(new MarkerOptions()
                .position(dublin)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(dublin));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //String cityName1 = getArguments().getString("cityname");
            String cityName = "Dublin";
            double lat1 = getArguments().getDouble("lat", 53.3498);
            double lon1 = getArguments().getDouble("lon", -6.266155);
//            Log.d("testing", "received from fragment" + lon1 + ", " + lat1 + ", " + cityName1);
            LatLng newLocation1 = new LatLng(lat1, lon1);
            map.addMarker(new MarkerOptions()
                    .position(newLocation1)
                    .title(cityName));
            map.moveCamera(CameraUpdateFactory.newLatLng(newLocation1));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}