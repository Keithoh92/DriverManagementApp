package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.annotations.NotNull;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoutesActivity extends AppCompatActivity {

    //Location config
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;

    List<StoreModel> storeModels;
    ApiInterface apiInterface;

    String latlngString;
    LatLng latLng;
    String usersLoc;
    /////////////////
    RecyclerView recyclerView;
    RecyclerAdapterDestinations recyclerViewAdapter;

    List<String> destinationsList;
    private Toolbar toolbar;
    private CardView cardView;
    private TextView ETA;
    Button startButton;
    AutocompleteSupportFragment autocompleteSupportFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        toolbar = findViewById(R.id.toolbar_routes);
        cardView = findViewById(R.id.cardview_layout);
        ETA = findViewById(R.id.eta_textview);
        startButton = findViewById(R.id.start_button);

        destinationsList = new ArrayList<>();
//        destinationsList.add("Destination 1");
        recyclerView = findViewById(R.id.destination_recycler);
        recyclerViewAdapter = new RecyclerAdapterDestinations(destinationsList);
        recyclerView.setAdapter(recyclerViewAdapter);

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

        double myLat = 0;
        double myLon = 0;
        Location usersLocation = SmartLocation.with(RoutesActivity.this).location().getLastLocation();

        if(usersLocation != null){
            myLat = usersLocation.getLatitude();
            myLon = usersLocation.getLongitude();
        }
        Log.d("testing", "Users Location: " + myLat + "," +myLon);

        Bundle bundle = new Bundle();
        bundle.putDouble("usersLat", myLat);
        bundle.putDouble("usersLon", myLon);

        MapsFragment mf = new MapsFragment();
        assert mf != null;
        mf.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.maps_fragment_routes_activity, mf)
                .commit();
////
//                Log.d("TAG", "Place: " + place.getName() + ", " + place.getId());

//
//
//        apiService = APIClient.getClient().create(ApiInterface.class);
        String api_key = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), api_key);
            PlacesClient placesClient = Places.createClient(this);
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Route Finder");

        autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_searchbar_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull final Place place) {
                // TODO: Get info about the selected place.
//                String address = place.getAddress();
                destinationsList.add(place.getName());
                recyclerView.getAdapter().notifyDataSetChanged();
                autocompleteSupportFragment.getView().findViewById(R.id.places_autocomplete_clear_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        autocompleteSupportFragment.setText("");
//                        cardView.setVisibility(View.GONE);
                    }
                });
//                String address = place.getAddress().toString();
//                destinationsList.add(address);

                autocompleteSupportFragment.setHint("Add stop");


            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.d("TAG", "An error occurred: " + status);
            }
        });
    }
//    Method to retrieve users current location using SmartLocation Library
void fetchLocation(){
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location)
                    {
                        latlngString = location.getLatitude() + "," +location.getLongitude();
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                });
    }

//    private void showLocation(Location location) {
//        if (location != null) {
////            final String text = String.format("Latitude %.6f, Longitude %.6f",
//                    location.getLatitude(),
//                    location.getLongitude());
//            usersLoc = text;
//        }
//    }

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

//
//    //Method to retrieve distance from current location to destination
//    private void fetchDistance(){
//
//    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(destinationsList, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}