package com.example.drivermanagement.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.drivermanagement.CustomMessagesDialog;
import com.example.drivermanagement.CustomMessagesDialogManagersDash;
import com.example.drivermanagement.LoginActivity;
import com.example.drivermanagement.MessagesActivity;
import com.example.drivermanagement.R;
import com.example.drivermanagement.TinyDB;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/*

THIS IS THE MAIN ACTIVITY ON THE MANAGERS DASHBOARD THAT HOLDS ALL THE FRAGMENTS


 */

public class ManagementDashboard extends AppCompatActivity implements CustomMessagesDialog.DialogListener, CustomMessagesDialogManagersDash.DialogListener {
    Toolbar managementToolbar;
    BottomNavigationView bottomNavigationView;
    Button sendLocation;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_dashboard);
        managementToolbar = (Toolbar) findViewById(R.id.management_dashboard_toolbar);

        fAuth = FirebaseAuth.getInstance();

        setSupportActionBar(managementToolbar);
        getSupportActionBar().setTitle("DriverX");

        bottomNavigationView = findViewById(R.id.management_dash_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.messages_menu_button_manage:
                        SendUserToMessagesActivity();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu_management_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.logout_option)
        {
            fAuth.signOut();
            SendUserToLoginActivity();
        }

        if(item.getItemId() == R.id.settings_option)
        {
//            SendUserToChatSettingsActivity();
        }
        return true;
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(ManagementDashboard.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


    private void SendUserToMessagesActivity(){
        Intent messagesIntent = new Intent(ManagementDashboard.this, MessagesActivity.class);
        messagesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(messagesIntent);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    // THIS GETS THE CUSTOM MESSAGE THAT THE USER ADDS TO THE MAIN DASHBOARD FRAGMENT
    // AND SAVES IT INTO PERMANENT STORAGE FOR THAT FRAGMENT TO LOAD ON STARTUP INTO THE CUSTOM MESSAGES DROPDOWNS
    @Override
    public void applyMessages(String messageA, String messageB, String messageC, String messageD, String messageE) {
        ArrayList<String> messageArray = new ArrayList<>();
        if (messageA != null) {
            messageArray.clear();
            messageArray.add("NA");
            messageArray.add(messageA);
            messageArray.add(messageB);
            messageArray.add(messageC);
            messageArray.add(messageD);
            messageArray.add(messageE);
            TinyDB tinyDB = new TinyDB(getApplicationContext());
            tinyDB.putListString("MessagesList", messageArray);

        }
    }
    // THIS GETS THE CUSTOM MESSAGE THAT THE USER ADDS TO THE MAIN DASHBOARD FRAGMENT
    // AND SAVES IT INTO PERMANENT STORAGE FOR THAT FRAGMENT TO LOAD ON STARTUP INTO THE CUSTOM MESSAGES DROPDOWNS
    @Override
    public void applyMessagesManagement(String listName, String messageA, String messageB, String messageC, String messageD, String messageE) {
        ArrayList<String> returnedItems = new ArrayList<>();
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        if(listName != null){
            returnedItems.clear();
            returnedItems.add("NA");
            returnedItems.add(messageA);
            returnedItems.add(messageB);
            returnedItems.add(messageC);
            returnedItems.add(messageD);
            returnedItems.add(messageE);
            if(listName.equals("OrdersList")){
                tinyDB.getListString("OrdersList").clear();
                tinyDB.putListString("OrdersList", returnedItems);
            }
            if(listName.equals("LocationsList")){
                tinyDB.getListString("LocationsList").clear();
                tinyDB.putListString("LocationsList", returnedItems);
            }
            if(listName.equals("MessagesList2")){
                tinyDB.getListString("MessagesList2").clear();
                tinyDB.putListString("MessagesList2", returnedItems);
            }

        }
    }
}