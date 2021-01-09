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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private Button login, register;
    private EditText email, password;
//    private ProgressDialog loadingBar;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerButton);
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editPassword);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ProfileTypeActivity.class));
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = email.getText().toString();
                String txtPassword = password.getText().toString();

                if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(LoginActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                }else if(txtPassword.length() < 6){
                    Toast.makeText(LoginActivity.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                }else{
//                    loadingBar.setTitle("Logging in");
//                    loadingBar.setMessage("Please Wait");
//                    loadingBar.setCanceledOnTouchOutside(true);
//                    loadingBar.show();
                    loginUser(txtEmail, txtPassword);
                }
            }
        });

    }

    private void loginUser(String txtEmail, String txtPassword) {
        fAuth.signInWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("TAG", "Login is succesful" +task.getResult() + task.getException());
                if(task.isSuccessful()){
                    Log.d("TAG", "Login is succesful");
                    FirebaseUser user = fAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

//                    updateUI(user);
                    checkUserAccessLevel(user.getUid());
//                    loadingBar.dismiss();
                }else if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        //extract data from document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "OnSuccess: " + documentSnapshot.getData());
                //Identify the user access

//                    DocumentSnapshot document = task.getResult();
                    String userType = documentSnapshot.getString("UserType");
                    if(userType.equals("Management")){
                        //user is admin
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    if(userType.equals("Driver")){
                        startActivity(new Intent(LoginActivity.this, DriverDashboard.class));
                        finish();
                    }
                }

        });
//        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                Log.d("TAG", "OnSuccess: " + task.getResult().getData());
//                //Identify the user access
//
//                if(task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    String userType = document.getString("UserType");
//                       if(userType.equals("Management")){
//                           //user is admin
//                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                            finish();
//                       }
//                       if(userType.equals("Driver")){
//                           startActivity(new Intent(LoginActivity.this, DriverDashboard.class));
//                           finish();
//                       }
//                }



//                if(task.getResult().getString("UserType").equals("Management")){
//                    //user is admin
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
//                }
//                if(task.getResult().getString("UserType").equals("Driver")){
//                    startActivity(new Intent(LoginActivity.this, DriverDashboard.class));
//                    finish();
//                }

//            }
//        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(fAuth.getCurrentUser() != null)
        {
            DocumentReference df = fStore.collection("Users").document(fAuth.getUid());
            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        checkUserAccessLevel(fAuth.getCurrentUser().getUid());
                    }else{
                        fAuth.signOut();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    fAuth.signOut();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
//        onBackPressed();
    }
}