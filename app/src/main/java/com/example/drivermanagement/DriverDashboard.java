package com.example.drivermanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class DriverDashboard extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    String[] driverNotifications;
    RecyclerViewAdapter recycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        driverNotifications = getResources().getStringArray(R.array.driver_notifications);

        ArrayList<String> message_arr = new ArrayList<>();
        message_arr.add("Can you go to swords and pick up returns?");
        message_arr.add("Steve cancelled, no need to go anymore");

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
}