package com.example.drivermanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.job.JobInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class GroupChatActivity extends AppCompatActivity {

    ScrollView myScrollView;
    Toolbar toolbar;
    TextView groupChatTextDisplay;
//    LinearLayout myLinearLayout;
    EditText inputGroupMessage;
    ImageButton sendMessage;

    private String currentGroupName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Log.d("TAG", "Retrieved current group name from fragment" +currentGroupName);

        InitialiseFields();


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
}