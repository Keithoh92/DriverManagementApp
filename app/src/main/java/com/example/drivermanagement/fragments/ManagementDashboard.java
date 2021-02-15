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

import com.example.drivermanagement.LoginActivity;
import com.example.drivermanagement.MessagesActivity;
import com.example.drivermanagement.R;
import com.google.firebase.auth.FirebaseAuth;

public class ManagementDashboard extends AppCompatActivity {
    Toolbar managementToolbar;
    Button messagesButton, driversButton;
    public Button myButton;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_dashboard);
        managementToolbar = (Toolbar) findViewById(R.id.management_dashboard_toolbar);
        messagesButton = (Button) findViewById(R.id.messagesButton);
        driversButton = (Button) findViewById(R.id.driverButton);

        fAuth = FirebaseAuth.getInstance();

        //        myButton = (Button) findViewById(R.id.chooseButton);

        setSupportActionBar(managementToolbar);
        getSupportActionBar().setTitle("DriverX");

        messagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messagesIntent = new Intent(ManagementDashboard.this, MessagesActivity.class);
                messagesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(messagesIntent);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
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
        if(item.getItemId() == R.id.add_drivers_option)
        {

        }
        if(item.getItemId() == R.id.create_group_option)
        {
//            RequestNewGroup();
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

    @Override
    public void onStart() {
        super.onStart();

//        FragmentManager fragment = getSupportFragmentManager();
//        Fragment myFrag = fragment.findFragmentById(R.id.chooserFrag);
//        fragment.beginTransaction()
//                .hide(myFrag)
//                .commit();

//        FragmentManager fragment1 = getSupportFragmentManager();
//        Fragment myFrag1 = fragment1.findFragmentById(R.id.manDash2);
//        fragment1.beginTransaction()
//                .hide(myFrag1)
//                .commit();

//        myButton.setVisibility(View.INVISIBLE);

//        if(fragment.isH)
    }
}