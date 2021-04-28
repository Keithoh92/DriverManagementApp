package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/*
USER CAN UPDATE USERNAME HERE AND ADD A PROFILE PHOTO
 */

public class ChatSettingsActivity extends AppCompatActivity {

    private Button updateButton;
    private EditText userName;
    private CircleImageView userprofileImage;
    private FirebaseAuth fAuth;
    private DatabaseReference RootRef;
    private StorageReference fStorage;

    private static final int GALLERY_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_chat);
        fStorage = FirebaseStorage.getInstance().getReference("Images");
        //As Firebase account is setup in europe we need to provide the full path to database reference
        RootRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        fAuth = FirebaseAuth.getInstance();
        userName = (EditText) findViewById(R.id.set_user_name);
        updateButton = (Button) findViewById(R.id.update_settings_button);
        userprofileImage = (CircleImageView) findViewById(R.id.profile_image);

        RetrieveUsername();
        //user clicks on image view new intent is started to initiated choose image from phone
        userprofileImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/'");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_PICK);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UpdateUserSettings();
                Toast.makeText(ChatSettingsActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imgUri = data.getData();

            //EXTERNAL LIBRARY TO CROP THE IMAGE CHOSEN BY THE USER FOR THEIR PROFILE PIC
            CropImage.activity(imgUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();

                final FirebaseUser currentUserID = fAuth.getCurrentUser();
                //Store image under users id and give jpg extension to that image
                StorageReference filePath = fStorage.child(currentUserID.getUid() + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ChatSettingsActivity.this, "Successfully uploaded new profile picture", Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "Successfully uploaded image to storage");
                            //Get the downloadUri reference
                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("TAG", "Successfully uploaded Firebase storage image reference to users database");
                                    //Store firebase storage image reference in database for use in retrieval of image
                                    Uri downloadUri = uri;
                                    Map<String, Object> profilePicMap = new HashMap<String, Object>();
                                    profilePicMap.put("image", downloadUri.toString());
                                    RootRef.child(currentUserID.getUid()).updateChildren(profilePicMap);
                                    RetrieveProfilePhoto();
                                }
                            });

                        }else{
                            Toast.makeText(ChatSettingsActivity.this, "Profile picture upload failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private String getExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    //method to update users username in database
    private void UpdateUserSettings()
    {
        String setUserName = userName.getText().toString();
        FirebaseUser currentUserID = fAuth.getCurrentUser();

        HashMap<String, Object> usernameMap = new HashMap<String, Object>();
        usernameMap.put("username", setUserName);
        RootRef.child(currentUserID.getUid()).updateChildren(usernameMap);

    }
    ////////////////////////Get username from database////////////////////////////
    private void RetrieveUsername()
    {
        Log.d("TAG", "Retrieve username function got called");
        FirebaseUser currentUser = fAuth.getCurrentUser();
        RootRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String username = snapshot.child("username").getValue().toString();
                userName.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatSettingsActivity.this, "Couldnt retrieve username", Toast.LENGTH_SHORT).show();
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////
    }
    /////////////////////new retrieve profile image method with database/////////////////////////////
    private void RetrieveProfilePhoto() {
        Log.d("TAG", "Retrieve profile image reference function got called");
        FirebaseUser currentUser = fAuth.getCurrentUser();
        RootRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((!snapshot.child("image").getValue().toString().equals(""))) {
                    Log.d("TAG", "OnSuccess profile image reference exists" +snapshot.child("image").getValue().toString());
                    String imageUri = snapshot.child("image").getValue().toString();
                    Picasso.get().load(imageUri).into(userprofileImage);//library to load image from firebase storage into circleimageview
                } else {
                    Toast.makeText(ChatSettingsActivity.this, "You don't seem to have a profile picture yet, Please upload new image!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatSettingsActivity.this, "There was an error retrieving Profile Image from Database", Toast.LENGTH_SHORT).show();
            }
        });
    }
        ///////////////////////////////////////////////////////////////////////////////////////
    //get username and profile photo if one has been set and display in UI when user navigates to chat settings activity
    @Override
    protected void onStart() {
        super.onStart();

        if(fAuth.getCurrentUser() != null){
            Log.d("TAG", "onStart Retrieving username & profile image");
            RetrieveUsername();
            RetrieveProfilePhoto();
        }
    }
}