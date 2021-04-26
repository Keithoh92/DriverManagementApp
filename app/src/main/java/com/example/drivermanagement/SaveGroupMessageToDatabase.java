package com.example.drivermanagement;

import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SaveGroupMessageToDatabase {

    private DatabaseReference GroupRef, groupMessageKeyRef;

    public void SendingGroupMessage(String groupName, String currentUser, String message) {

        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

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
            infoMap.put("username", currentUser);
            infoMap.put("message", message);
            infoMap.put("date", currentDate);
            infoMap.put("time", currentTime);
            infoMap.put("type", "text");
            infoMap.put("from", currentUser);

            groupMessageKeyRef.updateChildren(infoMap);

    }
}
