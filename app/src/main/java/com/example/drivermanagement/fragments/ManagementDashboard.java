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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ManagementDashboard extends AppCompatActivity {
    Toolbar managementToolbar;
    BottomNavigationView bottomNavigationView;
    Button sendLocation;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_dashboard);
        managementToolbar = (Toolbar) findViewById(R.id.management_dashboard_toolbar);
//        sendLocation = findViewById(R.id.accept_location_request);

        fAuth = FirebaseAuth.getInstance();

        //        myButton = (Button) findViewById(R.id.chooseButton);

        setSupportActionBar(managementToolbar);
        getSupportActionBar().setTitle("DriverX");

        bottomNavigationView = findViewById(R.id.management_dash_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.messages_menu_button:
                        SendUserToMessagesActivity();
                        break;
                    case R.id.ocr_menu_button:
                        SendUserToCreateOrderActivity();
                        break;
                }
                return true;
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

    private void SendUserToCreateOrderActivity()
    {

    }
    private void SendUserToMessagesActivity(){
        Intent messagesIntent = new Intent(ManagementDashboard.this, MessagesActivity.class);
        messagesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(messagesIntent);
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