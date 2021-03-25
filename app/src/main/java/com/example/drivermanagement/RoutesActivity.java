package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.annotations.NotNull;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoutesActivity extends AppCompatActivity implements DataPasser, MapsFragment.FragmentToActivity {

    //Location config
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static String BASE_URL= "https://maps.googleapis.com/maps/api/directions/";

    //REMOVE API KEY FROM HERE WHEN USING VERSION CONTROL
//    private static String APPID = ;

    List<StoreModel> storeModels;
    ApiInterface apiInterface;

    //    ConnectionReceiver receiver;
    IntentFilter intentFilter;


    String latlngString;
    LatLng latLng;
    String usersLoc;
    Double selectedLat, selectedLon;
    String placeName = "";
    /////////////////
    RecyclerView recyclerView;
    RecyclerAdapterDestinations recyclerViewAdapter;

    List<String> destinationsList;
    List<LatLng> listOfDestinations;
    private Toolbar toolbar;
    private CardView cardView;
    private TextView ETA;
    Button startButton;
    AutocompleteSupportFragment autocompleteSupportFragment;
    double myLat = 0;
    double myLon = 0;
    String distanceStr = "Dist: 0km | Duration: 0mins";
    String url = "";
//    String myLocationName = "My Location";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        toolbar = findViewById(R.id.toolbar_routes);
        cardView = findViewById(R.id.cardview_layout);
        ETA = findViewById(R.id.eta_textview);
        startButton = findViewById(R.id.start_button);

        ETA.setText(distanceStr);


        destinationsList = new ArrayList<>();
        listOfDestinations = new ArrayList<>();
//        destinationsList.add("Destination 1");
        recyclerView = findViewById(R.id.destination_recycler);
        recyclerViewAdapter = new RecyclerAdapterDestinations(destinationsList);
        recyclerView.setAdapter(recyclerViewAdapter);

        //Initialise BroadcastReceiver for retrieving API response
//        receiver = new ConnectionReceiver();
        intentFilter = new
                IntentFilter("com.example.drivermanagement.ACTION");

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            else {
                fetchLocation();
            }
        } else {
            fetchLocation();
        }


        final Location usersLocation = SmartLocation.with(RoutesActivity.this).location().getLastLocation();
//        final LatLng usersLatLng = new LatLng(usersLocation.getLatitude(), usersLocation.getLatitude());
//        if (usersLocation != null) {
            myLat = usersLocation.getLatitude();
            //53.3498;
            myLon = usersLocation.getLongitude();
            //-6.266155;
//        }
        Log.d("testing", "Users Location: " + myLat + "," + myLon);

//        apiService = APIClient.getClient().create(ApiInterface.class);
        String api_key = getString(R.string.api_key);
//        final String GOOGLE_PLACE_API_KEY1 = Resources.getSystem().getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), api_key);
            PlacesClient placesClient = Places.createClient(this);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri mapsUri = Uri.parse(url);
                Intent mapsIntent = new Intent(Intent.ACTION_VIEW, mapsUri);
                mapsIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapsIntent);
            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Route Finder");

        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_searchbar_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
//        double radius = 250.00;
//        double[] boundsFromLatLng = getBoundsFromLatLng(radius, usersLocation.getLatitude(), usersLocation.getLongitude());
//        autocompleteSupportFragment.setLocationRestriction(RectangularBounds.newInstance(
//                new LatLng(boundsFromLatLng[0], boundsFromLatLng[1]),
//                new LatLng(boundsFromLatLng[2], boundsFromLatLng[3])
//        ));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull final Place place) {
                // TODO: Get info about the selected place.
                placeName = place.getName();
                LatLng selectedPlaceLatLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                selectedLat = selectedPlaceLatLng.latitude;
                selectedLon = selectedPlaceLatLng.longitude;
                Log.d("testing", "Selected Place name: " + placeName + "LatLng: " + selectedLat + "," + selectedLon);
                destinationsList.add(place.getName() + "," + place.getAddress() + "," + selectedLat + "," + selectedLon);
                Log.d("testing", "Added to destinations list: " + place.getAddress() + "," + place.getName() + "," + selectedLat + "," + selectedLon);
                for (int i = 0; i < destinationsList.size(); i++) {
                    Log.d("testing", "Destination List = " + destinationsList.get(i));
                }
                recyclerView.getAdapter().notifyDataSetChanged();

                if (destinationsList.size() == 1) {
                    Bundle addLocation = new Bundle();
                    addLocation.putString("placeName", placeName);
                    addLocation.putDouble("selectedLat", selectedLat);
                    addLocation.putDouble("selectedLon", selectedLon);

                    url = "https://www.google.co.in/maps/dir/";
                    url = url +myLat+","+myLon+"/"+selectedLat+","+selectedLon;

                    MapsFragment mf = new MapsFragment();
                    assert mf != null;
                    mf.setArguments(addLocation);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.maps_fragment_routes_activity, mf)
                            .commit();
                    Log.d("testing", "Sending 1st destination to maps: " + placeName + "LatLng: " + selectedLat + "," + selectedLon);
                } else {
                    //If user selects another desination call processRoutes and pass new destination
                    Log.d("testing", "processRoutes called");
                    processRoutes(placeName, selectedLat, selectedLon);
                }
                /////////Directions Code//////////////////////
//                ResultDirectionsApi directionsApiClient = new ResultDirectionsApi();
//                String url = directionsApiClient.ResultDirectionsApi(usersLatLng, selectedPlaceLatLng); //pass origin and destination to Directions client to return url
//                Intent directionsServiceIntent = new Intent(RoutesActivity.this, DirectionsService.class);
//                directionsServiceIntent.putExtra("DirectionsApiUrl", url);
//                startService(directionsServiceIntent);
//                Log.d("testing", "Starting service, sending origin & Destination: " + url);

                /////////////////////////////////////////////////
                autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        autocompleteSupportFragment.setText("");
                    }
                });
                autocompleteSupportFragment.setHint("Add stop");
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.d("TAG", "An error occurred: " + status);
            }
        });
    }

//    private double[] getBoundsFromLatLng(double radius, double lat, double lng) {
//        double lat_change = radius / 111.2f;
//        double lon_change = Math.abs(Math.cos(lat * (Math.PI / 180)));
//        return new double[]{
//                lat - lat_change,
//                lng - lon_change,
//                lat + lat_change,
//                lng + lon_change
//        };
//    }


    //    Method to retrieve users current location using SmartLocation Library
    void fetchLocation() {
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        latlngString = location.getLatitude() + "," + location.getLongitude();
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                });
    }


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    fetchLocation();
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(RoutesActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(destinationsList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            Log.d("testing", "User reordered destinations");
            String newDest = destinationsList.get(destinationsList.size()-1);
            String[] arr = newDest.split(",");
            String newPlaceName = arr[0];
            double newDestLat = Double.parseDouble(arr[arr.length-2]);
            double newDestLng = Double.parseDouble(arr[arr.length-1]);
            Log.d("testing", "New destination upon destination reordering by user= "+newPlaceName+ "," +newDestLat+ "," +newDestLng);
            processRoutes(newPlaceName, newDestLat, newDestLng);
            Log.d("testing", "Sent new ordering to process routes");
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }
    };

    @Override
    public Double getLat() {
        return myLat;
    }

    @Override
    public Double getLon() {
        return myLon;
    }

    @Override
    public List getList() {
        return destinationsList;
    }


    private void processRoutes(String placeName, double lat, double lng) {
        //Here we take the 1st destination in list and add it is a >>>>>>   waypoint
        //And the 2nd destination is the >>>>>>>                    destination
        //And the origin will be the origin >>>>>>>                 users location

        if (destinationsList.size() > 1) {
            Log.d("testing", "process Routes intialisation");

            String str_origin = "origin=" + myLat + "," + myLon;
            String str_destination = "destination=" + lat + "," + lng;
            String mode = "mode=driving";

            String parameters = str_origin + "&" + str_destination;//origin and destination part of url
            String output = "json?";

            StringBuilder sb = new StringBuilder(parameters + "&waypoints=");
            String barSeperator = "";

            StringBuilder startMapsUrl = new StringBuilder(myLat+","+myLon+"/");
            String sepereator = "";

            //Get new waypoints
            for (int i = 0; i < destinationsList.size()-1; i++) {
                //Get the list item at i
                String destinations = destinationsList.get(i);
                String[] arr = destinations.split(",");
                Double waypointLat = Double.parseDouble(arr[arr.length - 2]);
                Log.d("testing", "Getting Waypoint latitude" + i + " from list: " + waypointLat);
                Double waypointLng = Double.parseDouble(arr[arr.length - 1]);
                Log.d("testing", "Getting Waypoint longitude" + i + " from list: " + waypointLng);

                sb.append(barSeperator + waypointLat + "," + waypointLng);
                barSeperator = "|";

                startMapsUrl.append(sepereator + waypointLat +","+ waypointLng);
                sepereator = "/";
            }
            startMapsUrl.append("/"+lat+","+lng);

            url = "https://www.google.co.in/maps/dir/";
            url = url + startMapsUrl;

            BASE_URL= "https://maps.googleapis.com/maps/api/directions/";
            Log.d("testing", "Reset base url: " +BASE_URL);
            BASE_URL = BASE_URL + output + sb + "&key=" + APPID;
            Log.d("testing", "New url with waypoints: " + BASE_URL);

            //Get new destination - Destination 2 selected
            Bundle addLocation = new Bundle();
            addLocation.putString("waypointsUrl", BASE_URL);
            addLocation.putString("newDestinationPlaceName", placeName);
            addLocation.putDouble("newDestinationLat", lat);
            addLocation.putDouble("newDestinationLng", lng);

            MapsFragment mf = new MapsFragment();
            assert mf != null;
            mf.setArguments(addLocation);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.maps_fragment_routes_activity, mf)
                    .commit();
            Log.d("testing", "2 destinations selected, Sending to maps: " + placeName + "LatLng: " + selectedLat + "," + selectedLon);

        }
//
    }

    @Override
    public void communicate(String distance) {
        ETA.setText(distance);
    }

    @Override
    public void startDirections(final String urlOnStart) {
//        url = urlOnStart
    }
}




//            String originStr = destinationsList.get(0);
//            String[] arr = originStr.split(",");//-> Address, Latitude, Longitude
//
//            String waypointPlaceName = arr[1];
//            Log.d("testing", "Getting Waypoint placename from list: "+waypointPlaceName);
//            Double waypointLat = Double.parseDouble(arr[arr.length-2]);
//            Log.d("testing", "Getting Waypoint latitude from list: "+waypointLat);
//            Double waypointLng = Double.parseDouble(arr[arr.length-1]);
//            Log.d("testing", "Getting Waypoint longitude from list: "+waypointLng);

//            Log.d("testing", "New waypoint = " + waypointPlaceName + "LatLng: " + waypointLat + "," + waypointLng);



//    private void sendLocations()
//    public class ConnectionReceiver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d("testing", "Received Broadcast from service");
//            if(intent.getAction().equals("MyBroadcast")){
//                PolylineOptions routeLines = (PolylineOptions) intent.getExtras().get("polylines");
//                Log.d("testing", "Polylines received from broadcast: "+routeLines.getColor());
//
//
//                Bundle routeDirections = new Bundle();
//                routeDirections.putParcelable("polyLinesResult", routeLines);
//                routeDirections.putString("placeName", placeName);
//                routeDirections.putDouble("selectedLat", selectedLat);
//                routeDirections.putDouble("selectedLon", selectedLon);
//
//
//                MapsFragment mf = new MapsFragment();
//                assert mf != null;
//                mf.setArguments(routeDirections);
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.maps_fragment_routes_activity, mf)
//                        .commit();
//                Log.d("testing", "Sending new route directions to maps" +placeName+ ", " +selectedLat+ ", " + selectedLon);
//            }
//        }

//}