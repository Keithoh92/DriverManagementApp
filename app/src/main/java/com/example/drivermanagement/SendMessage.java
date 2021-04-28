package com.example.drivermanagement;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/*

    CLASS TO SAVE MESSAGES TO FIREBASE CLOUD MESSAGING

 */
public class SendMessage {

    private DatabaseReference usersRef;

    public void SendingMessage(String message, String userID, String receiverUserID)
    {

        usersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Log.d("testing", "message entered: " +message);
        if(TextUtils.isEmpty(message)){
            Log.d("testing", "no message inserted");
        }else{
            String messageSenderRef = "Messages/"+userID+"/"+receiverUserID;
            String messageReceiverRef = "Messages/"+receiverUserID+"/"+userID;
            Log.d("testing", "creating message references: " +messageSenderRef+", "+messageReceiverRef);


            DatabaseReference messageKeyRef = usersRef.child("Messages").child(userID).child(receiverUserID).push();
            String messagePushID = messageKeyRef.getKey();

            HashMap<String, Object> messageTextBody = new HashMap<>();
            messageTextBody.put("message", message);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", userID);

            HashMap<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            usersRef.updateChildren(messageBodyDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Log.d("testing", "successfully sent message");
                }
            });
        }
    }
}
