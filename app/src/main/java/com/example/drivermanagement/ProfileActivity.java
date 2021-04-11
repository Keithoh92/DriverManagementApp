package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId;

    private Toolbar toolbar;
    private TextView userUsername, userEmail, userPhone;
    private CircleImageView usersProfilePicture;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        toolbar = findViewById(R.id.toolbar_profile);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Driver");



        usersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        Log.d("TAG", "User id received: "+receiverUserId);

        userUsername = findViewById(R.id.user_profile_name1);
        userEmail = findViewById(R.id.users_email);
        usersProfilePicture = findViewById(R.id.users_profile_image1);
        userPhone = findViewById(R.id.users_phone_number);

        RetrieveUsersInfo();

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

    private void RetrieveUsersInfo()
    {
        usersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String username = snapshot.child("username").getValue().toString();
                userUsername.setText(username);

                if((!snapshot.child("image").getValue().toString().equals("")))
                {
                    Log.d("TAG", "Profile image reference exists for this user" +snapshot.child("image").getValue().toString());
                    String imageUri = snapshot.child("image").getValue().toString();
                    Picasso.get().load(imageUri).placeholder(R.drawable.profile_image).into(usersProfilePicture);//library to load image from firebase storage into circleimageview
                }

                String usersEmail = snapshot.child("email").getValue().toString();
                userEmail.setText(usersEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}