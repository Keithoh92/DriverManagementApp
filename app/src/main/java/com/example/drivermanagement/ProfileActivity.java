package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    private TextView userUsername, userEmail, userPhone;
    private CircleImageView usersProfilePicture;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        Log.d("TAG", "User id received: "+receiverUserId);

        userUsername = findViewById(R.id.user_profile_name1);
        userEmail = findViewById(R.id.users_email);
        usersProfilePicture = findViewById(R.id.users_profile_image1);

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
        
        RetrieveUSersInfo();
    }

    private void RetrieveUSersInfo()
    {
        usersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String username = snapshot.child("Username").getValue().toString();
                userUsername.setText(username);

                if((!snapshot.child("image").getValue().toString().equals("")))
                {
                    Log.d("TAG", "Profile image reference exists for this user" +snapshot.child("image").getValue().toString());
                    String imageUri = snapshot.child("image").getValue().toString();
                    Picasso.get().load(imageUri).placeholder(R.drawable.profile_image).into(usersProfilePicture);//library to load image from firebase storage into circleimageview
                }else {
                    Toast.makeText(ProfileActivity.this, "You don't seem to have a profile picture yet, Please upload new image!", Toast.LENGTH_SHORT).show();
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}