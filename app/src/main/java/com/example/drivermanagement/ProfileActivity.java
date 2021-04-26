package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private ValueEventListener listener;
    private ValueEventListener listener2;
    private ValueEventListener listener3;

    private static final String CHANNEL_ID = "5643";

    private FirebaseAuth fAuth;
    private DatabaseReference usersRef, DriversRef, LocationRequestsRef, LocationsRef, NotificationRef;
    private static boolean isInForeground;

    //User to add info
    String imageUri, usersEmail, username;
    //Management users info
    String manImageUri, manEmail, manUsername;
    String currentDate, currentTime, time;
    String managersID = "";
    Double getLat, getLng;

    UsersLocation usersLocation;

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
//        current_state;

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
        LocationsRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("DriversLocations");
        NotificationRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Notifications");

        mapsFragment = fm.findFragmentById(R.id.maps_fragment_profile_activity);

        //Need to perform check on users DB under node locations -> currentDate and get the latest LatLng added by that user
        //Upon sending a request to the driver, they will get notified of the request and can then press the button send location
        //which will get the users location and upload it to their users/locations/currentDate DB
        //if there is no locations there, we hide the maps fragment, if there is we get the latlng pass it in a bundle to the maps fragment and show the
        //maps fragment




        checkUserAccessLevel1();

        //getting profile owners id and validating access levels
        if(getIntent().getExtras().containsKey("driversID")){
            receiverUserId = getIntent().getExtras().get("driversID").toString();
            Log.d("TAG", "User id received from Driver username search: "+receiverUserId);
            addDrivers.setVisibility(View.VISIBLE);
            requestLocation.setVisibility(View.INVISIBLE);
        }
        if(getIntent().getExtras().containsKey("visit_user_id")){
            receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
            Log.d("TAG", "User id received from choose driver: "+receiverUserId);
            sendMessage.setVisibility(View.VISIBLE);
            if(isNormalUser) {
                removeDriver.setVisibility(View.INVISIBLE);
            }else{
                removeDriver.setVisibility(View.VISIBLE);
            }
        }if(getIntent().getExtras().containsKey("ManagersID")){
            receiverUserId = getIntent().getExtras().get("ManagersID").toString();
            if(isNormalUser){
                removeDriver.setVisibility(View.INVISIBLE);
                sendMessage.setVisibility(View.VISIBLE);
            }
        }else if(getIntent().getExtras().containsKey("DriversID1")){
            receiverUserId = getIntent().getExtras().get("DriversID1").toString();
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ProfileActivity", "OnStart called Current state = "+current_state);
//        ManageLocationRequests();
//        CheckForLocationUpdates();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isInForeground = true;
        Log.d("ProfileActivity", "OnResume called Current state = "+current_state);
        if(!isNormalUser){
            CheckForLocationUpdates();
            Log.d("ProfileActivity", "Current state = "+current_state);
        }
//        ManageLocationRequests();


    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
        Log.d("ProfileActivity", "OnPause called Current state = "+current_state);
//        LocationsRef.child(currentUserID).child(currentDate).child(receiverUserId).orderByKey().limitToLast(1).removeEventListener(listener);
//        LocationRequestsRef.child(currentUserID).removeEventListener(listener2);
        usersRef.child(currentUserID).removeEventListener(listener3);
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

    private void checkUserAccessLevel1() {
        Log.d("Contacts", "Checking access level - ID passed: "+currentUserID);
        listener3 = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    String retrieveUserType = Objects.requireNonNull(snapshot.child("userType").getValue()).toString();
                    if (retrieveUserType.equals("Management")) {
                        Log.d("ProfileActivity", "User is Management user");
//                        CheckForLocationUpdates();

                    }
                    if (retrieveUserType.equals("Driver")) {
                        Log.d("ProfileActivity", "User is normal user");
                        addDrivers.setVisibility(View.INVISIBLE);
                        removeDriver.setVisibility(View.INVISIBLE);
                        requestLocation.setVisibility(View.INVISIBLE);
                        isNormalUser = true;
//                        GetUsersLocation();
                    }
                }else{
                    Log.d("ProfileActivity", "No usertype found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.child(currentUserID).addValueEventListener(listener3);
    }

//    private void CheckForLocationUpdates() {
//        //LOCATIONSREF = USERS -> MANAGERSID -> LOCATIONS
//
//    }

    private void CallFragment() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Bundle sendTheLocation = new Bundle();
                sendTheLocation.putDouble("usersLat", usersLocation.getLat());
                sendTheLocation.putDouble("usersLng", usersLocation.getLng());
                sendTheLocation.putString("time", usersLocation.getTime());
                Log.d("ProfileActivity", "Preparing to replace Maps Fragment");
                MapsFragment2 mf = new MapsFragment2();
                assert mf != null;
                mf.setArguments(sendTheLocation);
                fm.beginTransaction()
                        .replace(R.id.maps_fragment_profile_activity, mf)
                        .commitAllowingStateLoss();
            }
        });
        Toast.makeText(ProfileActivity.this, "Click on marker to see time of drivers location update", Toast.LENGTH_LONG).show();
        current_state = "new";
        requestLocation.setText("Request Location Update");
    }


    public void CheckForLocationUpdates() {
        Log.d("ProfileActivity", "Checking for Updates called");

        LocationsRef.child(currentUserID).child(currentDate).child(receiverUserId).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        usersLocation = ds.getValue(UsersLocation.class);
                        Log.d("ProfileActivity", "Retrieved location for user: " + receiverUserId + ", location: " + usersLocation.getLat() + ", " + usersLocation.getLng() + ", " + usersLocation.getTime());
                        if(!isInForeground) {
                            Log.d("ProfileActivity", "Starting notification service");
                            Intent notificationServiceLocationReceived = new Intent(getApplicationContext(), NotificationsService.class);
                            notificationServiceLocationReceived.putExtra("DriversID", receiverUserId);
                            getApplicationContext().startService(notificationServiceLocationReceived);
//                        CallFragment();
                            current_state = "location_received";
                            requestLocation.setText("Get Users Location");
                        }
                        current_state = "location_received";
                        requestLocation.setText("Get Users Location");
                    }
                } else {
                    Log.d("profileActivity", "Couldnt find any locations for this user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("profileActivity", error.getMessage());
            }
        });
//        LocationsRef.child(currentUserID).child(currentDate).child(receiverUserId).orderByKey().limitToLast(1).addValueEventListener(listener);
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
        Log.d("ProfileActivity", "ManageLocationResuests called Current state = "+current_state);
        LocationRequestsRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!snapshot.exists()) {
//                    current_state = "new";
//                    requestLocation.setText("Request Location Update");
                    Log.d("ProfileActivity", "ManageLocationResuests no requests found Current state = " + current_state);
                }

                if(snapshot.exists()) {
                    //Here we are checking to see if weve already sent a location request
                    Log.d("ProfileActivity", "ManageLocationResuests request found Current state = " + current_state);
                    if (snapshot.hasChild(receiverUserId)) {
                        String request_type = snapshot.child(receiverUserId).child("request_type").getValue().toString();

                        //will never fire for driver user
                        if (request_type.equals("sent")) {
                            current_state = "request_sent";
                            requestLocation.setText("Cancel Request");
                            Log.d("ProfileActivity", "ManageLocationResuests method Current state = "+current_state);
                        }

                        //Will never be fired for management
                        if (request_type.equals("received")) {
                            current_state = "request_received";
                            Log.d("ProfileActivity", "ManageLocationResuests method Current state = "+current_state);
                            if (isNormalUser) {
                                sendLocation.setEnabled(true);
                                sendLocation.setVisibility(View.VISIBLE);
                                }

                            //Get Senders ID
                            NotificationRef.child(currentUserID).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            managersID = ds.child("from").getValue().toString();
                                            if (!managersID.equals("")) {
                                                if (!isInForeground) {
                                                    Log.d("ProfileActivity", "Starting notification service");
                                                    Intent notificationService = new Intent(getApplicationContext(), NotificationsService.class);
                                                    notificationService.putExtra("ManagerID", managersID);
                                                    getApplicationContext().startService(notificationService);
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            }
                        }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ProfileActivity", "Database error: "+error.getMessage());
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
                if(current_state.equals("location_received"))
                {
                    CallFragment();
                }
//                if(current_state.equals("location_received")){
//                    CallFragment();
//                }
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
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                GetUsersLocation();
//                            requestLocation.setEnabled(true);
//                            current_state = "new";
//                            requestLocation.setText("Request Location Update");
                                sendLocation.setEnabled(false);
                                sendLocation.setVisibility(View.INVISIBLE);

                                //receiverID will be the managers ID, use this to access the location received DB of the manager
                                //and add a under the mangers id -> usersid -> and current date the latlng of the user
                            }
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
        Log.d("ProfileActivity", "Send Location Requests called");

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
                                        HashMap<String, String> locationNotification = new HashMap<>();
                                        locationNotification.put("from", currentUserID);
                                        locationNotification.put("type", "request");

                                        NotificationRef.child(receiverUserId).push().setValue(locationNotification).addOnCompleteListener(new OnCompleteListener<Void>() {
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

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());


                Bundle sendTheLocation = new Bundle();
                sendTheLocation.putDouble("usersLat", latLng.latitude);
                sendTheLocation.putDouble("usersLng", latLng.longitude);
                sendTheLocation.putString("time", currentTime);

                MapsFragment2 mf = new MapsFragment2();
                assert mf != null;
                mf.setArguments(sendTheLocation);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.maps_fragment_profile_activity, mf)
                        .commit();

                HashMap<String, Object> usersLatLng = new HashMap<>();


                usersLatLng.put("lat", latLng.latitude);
                usersLatLng.put("lng", latLng.longitude);
                usersLatLng.put("time", currentTime);

                DatabaseReference locationKeyRef = LocationsRef.child(receiverUserId).child(currentDate).child(currentUserID).push();
                String locationKey = locationKeyRef.getKey();

                //REceiverId here is the manager that sent the request, we save the latlng under the managers user account under locations received DB ->
                //current_date-> userID
                //We will send a notification to the manager on reveive of the latlng from user which will then open that users profile
                //and set the map to that latlng stored in the DB
                LocationsRef.child(receiverUserId).child(currentDate).child(currentUserID).child(locationKey).setValue(usersLatLng).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Sent location update to management successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to send your location to management", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }


}