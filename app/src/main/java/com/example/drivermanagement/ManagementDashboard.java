package com.example.drivermanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class ManagementDashboard extends AppCompatActivity {
    Toolbar managementToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_dashboard);
        managementToolbar = (Toolbar) findViewById(R.id.management_dashboard_toolbar);

        setSupportActionBar(managementToolbar);
        getSupportActionBar().setTitle("DriverX");

    }
}