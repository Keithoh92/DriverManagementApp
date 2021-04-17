package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.nlopez.smartlocation.SmartLocation;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId, currentUserID, current_state;

    private Toolbar toolbar;
    private TextView userUsername, userEmail, userPhone;
    private CircleImageView usersProfilePicture;
    private Button addDrivers, sendMessage, removeDriver, requestLocation, sendLocation;
    private boolean isNormalUser;
    Fragment mapsFragment;
    FragmentManager fm = getSupportFragmentManager();


    private FirebaseAuth fAuth;
    private DatabaseReference usersRef, DriversRef, LocationRequestsRef, LocationsRef;

    //User to add info
    String imageUri, usersEmail, username;
    //Management users info
    String manImageUri, manEmail, manUsername;
    String currentDate, currentTime;
    Double usersLat, usersLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar_profile);
        userUsername = findViewById(R.id.user_profile_name1);
        userEmail = findViewById(R.id.users_email);
        usersProfilePicture = findViewById(R.id.users_profile_image1);
        userPhone = findViewById(R.id.users_phone_number);
        addDrivers = findViewById(R.id.add_to_drivers_button);
        sendMessage = findViewById(R.id.send_message_profile);
        removeDriver = findViewById(R.id.remove_driver_button);
        sendLocation = findViewById(R.id.send_location);
        sendLocation.setVisibility(View.INVISIBLE);
        requestLocation = findViewById(R.id.request_location_update_button);
        current_state = "new";

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Driver");

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        currentUserID = currentUser.getUid();

        //Get Current Date
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        //Database initialisation
        usersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        DriversRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");
        LocationRequestsRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LocationRequests");
        LocationsRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(currentUserID).child("locations").child(currentDate);

        mapsFragment = fm.findFragmentById(R.id.maps_fragment_profile_activity);

        //Need to perform check on users DB under node locations -> currentDate and get the latest LatLng added by that user
        //Upon sending a request to the driver, they will get notified of the request and can then press the button send location
        //which will get the users location and upload it to their users/locations/currentDate DB
        //if there is no locations there, we hide the maps fragment, if there is we get the latlng pass it in a bundle to the maps fragment and show the
        //maps fragment
//        fm.beginTransaction()
//                .hide(mapsFragment)
//                .commit();


        checkUserAccessLevel();

        if(getIntent().getExtras().containsKey("driversID")){
            receiverUserId = getIntent().getExtras().get("driversID").toString();
            Log.d("TAG", "User id received from Driver username search: "+receiverUserId);
            addDrivers.setVisibility(View.VISIBLE);
            requestLocation.setVisibility(View.INVISIBLE);
        }else if(getIntent().getExtras().containsKey("visit_user_id")){
            receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
            Log.d("TAG", "User id received from choose driver: "+receiverUserId);
            sendMessage.setVisibility(View.VISIBLE);
            if(isNormalUser) {
                removeDriver.setVisibility(View.INVISIBLE);
            }else{
                removeDriver.setVisibility(View.VISIBLE);
            }
        }



        RetrieveUsersInfo();

        addDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewDriver(receiverUserId);
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(ProfileActivity.this, ChatActivity.class);
                chatIntent.putExtra("UserID", receiverUserId);
                chatIntent.putExtra("Username", username);
                startActivity(chatIntent);
            }
        });

        removeDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveDriver();
            }
        });

        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usersEmail = userEmail.getText().toString();
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("*/*");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {usersEmail});
                if(emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent.createChooser(emailIntent, "Choose email client app"));
                }else if(emailIntent.resolveActivity(getPackageManager()) == null) {
                    Toast.makeText(ProfileActivity.this, "There are no email clients installed.", Toast.LENGTH_LONG).show();
                }
                }
        });

        userPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usersPhoneNumber = userPhone.getText().toString();
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse(usersPhoneNumber));
                if(phoneIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(phoneIntent);
                }else if(phoneIntent.resolveActivity(getPackageManager()) == null){
                    Toast.makeText(ProfileActivity.this, "Cant start phone call on this device.", Toast.LENGTH_LONG).show();
                }
            }
        });
        
    }

    private void RemoveDriver() {
        DriversRef.child(currentUserID).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Successfully removed driver from your contacts", Toast.LENGTH_SHORT).show();
                    Log.d("testing", "Successfully removed Driver from DB");
                    Intent goBackAfterDelete = new Intent(ProfileActivity.this, MessagesActivity.class);
                    startActivity(goBackAfterDelete);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Something went wrong, not able to delete driver from your contacts", Toast.LENGTH_SHORT).show();
                Log.d("DataBaseInsertFailed:", e.toString());
            }
        });
    }

    private void RetrieveUsersInfo()
    {
        usersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                username = snapshot.child("username").getValue().toString();
                userUsername.setText(username);
                Log.d("Testing", "Retrieved users username and setting username textview: " + username);

                if((!snapshot.child("image").getValue().toString().equals("")))
                {
                    Log.d("TAG", "Profile image reference exists for this user" +snapshot.child("image").getValue().toString());
                    imageUri = snapshot.child("image").getValue().toString();
                    Picasso.get().load(imageUri).placeholder(R.drawable.profile_image).into(usersProfilePicture);//library to load image from firebase storage into circleimageview
                }

                usersEmail = snapshot.child("email").getValue().toString();
                userEmail.setText(usersEmail);

                ManageLocationRequests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void RetrieveManagementInfo()
    {
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                manUsername = snapshot.child("username").getValue().toString();
                Log.d("Testing", "Retrieved managers username");

                if((!snapshot.child("image").getValue().toString().equals("")))
                {
                    Log.d("TAG", "Profile image reference exists for management" +snapshot.child("image").getValue().toString());
                    manImageUri = snapshot.child("image").getValue().toString();
                }
                manEmail = snapshot.child("email").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void CreateNewDriver(final String userIDReceived){
        RetrieveManagementInfo();
        Log.d("MessagesActivity", "Create new driver called, received: "+userIDReceived);
        Map<String, Object> myManagersID = new HashMap<>();

        HashMap<String, String> myDrivers = new HashMap<>();
        myDrivers.put("username", username);
        myDrivers.put("driverid", userIDReceived);
        if(imageUri != null) {
            myDrivers.put("image", imageUri);
        }else{
            myDrivers.put("image", "");
        }
        myDrivers.put("email", usersEmail);

        myManagersID.put("myManagersID", currentUserID);



        DriversRef.child(currentUserID).child(userIDReceived).setValue(myDrivers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Successfully added new driver to your contacts", Toast.LENGTH_SHORT).show();
                    Log.d("testing", "Successfully added new Driver to DB");
                    addDrivers.setVisibility(View.INVISIBLE);
                    sendMessage.setVisibility(View.VISIBLE);
                    removeDriver.setVisibility(View.VISIBLE);
                    requestLocation.setVisibility(View.VISIBLE);
//                    Intent goBackToMessagesActivity = new Intent(ProfileActivity.this, MessagesActivity.class);
//                    startActivity(goBackToMessagesActivity);
//                    finish();

                    usersRef.child(userIDReceived).updateChildren(myManagersID).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Testing", "Successfully added managersID to users DB");
                        }
                    });

                    DriversRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                if(!snapshot.child(currentUserID).exists()){
                                    HashMap<String, String> insertManager = new HashMap<>();
                                    insertManager.put("username", manUsername);
                                    insertManager.put("email", manEmail);
                                    if(manImageUri != null) {
                                        insertManager.put("image", manImageUri);
                                    }else{
                                        insertManager.put("image", "");
                                    }
                                    insertManager.put("driverid", currentUserID);

                                    DriversRef.child(currentUserID).child(currentUserID).setValue(insertManager).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("testing", "Management added to list of drivers in DB as they were not in there already");
                                        }
                                    });
                                }else{
                                    Log.d("testing", "Management already in drivers DB");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Something went wrong, not able to add new driver to your contacts", Toast.LENGTH_SHORT).show();
                Log.d("DataBaseInsertFailed:", e.toString());
            }
        });
    }

    private void checkUserAccessLevel() {
        Log.d("Contacts", "Checking access level - ID passed: "+currentUserID);
        usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    String retrieveUserType = Objects.requireNonNull(snapshot.child("userType").getValue()).toString();
                    if (retrieveUserType.equals("Management")) {
                        Log.d("ProfileActivity", "User is Management user");
                        CheckForLocationUpdates();

                    }
                    if (retrieveUserType.equals("Driver")) {
                        Log.d("ProfileActivity", "User is normal user");
                        addDrivers.setVisibility(View.INVISIBLE);
                        removeDriver.setVisibility(View.INVISIBLE);
                        requestLocation.setVisibility(View.INVISIBLE);
                        isNormalUser = true;
                    }
                }else{
                    Log.d("ProfileActivity", "No usertype found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//for (DataSnapshot ds : snapshot.getChildren()) {
//        double getLat = ds.child("lat").getValue(Double.class);
//        double getLng = ds.child("lng").getValue(Double.class);
//        String time = ds.child("time").getValue().toString();
//
//        Bundle sendTheLocation = new Bundle();
//        sendTheLocation.putDouble("usersLat", getLat);
//        sendTheLocation.putDouble("usersLng", getLng);
//        sendTheLocation.putString("time", time);
//        MapsFragment2 mf = new MapsFragment2();
//        assert mf != null;
//        mf.setArguments(sendTheLocation);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.maps_fragment_profile_activity, mf)
//                .show(mf)
//                .commit();
//    }
    private void CheckForLocationUpdates()
    {
        //LOCATIONSREF = USERS -> MANAGERSID -> LOCATIONS
        LocationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild(receiverUserId)){
                    LocationsRef.child(receiverUserId).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                    double getLat = ds.child("lat").getValue(Double.class);
                                    double getLng = ds.child("lng").getValue(Double.class);
                                    String time = ds.child("time").getValue().toString();

                                    Bundle sendTheLocation = new Bundle();
                                    sendTheLocation.putDouble("usersLat", getLat);
                                    sendTheLocation.putDouble("usersLng", getLng);
                                    sendTheLocation.putString("time", time);
                                    MapsFragment2 mf = new MapsFragment2();
                                    assert mf != null;
                                    mf.setArguments(sendTheLocation);
                                    getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.maps_fragment_profile_activity, mf)
                                            .commit();
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("ProfileActivity", error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ProfileActivity", error.getMessage());
            }
        });
    }

    //This method is called in the retrieve users info method
    //which will check what the current state of the location requests are in the DB

    //If the manager has already sent a location update to a driver, the button will be disabled on the management side

    //If the user is a driver and has received a location request from the manager,
    // this will trigger the send location button which will send the the latlng to the manager
    //we will have a notification that is sent to the driver on location request which will let
    //the user know there is a request from the manager, this notification will open the profile of their manager and
    //when the user clicks the send location button, retrieve the current location and send it to the managers
    //this will set a value in the managers location received DB along with the user ID that sent it and the latlng, when the manager opens that users profile again, the
    //latlng for that user will be fetched from the DB and the maps fragment will be loaded with that location
    private void ManageLocationRequests()
    {
        LocationRequestsRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                //Here we are checking to see if weve already sent a location request
                if(snapshot.hasChild(receiverUserId))
                {
                    String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                    //will never fire for driver user
                    if(request_type.equals("sent"))
                    {
                        current_state = "request_sent";
                        requestLocation.setText("Cancel Request");
                    }

                    //Will never be fired for management
                    if(request_type.equals("received"))
                    {
                        current_state = "request_received";
                        if(isNormalUser){
                            sendLocation.setEnabled(true);
                            sendLocation.setVisibility(View.VISIBLE);
                        }

                    }
//                    else if(request_type.equals("location_received"))
//                    {
//                        current_state = "request_received";
//                        if(isNormalUser){
//                            sendLocation.setVisibility(View.VISIBLE);
//                        }
//
//                    }
                }else{
                    current_state = "new";
                    requestLocation.setText("Request Location Update");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                requestLocation.setEnabled(false);

                if(current_state.equals("new")){
                    SendLocationRequest();
                }
                if(current_state.equals("request_sent"))
                {
                    CancelLocationRequest();
                }
            }
        });
        sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_state.equals("request_received"))
                {
                    AcceptLocationRequest();
                }

            }
        });


    }

//    private void RetrieveLocation() {
//        usersRef.child(currentUserID).child("locations_received").child(receiverUserId).child(currentDate).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    double getLat = (double) snapshot.child("lat").getValue();
//                    double getLng = (double) snapshot.child("lng").getValue();
//
//                    Bundle sendTheLocation = new Bundle();
//                    sendTheLocation.putDouble("usersLat", getLat);
//                    sendTheLocation.putDouble("usersLng", getLng);
//                    MapsFragment2 mf = new MapsFragment2();
//                    assert mf != null;
//                    mf.setArguments(sendTheLocation);
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.maps_fragment_profile_activity, mf)
//                            .show(mf)
//                            .commit();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    //accept location request
    private void AcceptLocationRequest()
    {
        //On response from driver, First remove the request
        LocationRequestsRef.child(currentUserID).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    LocationRequestsRef.child(receiverUserId).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            GetUsersLocation();
//                            requestLocation.setEnabled(true);
                            current_state = "new";
//                            requestLocation.setText("Request Location Update");
                            sendLocation.setEnabled(false);
                            //receiverID will be the managers ID, use this to access the location received DB of the manager
                            //and add a under the mangers id -> usersid -> and current date the latlng of the user
                        }
                    });
                }

            }
        });
    }

    private void GetUsersLocation()
    {
        //code to retrieve users location
        GetUsersLocation getUsersLocation = new GetUsersLocation();
        getUsersLocation.execute();
    }

    private void CancelLocationRequest()
    {
        LocationRequestsRef.child(currentUserID).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    LocationRequestsRef.child(receiverUserId).child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                requestLocation.setEnabled(true);
                                current_state = "new";
                                requestLocation.setText("Request Location Update");
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendLocationRequest()
    {
//        requestLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

                LocationRequestsRef.child(currentUserID).child(receiverUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            LocationRequestsRef.child(receiverUserId).child(currentUserID).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        requestLocation.setEnabled(true);
                                        requestLocation.setText("Cancel Request");
                                        current_state = "request_sent";
                                    }
                                }
                            });
                        }
                    }
                });
//            }
//        });
    }

    public class GetUsersLocation extends AsyncTask<Void, Void, LatLng>{
        LatLng thisUsersLatLng;




        @Override
        protected LatLng doInBackground(Void... voids) {
            Log.d("testing", "Fetching users location in AsyncTask");
            try {
                final Location usersLocation = SmartLocation.with(ProfileActivity.this).location().getLastLocation();
//        final LatLng usersLatLng = new LatLng(usersLocation.getLatitude(), usersLocation.getLatitude());
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
            HashMap<String, Object> usersLatLng = new HashMap<>();

            usersLatLng.put("lat", latLng.latitude);
            usersLatLng.put("lng", latLng.longitude);
            usersLatLng.put("time", currentTime);

            String locationKey = usersRef.child(receiverUserId).child("locations").child(currentDate).child(currentUserID).push().getKey();

            //REceiverId here is the manager that sent the request, we save the latlng under the managers user account under locations received DB ->
            //current_date-> userID
            //We will send a notification to the manager on reveive of the latlng from user which will then open that users profile
            //and set the map to that latlng stored in the DB
            usersRef.child(receiverUserId).child("locations").child(currentDate).child(currentUserID).child(locationKey).setValue(usersLatLng).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ProfileActivity.this, "Sent location update to management successfully", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(ProfileActivity.this, "Failed to send your location to management", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}