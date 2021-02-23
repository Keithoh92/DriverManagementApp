package com.example.drivermanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
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
import java.util.List;

public class RoutesActivity extends AppCompatActivity {

    //Location config
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;

    List<StoreModel> storeModels;
    ApiInterface apiInterface;

    String latlngString;
    LatLng latLng;
    /////////////////
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

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
        recyclerView = findViewById(R.id.destination_recycler);
        recyclerViewAdapter = new RecyclerViewAdapter(destinationsList);
        recyclerView.setAdapter(recyclerViewAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);



        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

//        permissionsToRequest = findUnAskedPermissions(permissions);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//
//            if (permissionsToRequest.size() > 0)
//                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
//            else {
//                fetchLocation();
//            }
//        } else {
//            fetchLocation();
//        }
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
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                String latlngStr = place.getLatLng().toString();
                Double latlng = Double.parseDouble(latlngStr);
//                String geo = place.get

                Bundle args = new Bundle();
                args.putDouble("latlng", latlng);

                MapsFragment mf = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert mf != null;
                mf.setArguments(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.maps_fragment_routes_activity, mf)
                        .commit();

                Log.d("TAG", "Place: " + place.getName() + ", " + place.getId());
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.d("TAG", "An error occurred: " + status);
            }
        });


    }
    //Method to retrieve users current location using SmartLocation Library
//    private void fetchLocation(){
//        SmartLocation.with(this).location()
//                .oneFix()
//                .start(new OnLocationUpdatedListener() {
//                    @Override
//                    public void onLocationUpdated(Location location)
//                    {
//                        latlngString = location.getLatitude() + "," +location.getLongitude();
//                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    }
//                });
//    }
//
//    //Method to retrieve distance from current location to destination
//    private void fetchDistance(){
//
//    }
}