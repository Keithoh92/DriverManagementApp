package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/*
*********************NOT USED*****************************

 */
public class DriverDashboard extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    String[] driverNotifications, messageReplies;
    RecyclerViewAdapter recycleAdapter;
    Toolbar driverToolbar;
    Button messages;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        driverNotifications = getResources().getStringArray(R.array.driver_notifications);
        messageReplies = getResources().getStringArray(R.array.message_replies);
        driverToolbar = (Toolbar) findViewById(R.id.driver_dashboard_toolbar);
        messages = (Button) findViewById(R.id.button);

        fAuth = FirebaseAuth.getInstance();

        setSupportActionBar(driverToolbar);
        getSupportActionBar().setTitle("DriverX");

        ArrayList<String> message_arr = new ArrayList<>();
        message_arr.add("Can you go to swords and pick up returns?");
        //message_arr.add("From: Jacob");

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messagesIntent = new Intent(DriverDashboard.this, MessagesActivity.class);
                messagesIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(messagesIntent);
            }
        });

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, driverNotifications);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner  = findViewById(R.id.notifications_driver_spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DriverDashboard.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter adapter1 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, messageReplies);
        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner1  = findViewById(R.id.message_reply_spinner);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DriverDashboard.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recycleAdapter = new RecyclerViewAdapter(message_arr);
        recycleAdapter.setClickListener(this);
        recyclerView.setAdapter(recycleAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.logout_option)
        {
            fAuth.signOut();
            SendUserToLoginActivity();
        }
        if(item.getItemId() == R.id.add_drivers_option)
        {
        }
        if(item.getItemId() == R.id.settings_option)
        {
            SendUserToChatSettingsActivity();
            return true;
        }
        return true;
    }
    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(DriverDashboard.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void SendUserToChatSettingsActivity()
    {
        Intent settingsIntent = new Intent(DriverDashboard.this, ChatSettingsActivity.class);
        //settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
    }
}