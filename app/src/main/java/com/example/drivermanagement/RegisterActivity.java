package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/////////////////////// USER REGISTRATION /////////////////////////////



public class RegisterActivity extends AppCompatActivity {

    private EditText emailRegister, passwordRegister, companyName, username;
    private Button registerButton;
    String[] registerCreds;
    String userType = "";
    String deviceToken;
    private boolean usernameExists;

    private FirebaseAuth auth;
    FirebaseUser currentUser;
    private FirebaseDatabase dbRef;
    private DatabaseReference RootRef;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerCreds = getResources().getStringArray(R.array.register_cred);
        emailRegister = findViewById(R.id.editEmailRegister);
        passwordRegister = findViewById(R.id.editPasswordRegister);
        companyName = findViewById(R.id.companyName);
        username = findViewById(R.id.usernameTextview);
        registerButton = findViewById(R.id.registerButton);
        dbRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/");
        RootRef = dbRef.getReference("Users");

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if(intent.getExtras().getString("userType").equals("Management"))
        {
            userType = intent.getStringExtra("userType");
        }
        if(intent.getExtras().getString("userType").equals("Driver"))
        {
            userType = intent.getStringExtra("userType");
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = emailRegister.getText().toString();
                String txtPassword = passwordRegister.getText().toString();
                String txtCompanyName = companyName.getText().toString();
                String txtUsername = username.getText().toString();

                RootRef.orderByChild("username").equalTo(txtUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            usernameExists = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword) || TextUtils.isEmpty(txtCompanyName) || TextUtils.isEmpty(txtCompanyName) || TextUtils.isEmpty(txtUsername)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials, Please fill in all fields!", Toast.LENGTH_SHORT).show();
                }else if(txtPassword.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                }else if(usernameExists){
                    Toast.makeText(RegisterActivity.this, "Username is taken, Please choose another", Toast.LENGTH_SHORT).show();
                }
                else{
                    registerUser(txtEmail, txtPassword);
                }
            }
        });
    }

    private void registerUser(String txtEmail, String txtPassword)
    {
        auth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    currentUser = auth.getCurrentUser();
                    currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if(task.isSuccessful()){
                                deviceToken = task.getResult().getToken();
                                Log.d("RegisterActivity", "Device token received");
                            }else{
                                deviceToken = "";
                                Log.d("RegisterActivity", "Device token not received");
                            }
                        }
                    });
                    String currentUserId = auth.getCurrentUser().getUid();
                    RootRef.child(currentUserId).setValue("");


                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("companyName", companyName.getText().toString());
                    userInfo.put("uid", currentUserId);
                    userInfo.put("email", emailRegister.getText().toString());
                    userInfo.put("username", username.getText().toString());
                    userInfo.put("userType", userType);
                    userInfo.put("image", "");
                    userInfo.put("device_token", deviceToken);
                    RootRef.child(currentUserId).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful())
                                 {
                                     Toast.makeText(RegisterActivity.this, "Successfully created user profile", Toast.LENGTH_SHORT).show();

                                 }
                                 else
                                 {
                                     String message = task.getException().toString();
                                     Toast.makeText(RegisterActivity.this, message+" Error creating profile, please try again", Toast.LENGTH_SHORT).show();
                                 }
                        }
                    });


                    startActivity(new Intent(RegisterActivity.this, PhoneLoginActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Registration Failed, Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}