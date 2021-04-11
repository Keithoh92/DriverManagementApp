package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


public class MessagesActivity extends AppCompatActivity {

    Toolbar messagesToolbar;
    private ViewPager viewPager;
    private TabLayout messagesTablayout;
    private TabsAdaptor myTabsAdaptor;

    private FirebaseAuth fAuth;
    private DatabaseReference RootRef;

    Fragment addDrivers = new Fragment();
    FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        addDrivers = fm.findFragmentById(R.id.add_drivers_fragment);

        fm.beginTransaction()
                .hide(addDrivers)
                .commit();

        fAuth = FirebaseAuth.getInstance();
        Log.d("TAG", "Database reference created");
        RootRef = FirebaseDatabase.getInstance("https://drivermanagement-64ab9-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Groups");

        messagesToolbar = (Toolbar) findViewById(R.id.messages_toolbar);
        setSupportActionBar(messagesToolbar);
        getSupportActionBar().setTitle("DriverX");

        //Add chat fragments to viewpager
        viewPager = (ViewPager) findViewById(R.id.messages_tab_pager);
        myTabsAdaptor = new TabsAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(myTabsAdaptor);

        //Create tab layout for viewpager
        messagesTablayout = (TabLayout) findViewById(R.id.tablayout_messages);
        messagesTablayout.setupWithViewPager(viewPager);
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
            SendUSerToFindDriversActivity();
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

    private void SendUSerToFindDriversActivity()
    {
        Intent findDriversIntent = new Intent(MessagesActivity.this, FindDriversActivity.class);
        startActivity(findDriversIntent);
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
}
