package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drivermanagement.fragments.ManagementDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private Button login, register, loginWithPhoneButton;
    private EditText email, password;

    private FirebaseAuth fAuth;
    private FirebaseDatabase dbRef;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerButton);
        loginWithPhoneButton = (Button) findViewById(R.id.login_with_phone_button);
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);

        fAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/");
        RootRef = dbRef.getReference("Users");

        //Go to registration activity when register is clicked
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ProfileTypeActivity.class));
                finish();
            }
        });
        //If user selects login button validate input and call login method
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = email.getText().toString();
                String txtPassword = password.getText().toString();

                if (TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(LoginActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(txtEmail, txtPassword);
                }
            }
        });
        //Allow user to login with phone number
        loginWithPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent phoneLogin = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(phoneLogin);
            }
        });

    }
    //Authenticate users login details in Firebase
    private void loginUser(String txtEmail, String txtPassword) {
        fAuth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("TAG", "Login is succesful" + task.getResult() + task.getException());
                if (task.isSuccessful()) {
                    Log.d("TAG", "Login is succesful");
                    FirebaseUser user = fAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    checkUserAccessLevel(user.getUid());

                } else if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //Check users access level i.e. management or driver and go respective dashboard
    private void checkUserAccessLevel(String uid) {

        RootRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if ((snapshot.exists()) && (snapshot.hasChild("UserType"))) {
                    String retrieveUserType = snapshot.child("UserType").getValue().toString();
                    if (retrieveUserType.equals("Management")) {
                        startActivity(new Intent(LoginActivity.this, ManagementDashboard.class));
                        finish();
                    }
                    if (retrieveUserType.equals("Driver")) {
                        startActivity(new Intent(LoginActivity.this, DriverDashboard.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (fAuth.getCurrentUser() != null) {
            FirebaseUser user = fAuth.getCurrentUser();
            checkUserAccessLevel(user.getUid());
        }
//        } else {
//            fAuth.signOut();
//            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
//            finish();
//        }
    }
}
