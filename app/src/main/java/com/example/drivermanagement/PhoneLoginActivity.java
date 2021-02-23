package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.drivermanagement.fragments.ManagementDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendVerificationButton, verifyButton;
    private EditText phoneNumber, verificationCode;
    private String mVerificationId;

    private ProgressBar loadingBar;
    private int pStatus = 0;
    private Handler handler = new Handler();

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthCredential phoneAuthCredential;

    private FirebaseAuth fAuth;
    private FirebaseDatabase dbRef;
    private DatabaseReference RootRef;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        sendVerificationButton = findViewById(R.id.send_verification_button);
        verifyButton = findViewById(R.id.verify_button);
        phoneNumber = findViewById(R.id.phone_number_input);
        verificationCode = findViewById(R.id.verification_number_input);
        loadingBar = findViewById(R.id.progress_bar);
        loadingBar.setVisibility(View.GONE);

        dbRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/");
        RootRef = dbRef.getReference("Users");
        fAuth = FirebaseAuth.getInstance();

        sendVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String phoneNumberInput = phoneNumber.getText().toString();

                if(TextUtils.isEmpty(phoneNumberInput))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(pStatus <= 100){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingBar.setProgress(pStatus);
                                    }
                                });
                                try {
                                    Thread.sleep(100);
                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }
                                pStatus++;
                            }
                        }
                    }).start();
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(fAuth)
                                    .setPhoneNumber(phoneNumberInput)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(PhoneLoginActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                    Log.d("TAG", "Creating phone auth object and connecting to firebase" +options);
                }
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationButton.setVisibility(View.INVISIBLE);
                phoneNumber.setVisibility(View.INVISIBLE);

                String verificationCodeReceived = verificationCode.getText().toString();

                if(TextUtils.isEmpty(verificationCodeReceived))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter verification code", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while(pStatus <= 100){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingBar.setProgress(pStatus);
                                    }
                                });
                                try {
                                    Thread.sleep(100);
                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }
                                pStatus++;
                            }
                        }
                    }).start();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCodeReceived);
                    Log.d("TAG", "Verification code entered and verify buttin clicked" +verificationCodeReceived);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                loadingBar.setVisibility(View.INVISIBLE);
                Log.d("TAG", "Verification Successful" +phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                Log.d("TAG", "Verification Failed", e);

                loadingBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PhoneLoginActivity.this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();

                sendVerificationButton.setVisibility(View.VISIBLE);
                phoneNumber.setVisibility(View.VISIBLE);

                verifyButton.setVisibility(View.INVISIBLE);
                verificationCode.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token)
            {
                super.onCodeSent(verificationId, token);
                Log.d("TAG", "On code Sent" +verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(PhoneLoginActivity.this, "Verification code sent to you", Toast.LENGTH_SHORT).show();

                sendVerificationButton.setVisibility(View.INVISIBLE);
                phoneNumber.setVisibility(View.INVISIBLE);

                verifyButton.setVisibility(View.VISIBLE);
                verificationCode.setVisibility(View.VISIBLE);



            }

//            @Override
//            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
//                super.onCodeAutoRetrievalTimeOut(s);
//            }
        };
    }
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        Log.d("TAG", "signInWithAuthCredentialsCredential got called" +credential);
        fAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("TAG", "Signing in" +credential);
                    loadingBar.setVisibility(View.INVISIBLE);
                    FirebaseUser user = fAuth.getCurrentUser();
                    Toast.makeText(PhoneLoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    checkUserAccessLevel(user.getUid());
                    Log.d("TAG", "Checking users access level" +credential);
                } else {
                    Log.d("TAG", "Login failed" +task.getException());
                    Toast.makeText(PhoneLoginActivity.this, "Login Failed, Please try again, Your number could already be associated with another account!", Toast.LENGTH_LONG).show();
//                    final FirebaseUser prevUser = FirebaseAuth.getInstance().getCurrentUser();
//                    fAuth.signInWithCredential(credential)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    FirebaseUser currentUser = task.getResult().getUser();
//                                    Log.d("TAG", "Login Failed, Getting current user for delete");
//                                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Log.d("TAG", "Deleting current user to merge new credentials with new user login" +credential);
//                                            prevUser.linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                                    Log.d("TAG", "Successfully merged accounts email/phone - pass to checkuseraccesslevel method" +credential);
//                                                    fAuth.signInWithCredential(credential);
//                                                    FirebaseUser user = fAuth.getCurrentUser();
//                                                    checkUserAccessLevel(user.getUid());
//                                                }
//                                            });
//                                        }
//                                    });
//                                    // Merge prevUser and currentUser accounts and data
//                                    // ...
//                                }
//                            });
                }
            }
        });
    }


//        fAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            loadingBar.setVisibility(View.INVISIBLE);
//                            FirebaseUser user = fAuth.getCurrentUser();
//                            Toast.makeText(PhoneLoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
//                            checkUserAccessLevel(user.getUid());
//                        }
//                        else
//                        {
//                            Toast.makeText(PhoneLoginActivity.this, "Login Failed, Please try again", Toast.LENGTH_SHORT).show();
//                        }
//                        }
//                });
//    }
    private void checkUserAccessLevel(String uid) {
        Log.d("TAG", "Check user access level was called, sign in successful" +uid);
        //extract data from document
        RootRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    String retrieveUserType = snapshot.child("userType").getValue().toString();
                    Log.d("TAG", "Usertype is: " +retrieveUserType);
                    if (retrieveUserType.equals("Management")) {
                        startActivity(new Intent(PhoneLoginActivity.this, ManagementDashboard.class));
                        finish();
                    }
                    if (retrieveUserType.equals("Driver")) {
                        startActivity(new Intent(PhoneLoginActivity.this, DriverDashboard.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}