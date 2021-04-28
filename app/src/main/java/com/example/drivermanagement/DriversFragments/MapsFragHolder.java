package com.example.drivermanagement.DriversFragments;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.drivermanagement.MapsFragment2;
import com.example.drivermanagement.MapsFragment3;
import com.example.drivermanagement.R;
import com.google.android.gms.maps.model.LatLng;

import io.nlopez.smartlocation.SmartLocation;
/*
This is for the maps fragment on the Drivers Dashboard
 */

public class MapsFragHolder extends Fragment {


    public MapsFragHolder() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps_frag_holder, container, false);
    }

    //Only Update the users location when the user navigates to the fragment
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            GetUsersLocation2();
        }
    }

    private void GetUsersLocation2()
    {
        //code to retrieve users location
        GetUsersLocation2 getUsersLocation2 = new GetUsersLocation2();
        getUsersLocation2.execute();
    }

    public class GetUsersLocation2 extends AsyncTask<Void, Void, LatLng> {
        LatLng thisUsersLatLng;


        @Override
        protected LatLng doInBackground(Void... voids) {
            Log.d("testing", "Fetching users location in AsyncTask");
            try {
                final Location usersLocation = SmartLocation.with(getContext()).location().getLastLocation();
                if (usersLocation != null) {
                    Log.d("testing", "Fetch Users Location: Not null: " + usersLocation.getLatitude() + "," + usersLocation.getLongitude());
                    thisUsersLatLng = new LatLng(usersLocation.getLatitude(), usersLocation.getLongitude());
                }else{
                    Log.d("FetchUsersLocation", "Could not get user location");
                }
            }catch (Exception e){
                Log.d("Background Task", e.toString());
            }
            return thisUsersLatLng;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            //When users location is retrieved update the map
            Bundle getMyLocation = new Bundle();
            getMyLocation.putDouble("myLat", latLng.latitude);
            getMyLocation.putDouble("myLng", latLng.longitude);

            MapsFragment3 mf2 = new MapsFragment3();
            assert mf2 != null;
            mf2.setArguments(getMyLocation);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.maps_fragment_drivers_dash, mf2)
                    .commit();
        }
    }
}