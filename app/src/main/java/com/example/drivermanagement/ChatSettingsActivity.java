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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatSettingsActivity extends AppCompatActivity {

    private Button updateButton;
    private EditText userName;
    private ImageView userprofileImage;
    private Uri imgurl;
    private StorageTask uploadTask;
    private Uri downloadUrl;

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
//    FirebaseStorage fStorage;
    private StorageReference fStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_chat);
        fStorage = FirebaseStorage.getInstance().getReference("Images");
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userName = (EditText) findViewById(R.id.set_user_name);
        updateButton = (Button) findViewById(R.id.update_settings_button);
        userprofileImage = (ImageView) findViewById(R.id.profile_image);

        RetrieveUsername();
//        RetrieveProfilePhoto();
//        Log.d("TAG", "Retrieve username");

        userprofileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
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

    private void FileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode==RESULT_OK && data != null && data.getData() != null) {
            imgurl = data.getData();
            userprofileImage.setImageURI(imgurl);
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(ChatSettingsActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();

            } else {
                FileUploader();
            }
        }
    }

    private String getExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void UpdateUserSettings()
    {
        String setUserName = userName.getText().toString();
        FirebaseUser currentUserID = fAuth.getCurrentUser();
        DocumentReference df = fStore.collection("Users").document(currentUserID.getUid());

        df.update("Username", setUserName);

    }

    private void FileUploader()
    {
        final FirebaseUser currentUserID = fAuth.getCurrentUser();
        final StorageReference Ref = fStorage.child(System.currentTimeMillis()+ ","+getExtension(imgurl));
        uploadTask = Ref.putFile(imgurl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
//              ,,,,,,,,,,,,,,,Fix this, change file uploader to upload in bytes and change
                        //,,,,,,,uploadTask to Ref.putBytes(data like so:

//




//                        Task<Uri> task = uploadTask.continueWithTask(new Continuation() {
//                            @Override
//                            public Object then(@NonNull Task task) throws Exception {
//                                return null;
//                            }
//                        })
                                Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                        DocumentReference ref = fStore.collection("Users").document(currentUserID.getUid());
//                                    Map<String, Object> profileImageRef = new HashMap<>();
//                                    profileImageRef.put("Profile Picture", downloadUrl);
                                        ref.update("Profile Picture", downloadUrl);
                                    }
                                });
//                                final String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

//                                     Uri downloadUri = task.getResult();
//                                    DocumentReference ref = fStore.collection("Users").document(currentUserID.getUid());
////                                    Map<String, Object> profileImageRef = new HashMap<>();
////                                    profileImageRef.put("Profile Picture", downloadUrl);
//                                    ref.update("Profile Picture", downloadUrl);

                        // Get a URL to the uploaded content
//                        downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        Toast.makeText(ChatSettingsActivity.this, "Successfully uploaded new profile picture", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(ChatSettingsActivity.this, "Profile picture upload failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }



    private void RetrieveUsername()
    {
        Log.d("TAG", "Retrieve username function got called");
        FirebaseUser currentUser = fAuth.getCurrentUser();
        DocumentReference df = fStore.collection("Users").document(currentUser.getUid());
//        DocumentReference df = fStore.collection("Users").document(currentUser.getUid()).collection("Username").document(currentUser.getUid());
//            String usernameRet = currentUser
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "OnSuccess: " + documentSnapshot.getData());
                String username = documentSnapshot.getString("Username");
                userName.setText(username);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatSettingsActivity.this, "Couldnt retriev username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void RetrieveProfilePhoto()
    {
        Log.d("TAG", "Retrieve profile image reference function got called");
        FirebaseUser currentUser = fAuth.getCurrentUser();
        DocumentReference df = fStore.collection("Users").document(currentUser.getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "OnSuccess profile image reference: " + documentSnapshot.getData());
                if(documentSnapshot.getString("Profile Picture") != "") {
                    String imageUri = documentSnapshot.getString("Profile Picture");
//                    Uri uri = Uri.parse(imageUri);
                    Picasso.get().load(imageUri).transform(new CircleImage()).placeholder(R.drawable.profile_image).noFade().into(userprofileImage);
                }else{
                    Toast.makeText(ChatSettingsActivity.this, "You don't seem to have a profile picture yet", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatSettingsActivity.this, "You don't seem to have a profile picture yet", Toast.LENGTH_SHORT).show();
            }
        });
//            Picasso.get().load(downloadUrl).fit().into(userprofileImage);
//        }else{
//            Toast.makeText(this, "Please choose profile picture", Toast.LENGTH_SHORT).show();
//        }
//        FirebaseUser currentUser = fAuth.getCurrentUser();
//        StorageReference Ref = fStorage.child(System.currentTimeMillis()+ ","+getExtension(imgurl);
//        Ref.getFile(imgurl)
//                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Successfully downloaded data to local file
//                        // ...
//                        userprofileImage.setImageURI(downloadUrl);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle failed download
//                // ...
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(fAuth.getCurrentUser() != null){
            RetrieveProfilePhoto();
            Log.d("TAG", "Retrieve username");
        }
    }
}