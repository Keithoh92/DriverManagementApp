package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class MessagesActivity extends AppCompatActivity implements AddDriversFragment.AddDriversFragmentToActivity {

    Toolbar messagesToolbar;
    private ViewPager viewPager;
    private TabLayout messagesTablayout;
    private TabsAdaptor myTabsAdaptor;

    private FirebaseAuth fAuth;
    private DatabaseReference RootRef, DriversRef;
    public String userIDReceived, userNameReceived;

    private ActivityToFragment sCallback;

    Fragment addDrivers;
    Fragment foundDrivers;
    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
//        addDrivers = new Fragment();
//        foundDrivers = new Fragment();

        fAuth = FirebaseAuth.getInstance();
        Log.d("TAG", "Database reference created");
        RootRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");
        DriversRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Drivers");

        messagesToolbar = (Toolbar) findViewById(R.id.messages_toolbar);
        setSupportActionBar(messagesToolbar);
        getSupportActionBar().setTitle("DriverX");

        //Add chat fragments to viewpager
        viewPager = (ViewPager) findViewById(R.id.messages_tab_pager);
        myTabsAdaptor = new TabsAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(myTabsAdaptor);

//        viewPager.

        //Create tab layout for viewpager
        messagesTablayout = (TabLayout) findViewById(R.id.tablayout_messages);
        messagesTablayout.setupWithViewPager(viewPager);


        addDrivers = fm.findFragmentById(R.id.add_drivers_fragment);
        foundDrivers = fm.findFragmentById(R.id.found_driver_result);


        fm.beginTransaction()
                .hide(addDrivers)
                .hide(foundDrivers)
                .detach(foundDrivers)
                .commit();

//        foundDrivers.onDestroy();

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
            OpenFindDriversFragment();
//            fm.beginTransaction()
//                    .show(addDrivers)
//                    .commit();
        }
        if(item.getItemId() == R.id.create_group_option)
        {
            RequestNewGroup();
        }
        if(item.getItemId() == R.id.settings_option)
        {
            SendUserToChatSettingsActivity();
        }
        return true;
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MessagesActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }

    private void SendUserToChatSettingsActivity()
    {
        Intent settingsIntent = new Intent(MessagesActivity.this, ChatSettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void OpenFindDriversFragment()
    {
        Log.d("OpenFindDrivers", "Called openFindDrivers method");

        fm.beginTransaction()
                .replace(R.id.add_drivers_fragment, addDrivers)
                .show(addDrivers)
                .commit();
//        fm.getFragment(addDrivers).set
        viewPager.setVisibility(View.INVISIBLE);
        messagesTablayout.setVisibility(View.INVISIBLE);
        Log.d("OpenFindDrivers", "Opening find drivers fragment");
    }

    //Called when user wants to create new group
    private void RequestNewGroup()
    {
        //Build group chat creation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MessagesActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");

        final EditText groupNameText = new EditText(MessagesActivity.this);
        groupNameText.setHint("Company Name");
        builder.setView(groupNameText);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameText.getText().toString();

                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MessagesActivity.this, "Please enter a group name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(final String groupName)
    {
        Log.d("TAG", "Create new group got called " +groupName);

            RootRef.child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "Create new group successful");
                        Toast.makeText(MessagesActivity.this, groupName + " created successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MessagesActivity.this, "Failed to create " + groupName, Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

//    @Override
//    public void communicate(String userID, String userName) {
//        userIDReceived = userID;
//        userNameReceived = userName;
//        if(userIDReceived != null && userNameReceived != null){
//            Log.d("MessagesActivity", "Found drivers fragment returned user id: "+userIDReceived+", and username: "+userNameReceived);
//            Log.d("MessagesActivity", "Calling method create new driver");
//            CreateNewDriver(userIDReceived, userNameReceived);
//        }
//    }

    private void CreateNewDriver(final String userIDReceived, final String userNameReceived){
        Log.d("MessagesActivity", "Create new driver called, received: "+userIDReceived+", "+userNameReceived);
        HashMap<String, String> myDrivers = new HashMap<>();
        myDrivers.put("DriverUsername", userNameReceived);
        myDrivers.put("DriverID", userIDReceived);

        FirebaseUser currentUser = fAuth.getCurrentUser();
        String currentUserID = currentUser.getUid();

        DriversRef.child(currentUserID).child(userIDReceived).setValue(myDrivers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MessagesActivity.this, "Successfully added new driver to your contacts", Toast.LENGTH_SHORT).show();
                    Log.d("testing", "Successfully added new Driver to DB");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MessagesActivity.this, "Something went wrong, not able to add new driver to your contacts", Toast.LENGTH_SHORT).show();
                Log.d("DataBaseInsertFailed:", e.toString());
            }
        });
    }

    @Override
    public void comm(String foundDriversID) {
        if(foundDriversID != null){
            Log.d("testing", "Received User ID from add drivers frag " +foundDriversID);
            Intent findDriversIntent = new Intent(MessagesActivity.this, ProfileActivity.class);
            findDriversIntent.putExtra("driversID", foundDriversID);
            startActivity(findDriversIntent);

            Log.d("testing", "Sent user ID and opened Profile activity");
        }
    }

    public interface ActivityToFragment{
        public void sendToFrag(String id);
    }
//    private void send(String id){sCallback.sendToFrag(id);}
//    private void CheckIfUserExistsInContacts(String userID){
//        FirebaseUser currentUser = fAuth.getCurrentUser();
//        String currentUserID = currentUser.getUid();
//
//        DriversRef.child(currentUserID).orderByChild("userid").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    Toast.makeText(MessagesActivity.this, "User is already in your contacts", Toast.LENGTH_SHORT).show();
//                }else{
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}
