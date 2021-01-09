package com.example.drivermanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileTypeActivity extends AppCompatActivity {

    private Button managementButtonRegister, driverButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_type);
        managementButtonRegister = (Button) findViewById(R.id.managementButtonRegister);
        driverButtonRegister = (Button) findViewById(R.id.driveRbuttonRegister);

        managementButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register1Intent = new Intent(ProfileTypeActivity.this, RegisterActivity.class);
                register1Intent.putExtra("userType", "Management");
                startActivity(register1Intent);
            }
        });

        driverButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register2Intent = new Intent(ProfileTypeActivity.this, RegisterActivity.class);
                register2Intent.putExtra("userType", "Driver");
                startActivity(register2Intent);
            }
        });
    }
}