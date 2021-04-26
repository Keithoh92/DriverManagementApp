package com.example.drivermanagement.notifications;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {

    private DatabaseReference TokenRef;
    private FirebaseAuth fAuth;

    FirebaseUser currentUser;
    private String userID, device_token;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        currentUser = fAuth.getCurrentUser();
        userID = currentUser.getUid();

        currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if(task.isSuccessful())
                {
                    DatabaseReference reference = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tokens");
                    Token token = new Token(task.getResult().getToken());
                    reference.child(userID).setValue(token);
                }
            }
        });



    }

    //    public void getUserToken(){
//        currenUser = fAuth.getCurrentUser();
//        userID = currenUser.getUid();
//
//        TokenRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
//        TokenRef.child(userID).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists())
//                {
//                    device_token = snapshot.child("device_token").getValue().toString();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }


}
