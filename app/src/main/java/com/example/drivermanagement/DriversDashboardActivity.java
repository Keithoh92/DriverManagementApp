package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.drivermanagement.fragments.ManagementDashboard;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class DriversDashboardActivity extends AppCompatActivity implements CustomMessagesDialog.DialogListener, CustomMessagesDialog2.DialogListener{

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_dashboard);
        toolbar = findViewById(R.id.drivers_dashboard_toolbar);


        fAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DriverX");

        bottomNavigationView = findViewById(R.id.bottom_nav_drivers);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.messages_menu_button:
                        SendUserToMessagesActivityy();
                        break;
                    case R.id.ocr_menu_button:
                        SendUserToOCRExtractionActivity();
                        break;
                    case R.id.routes_menu_button:
                        SendUserToRoutesActivity();
                        break;
                }
                return true;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu_drivers, menu);
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
            SendUserToChatSettingsActivity();
        }
        return true;
    }

    private void SendUserToChatSettingsActivity() {
        Intent chatSettingsIntent = new Intent(DriversDashboardActivity.this, ChatSettingsActivity.class);
        startActivity(chatSettingsIntent);
    }

    private void SendUserToRoutesActivity() {
        Intent routesIntent = new Intent(DriversDashboardActivity.this, RoutesActivity.class);
        startActivity(routesIntent);
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(DriversDashboardActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


    private void SendUserToMessagesActivityy()
    {
        Intent messagesIntent = new Intent(DriversDashboardActivity.this, MessagesActivity.class);
        startActivity(messagesIntent);
    }

    private void SendUserToOCRExtractionActivity()
    {
        Intent ocrIntent = new Intent(DriversDashboardActivity.this, OCRExtractionActivity.class);
        startActivity(ocrIntent);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    @Override
    public void applyMessages(String messageA, String messageB, String messageC, String messageD, String messageE) {
        ArrayList<String> messageArray = new ArrayList<>();
        if (messageA != null) {
            messageArray.clear();
            messageArray.add(messageA);
            messageArray.add(messageB);
            messageArray.add(messageC);
            messageArray.add(messageD);
            messageArray.add(messageE);
            TinyDB tinyDB = new TinyDB(getApplicationContext());
            tinyDB.putListString("MessagesList", messageArray);
        }
    }

    @Override
    public void applyMessages2(String messageA, String messageB, String messageC, String messageD, String messageE) {
        ArrayList<String> messageArray2 = new ArrayList<>();
        if (messageA != null) {
            messageArray2.clear();
            messageArray2.add(messageA);
            messageArray2.add(messageB);
            messageArray2.add(messageC);
            messageArray2.add(messageD);
            messageArray2.add(messageE);
            TinyDB tinyDB = new TinyDB(getApplicationContext());
            tinyDB.putListString("DriversMessagesList", messageArray2);
        }
    }
}