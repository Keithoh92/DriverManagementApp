package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DriversDashboardActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_dashboard);


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
                }
                return true;
            }
        });

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
}