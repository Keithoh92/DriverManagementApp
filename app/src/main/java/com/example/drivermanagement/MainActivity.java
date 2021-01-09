package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.time.LocalDate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView title, date, heading1, chosenRecipients;
    Button messageButton, driverButton, notification1, notification2, notification3, notification4, notification5, clearButton, sendNotifications, chooseRecipients;
    Toolbar managementToolbar;

    FirebaseAuth fAuth;

    String[] contacts;
    boolean[] checkedContacts;
    ArrayList<Integer> selectedContacts = new ArrayList<>();

    String[] locationsArray = {"Locations", "Baldoyle", "Coolock", "Blanchardstown", "Santry"};
    String[] messageArray = {"Choose message", "How long will you be?", "How many deliveries have you left?", "What is your status", "Can you call when you get the chance"};


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        date = (TextView) findViewById(R.id.date);
        heading1 = (TextView) findViewById(R.id.heading1);
        messageButton = (Button) findViewById(R.id.messageButton);
        driverButton = (Button) findViewById(R.id.driverButton);
        notification1 = (Button) findViewById(R.id.not1);
        notification2 = (Button) findViewById(R.id.not2);
        notification3 = (Button) findViewById(R.id.not3);
        notification4 = (Button) findViewById(R.id.not4);
        notification5 = (Button) findViewById(R.id.not5);
        sendNotifications = (Button) findViewById(R.id.sendNotifications);
        chooseRecipients = (Button) findViewById(R.id.chooseRecipients);
        chosenRecipients = (TextView) findViewById(R.id.chosenRecipients);
        managementToolbar = (Toolbar) findViewById(R.id.management_dashboard_toolbar);


        contacts = getResources().getStringArray(R.array.recipents);
        checkedContacts = new boolean[contacts.length];

        chooseRecipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle(R.string.dialog_title);
                mBuilder.setMultiChoiceItems(contacts, checkedContacts, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if(isChecked){
                            if(!selectedContacts.contains(position)){
                                selectedContacts.add(position);
                            }else{
                                selectedContacts.remove(position);
                            }
                        }
                    }
                });
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for(int i = 0; i < selectedContacts.size(); i++){
                            item = item + contacts[selectedContacts.get(i)];
                            if(i != selectedContacts.size() -1){
                                item = item + ", ";
                            }
                        }
                         chosenRecipients.setText(item);
                    }
                });
                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setNeutralButton(R.string.clear_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < checkedContacts.length; i++){
                            checkedContacts[i] = false;
                            selectedContacts.clear();
                            chosenRecipients.setText("");
                        }
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });






        setSupportActionBar(managementToolbar);
        getSupportActionBar().setTitle("DriverX");

        //Set app name in toolbar
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null){
//            actionBar.setTitle("DriverX");
//        }

        //Set date when app is open with current date
        LocalDate cal = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        date.setText(cal.format(formatter));

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, locationsArray);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner  = findViewById(R.id.locationSpinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter adapter1 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, messageArray);
        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner1 = findViewById(R.id.messageSpinner);
        spinner1.setAdapter(adapter1);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        }
        return true;
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToChatSettingsActivity()
    {
        Intent settingsIntent = new Intent(MainActivity.this, ChatSettingsActivity.class);
        startActivity(settingsIntent);
    }


//    public void setAdapter(ArrayAdapter adapter) {
//        this.adapter = adapter;
//        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//    }
    //setDropDownViewResource(R.layout.spinner_dropdown_item);


}