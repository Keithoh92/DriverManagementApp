package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ScrollView myScrollview;
    Toolbar toolbar;
    TextView chatTextDisplay;
    EditText inputMessage;
    ImageButton sendMessage;
    private DatabaseReference ChatRef, usersRef;

    private String userID, currentUsername, receiverUsername, receiverUserID, currentDate, currentTime;
    private RecyclerView userMessagesList;

    private FirebaseUser currentUser;
    private FirebaseAuth fAuth;


    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendMessage = findViewById(R.id.send_message_button);
        inputMessage = findViewById(R.id.input_chat_message);


        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.list_of_messages);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
        userID = currentUser.getUid();
        Log.d("testing", "Current user id: " +userID);

        ChatRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Messages");
        usersRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
//        dRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        Log.d("ChatActivity", "receiver ID received: "+receiverUserID);
        getReceiverInfo();
        getUserInfo();


        toolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(receiverUsername);




//        receiverUsername = getIntent().getExtras().get("Username").toString();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
                Log.d("testing","Called send message method" );

            }
        });
    }

    private void SendMessage() {
        String messageText = inputMessage.getText().toString();
        Log.d("testing", "message entered: " +messageText);
        if(TextUtils.isEmpty(messageText)){
            Log.d("testing", "no message inserted");
        }else{
            String messageSenderRef = "Messages/"+userID+"/"+receiverUserID;
            String messageReceiverRef = "Messages/"+receiverUserID+"/"+userID;
            Log.d("testing", "creating message references: " +messageSenderRef+", "+messageReceiverRef);


            DatabaseReference messageKeyRef = usersRef.child("Messages").child(userID).child(receiverUserID).push();
            String messagePushID = messageKeyRef.getKey();

            HashMap<String, Object> messageTextBody = new HashMap<>();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", userID);

            HashMap<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            usersRef.updateChildren(messageBodyDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("testing", "successful");
                }
            });
        }
    }

//    //initialise view components
//    private void InitialiseFields() {
//
//
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
        usersRef.child("Messages").child(userID).child(receiverUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //current user info
    private void getUserInfo() {
        Log.d("TAG", "Retrieving user information from firestore");
        usersRef.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
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

    private void getReceiverInfo() {
        Log.d("TAG", "Retrieving user information from firestore");
        usersRef.child("Users").child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    receiverUsername = snapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}