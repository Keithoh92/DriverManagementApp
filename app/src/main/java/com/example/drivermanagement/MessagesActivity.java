package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity {

    Toolbar messagesToolbar;
    private ViewPager viewPager;
    private TabLayout messagesTablayout;
    private TabsAdaptor myTabsAdaptor;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        messagesToolbar = (Toolbar) findViewById(R.id.messages_toolbar);
        setSupportActionBar(messagesToolbar);
        getSupportActionBar().setTitle("DriverX");

        viewPager = (ViewPager) findViewById(R.id.messages_tab_pager);
        myTabsAdaptor = new TabsAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(myTabsAdaptor);

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

    private void RequestNewGroup()
    {
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

    private void CreateNewGroup(String groupName)
    {
        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null) {
            DocumentReference df = fStore.collection("Groups").document();
            Map<String, Object> groupInfo = new HashMap<>();
            groupInfo.put("Group Name", groupName);
            df.set(groupInfo);
        }else{
            Toast.makeText(MessagesActivity.this, "Couldnt create group", Toast.LENGTH_SHORT).show();
        }
    }
}