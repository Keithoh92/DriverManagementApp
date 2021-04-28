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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/*

    USERS CAN CHAT INSIDE THE GROUP SELECTED


 */
public class GroupChatActivity extends AppCompatActivity {

    ScrollView myScrollView;
    Toolbar toolbar;
    TextView groupChatTextDisplay;
    EditText inputGroupMessage;
    ImageButton sendMessage;

    private RecyclerView groupMessagesList;
    private final List<GroupMessages> messageList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManagerGroups;
    private GroupMessageAdapter groupMessageAdaptor;


    private String currentGroupName, currentUser, currentUsername, currentDate, currentTime, managementID, messageKey;
    private boolean isNormalUser;

    private FirebaseAuth fAuth;
    private DatabaseReference dRef, GroupRef, groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("GroupName").toString(); //get group name that user selected for display at top of group chat activity
        Log.d("TAG", "Retrieved current group name from fragment" +currentGroupName);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser().getUid();
        //Create 2 database references for users and groups
        dRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        InitialiseFields();
        getUserInfo();
        checkUserAccessLevel();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SaveMessageToDatabase();
                inputGroupMessage.setText("");
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
            }
        });
    }

    //initialise view components
    private void InitialiseFields() {

        toolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        groupMessageAdaptor = new GroupMessageAdapter(messageList);
        groupMessagesList = findViewById(R.id.list_of_group_messages);
        linearLayoutManagerGroups = new LinearLayoutManager(this);
        groupMessagesList.setLayoutManager(linearLayoutManagerGroups);
        groupMessagesList.setAdapter(groupMessageAdaptor);

        sendMessage = (ImageButton) findViewById(R.id.send_message_button);
//        groupChatTextDisplay = (TextView) findViewById(R.id.group_chat_text_display);
        inputGroupMessage = (EditText) findViewById(R.id.input_group_message);
//        myScrollView = (ScrollView) findViewById(R.id.my_scroll_view);


    }

    private void getUserInfo() {
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
    //Save message info to db - Username/Date/time/message
    private void SaveMessageToDatabase()
    {
        String message = inputGroupMessage.getText().toString();

        if(!isNormalUser) {
            messageKey = GroupRef.child("Groups").child(currentUser).child("GroupInfo").child(currentGroupName).child("GroupMessages").push().getKey();
        }else {
            if (managementID != null) {
                messageKey = GroupRef.child("Groups").child(managementID).child("GroupInfo").child(currentGroupName).child("GroupMessages").push().getKey();
            }
        }

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(GroupChatActivity.this, "Please type a message", Toast.LENGTH_SHORT).show();
        }
        else
        {


            HashMap<String, Object> groupMessageKey = new HashMap<>();

            if(!isNormalUser) {
                GroupRef.child("Groups").child(currentUser).child("GroupInfo").child(currentGroupName).child("GroupMessages").updateChildren(groupMessageKey);
                groupMessageKeyRef = GroupRef.child("Groups").child(currentUser).child("GroupInfo").child(currentGroupName).child("GroupMessages").child(messageKey);
            }else {
                if (managementID != null) {
                    GroupRef.child("Groups").child(managementID).child("GroupInfo").child(currentGroupName).child("GroupMessages").updateChildren(groupMessageKey);
                    groupMessageKeyRef = GroupRef.child("Groups").child(managementID).child("GroupInfo").child(currentGroupName).child("GroupMessages").child(messageKey);
                }
            }
            HashMap<String, Object> infoMap = new HashMap<>();
            infoMap.put("username", currentUsername);
            infoMap.put("message", message);
            infoMap.put("date", currentDate);
            infoMap.put("time", currentTime);
            infoMap.put("type", "text");
            infoMap.put("from", currentUser);

            groupMessageKeyRef.updateChildren(infoMap);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (!isNormalUser) {
//            messageKey = GroupRef.child(currentUser).child("GroupInfo").child(currentGroupName).child("GroupMessages").push().getKey();
//            GroupRef.child("Groups").child(managementID).child("GroupInfo").child(currentGroupName).child("GroupMessages");

//        } else {

            GroupRef.child("Groups").child(currentUser).child("GroupInfo").child(currentGroupName).child("GroupMessages").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.exists()) {
                        GroupMessages groupMessages = snapshot.getValue(GroupMessages.class);
                        messageList.add(groupMessages);
                        groupMessageAdaptor.notifyDataSetChanged();

                        groupMessagesList.smoothScrollToPosition(groupMessagesList.getAdapter().getItemCount());

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
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        messageList.clear();
        groupMessageAdaptor.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNormalUser){
            if(managementID != null) {
                GroupRef.child("Groups").child(managementID).child("GroupInfo").child(currentGroupName).child("GroupMessages").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            GroupMessages groupMessages = snapshot.getValue(GroupMessages.class);
                            messageList.add(groupMessages);
                            groupMessageAdaptor.notifyDataSetChanged();
                        } else {
                            Log.d("GroupChat", "Snapshot not found");
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
            }
        }
    }

    private void checkUserAccessLevel() {
        Log.d("Contacts", "Checking access level - ID passed: "+currentUser);
        dRef.child(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.exists()) && (snapshot.hasChild("userType"))) {
                    String retrieveUserType = Objects.requireNonNull(snapshot.child("userType").getValue()).toString();
                    if (retrieveUserType.equals("Management")) {
                        Log.d("Contacts", "User is Management user");
//                        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups").child(currentUser);

                    }
                    if (retrieveUserType.equals("Driver")) {
                        Log.d("Contacts", "User is normal user");

                        if(snapshot.hasChild("myManagersID")) {
                            managementID = Objects.requireNonNull(snapshot.child("myManagersID").getValue()).toString();
                        }//                        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups").child(managementID);
                        isNormalUser = true;
                        if(managementID != null) {
                            GroupRef.child("Groups").child(managementID).child("GroupInfo").child(currentGroupName).child("GroupMessages").addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    if (snapshot.exists()) {
                                        GroupMessages groupMessages = snapshot.getValue(GroupMessages.class);
                                        messageList.add(groupMessages);
                                        groupMessageAdaptor.notifyDataSetChanged();
                                    } else {
                                        Log.d("GroupChat", "Snapshot not found");
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
                        }
                    }
                }else{
                    Log.d("Contacts", "No usertype found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}