package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId, currentUserID, current_state;

    private Toolbar toolbar;
    private TextView userUsername, userEmail, userPhone;
    private CircleImageView usersProfilePicture;
    private Button addDrivers, sendMessage, removeDriver;

    private FirebaseAuth fAuth;
    private DatabaseReference usersRef, DriversRef;

    //User to add info
    String imageUri, usersEmail, username;
    //Management users info
    String manImageUri, manEmail, manUsername;

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
        current_state = "new";

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Driver");

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        assert currentUser != null;
        currentUserID = currentUser.getUid();


        usersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        DriversRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");




        if(getIntent().getExtras().containsKey("driversID")){
            receiverUserId = getIntent().getExtras().get("driversID").toString();
            Log.d("TAG", "User id received from Driver username search: "+receiverUserId);
            addDrivers.setVisibility(View.VISIBLE);
        }else if(getIntent().getExtras().containsKey("visit_user_id")){
            receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
            Log.d("TAG", "User id received from choose driver: "+receiverUserId);
            sendMessage.setVisibility(View.VISIBLE);
            removeDriver.setVisibility(View.VISIBLE);
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

//    private void ManageChatRequests()
//    {
//        sendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//    }
}