package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.drivermanagement.notifications.Client;
import com.example.drivermanagement.notifications.NotificationModel;
import com.example.drivermanagement.notifications.Response;
import com.example.drivermanagement.notifications.RootModel;
import com.example.drivermanagement.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {

    private RecentMessages mCallback;

    ScrollView myScrollview;
    Toolbar toolbar;
    TextView chatTextDisplay;
    EditText inputMessage;
    ImageButton sendMessage;
    private DatabaseReference ChatRef, usersRef, LastMessagesRef;
    private static boolean isInForeground;

    private String userID, currentUsername,fromUsername, receiverUsername, receiverUserID, currentDate, currentTime;
    private RecyclerView userMessagesList;

    private FirebaseUser currentUser;
    private FirebaseAuth fAuth;


    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    boolean notify = false;

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
        LastMessagesRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

//        dRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

//        chatIntent.putExtra("UserID", receiverUserId);
//        chatIntent.putExtra("Username", username);
        if(getIntent().getExtras().containsKey("UserID")){
            receiverUserID = getIntent().getExtras().get("UserID").toString();
            receiverUsername = getIntent().getExtras().get("Username").toString();
        }
        if(getIntent().getExtras().containsKey("visit_user_id")){
            receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
            receiverUsername = getIntent().getExtras().get("visit_user_name").toString();
        }
        else if(getIntent().getExtras().containsKey("MessengersID")) {
            receiverUserID = getIntent().getExtras().get("MessengersID").toString();
        }
        Log.d("ChatActivity", "receiver ID received: "+receiverUserID);

        getReceiverInfo();
        getUserInfo();
        currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                updateToken(task.getResult().getToken());
            }
        });


        toolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(receiverUsername);

    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(userID).setValue(token1);
    }

//    private void SendMessage() {
//        String messageText = inputMessage.getText().toString();
//        Log.d("testing", "message entered: " +messageText);
//        if(TextUtils.isEmpty(messageText)){
//            Log.d("testing", "no message inserted");
//        }else{
//            String messageSenderRef = "Messages/"+userID+"/"+receiverUserID;
//            String messageReceiverRef = "Messages/"+receiverUserID+"/"+userID;
//            Log.d("testing", "creating message references: " +messageSenderRef+", "+messageReceiverRef);
//
//
//            DatabaseReference messageKeyRef = usersRef.child("Messages").child(userID).child(receiverUserID).push();
//            String messagePushID = messageKeyRef.getKey();
//
//            HashMap<String, Object> messageTextBody = new HashMap<>();
//            messageTextBody.put("message", messageText);
//            messageTextBody.put("type", "text");
//            messageTextBody.put("from", userID);
//
//            HashMap<String, Object> messageBodyDetails = new HashMap<>();
//            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
//
//            usersRef.updateChildren(messageBodyDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d("testing", "successful");
//                    if(notify) {
//                        sendNotification(receiverUserID, currentUsername, messageText);
//                    }
//                    notify = false;
//                }
//            });
//        }
//    }

    private void sendNotification(String receiverUserID, final String currentUsername, final String messageText) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Tokens");
        Query query = reference.orderByKey().equalTo(receiverUserID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sd : snapshot.getChildren()) {
                    Token token = sd.getValue(Token.class);
                    Log.d("ChatAct", "Token received: "+token.getToken());
//                    NotificationModel notificationModel =

                    RootModel rootModel = new RootModel(token.getToken(), new NotificationModel(userID, R.mipmap.ic_launcher, currentUsername+": "+messageText, "New Message",
                            receiverUserID));

                    APIService apiService = Client.getClient().create(APIService.class);
                    retrofit2.Call<ResponseBody> responseCall = apiService.sendNotification(rootModel);

                    responseCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                            Log.d("ChatAct","Successfully notification send by using retrofit.");

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
//                    apiService.sendNotification(rootModel)
//                            .enqueue(new Callback<Response>(){
//
//
//                                @Override
//                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//                                    if(response.code() == 200)
//                                    {
//                                        if(response.body().success != 1)
//                                        {
//                                            Log.d("ChatActivity", "Failed to send notification: ");
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<Response> call, Throwable t) {
//
//                                }
//                            });
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
        Log.d("Chat activity", "OnStart called");
        usersRef.child("Messages").child(userID).child(receiverUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();

                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

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

        LastMessagesRef.child("Messages").child(userID).child(receiverUserID).orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messages = snapshot.getValue(Messages.class);
                if(messages.getFrom().equals(userID)){
                    Log.d("ReceivedFrag", "Not sending notification message because its from this user");
                }else {
                    if (!isInForeground) {
                        String fromUser = getMessageFromUsername(messages.getFrom());

                        Intent notificationServiceMessageReceived = new Intent(getApplicationContext(), NotificationsService.class);
                        notificationServiceMessageReceived.putExtra("messageReceivedFrom", receiverUserID);
                        notificationServiceMessageReceived.putExtra("message", messages.getMessage());
                        notificationServiceMessageReceived.putExtra("fromUsername", fromUser);
                        getApplicationContext().startService(notificationServiceMessageReceived);
                    }
                }
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

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("testing","Called send message method" );
                notify = true;
                SendMessage sendMessage = new SendMessage();
                sendMessage.SendingMessage(inputMessage.getText().toString(), userID, receiverUserID);
                inputMessage.setText("");
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
            }
        });

    }




    private String getMessageFromUsername(String from) {
            Log.d("Contacts", "Getting username - ID passed: "+from);
            usersRef.child("Users").child(from).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                        fromUsername = snapshot.child("username").getValue().toString();
                        Log.d("Contacts", "username retrieved: "+fromUsername);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return fromUsername;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
        Log.d("Chat activity", "OnPause called");
        messagesList.clear();
        messageAdapter.notifyDataSetChanged();

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Chat activity", "OnResume called");
        isInForeground = true;


    }

    //current user info
    private void getUserInfo() {
        Log.d("TAG", "Retrieving user information from firebase");
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
        Log.d("TAG", "Retrieving user information from firebase");
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

    public interface RecentMessages{
        public void sendMessages(String messageSending);
    }
    private void sendTheMessage(String messageSending){ mCallback.sendMessages(messageSending);}

}