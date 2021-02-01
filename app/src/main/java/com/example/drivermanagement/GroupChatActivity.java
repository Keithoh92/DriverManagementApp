package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.job.JobInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    ScrollView myScrollView;
    Toolbar toolbar;
    TextView groupChatTextDisplay;
//    LinearLayout myLinearLayout;
    EditText inputGroupMessage;
    ImageButton sendMessage;

    private String currentGroupName, currentUser, currentUsername, currentDate, currentTime;

    private FirebaseAuth fAuth;
    private DatabaseReference dRef, GroupRef, groupMessageKeyRef;
    private FirebaseFirestore fStore, groupRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Log.d("TAG", "Retrieved current group name from fragment" +currentGroupName);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        dRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users");
        GroupRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups").child(currentGroupName);


        InitialiseFields();

        getUserInfo();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SaveMessageToDatabase();
                inputGroupMessage.setText("");
            }
        });


    }

    private void InitialiseFields() {

        toolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessage = (ImageButton) findViewById(R.id.send_message_button);
        groupChatTextDisplay = (TextView) findViewById(R.id.group_chat_text_display);
        inputGroupMessage = (EditText) findViewById(R.id.input_group_message);
        myScrollView = (ScrollView) findViewById(R.id.my_scroll_view);


    }

    private void getUserInfo() {
        Log.d("TAG", "Retrieving user information from firestore");
        dRef.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    currentUsername = snapshot.child("Username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SaveMessageToDatabase()
    {
        String message = inputGroupMessage.getText().toString();
        String messageKey = GroupRef.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(GroupChatActivity.this, "Please type a message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh-mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupRef.updateChildren(groupMessageKey);
            groupMessageKeyRef = GroupRef.child(messageKey);

            HashMap<String, Object> infoMap = new HashMap<>();
            infoMap.put("Username", currentUsername);
            infoMap.put("message", message);
            infoMap.put("date", currentDate);
            infoMap.put("time", currentTime);

            groupMessageKeyRef.updateChildren(infoMap);



            //Set date when app is open with current date
//            LocalDate cal = LocalDate.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//            date.setText(cal.format(formatter));
        }
    }
}