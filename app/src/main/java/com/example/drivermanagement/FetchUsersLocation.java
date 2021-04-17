package com.example.drivermanagement;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import io.nlopez.smartlocation.SmartLocation;

public class FetchUsersLocation extends AsyncTask<Void, Void, LatLng> {

    LatLng usersLatLng;
    @SuppressLint("StaticFieldLeak")
    protected RoutesActivity context;
    public FetchUsersLocation(RoutesActivity _context){
        context = _context;
    }


    @Override
    protected LatLng doInBackground(Void... voids) {
        Log.d("testing", "Fetching users location in AsyncTask");
        try {
            final Location usersLocation = SmartLocation.with(context).location().getLastLocation();
//        final LatLng usersLatLng = new LatLng(usersLocation.getLatitude(), usersLocation.getLatitude());
            if (usersLocation != null) {
                Log.d("testing", "Fetch Users Location: Not null: " + usersLocation.getLatitude() + "," + usersLocation.getLongitude());
                usersLatLng = new LatLng(usersLocation.getLatitude(), usersLocation.getLongitude());
            }else{
                Log.d("FetchUsersLocation", "Could not get user location");
            }
        }catch (Exception e){
            Log.d("Background Task", e.toString());
        }
        return usersLatLng;
    }

    @Override
    protected void onPostExecute(LatLng latLng) {
        super.onPostExecute(latLng);
        context.myLat = usersLatLng.latitude;
        context.myLon = usersLatLng.longitude;
    }

}
