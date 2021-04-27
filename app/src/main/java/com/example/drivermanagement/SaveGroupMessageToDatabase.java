package com.example.drivermanagement;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SaveGroupMessageToDatabase {

    private DatabaseReference GroupRef, groupMessageKeyRef, dRef;
    String currentUsername;

    public void SendingGroupMessage(String groupName, String currentUser, String message) {

        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        dRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");

        getUserInfo(currentUser);

        String messageKey, currentDate, currentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());


        messageKey = GroupRef.child("Groups").child(currentUser).child("GroupInfo").child(groupName).child("GroupMessages").push().getKey();



        HashMap<String, Object> groupMessageKey = new HashMap<>();


        GroupRef.child("Groups").child(currentUser).child("GroupInfo").child(groupName).child("GroupMessages").updateChildren(groupMessageKey);
        groupMessageKeyRef = GroupRef.child("Groups").child(currentUser).child("GroupInfo").child(groupName).child("GroupMessages").child(messageKey);
            HashMap<String, Object> infoMap = new HashMap<>();
            infoMap.put("username", currentUsername);
            infoMap.put("message", message);
            infoMap.put("date", currentDate);
            infoMap.put("time", currentTime);
            infoMap.put("type", "text");
            infoMap.put("from", currentUser);

            groupMessageKeyRef.updateChildren(infoMap);

    }

    private void getUserInfo(String currentUser) {
        Log.d("TAG", "Retrieving user information from DB");
        dRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    currentUsername = snapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
