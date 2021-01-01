package com.example.drivermanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

public class MessagesActivity extends AppCompatActivity {

    Toolbar messagesToolbar;
    private ViewPager viewPager;
    private TabLayout messagesTablayout;
    private TabsAdaptor myTabsAdaptor;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        messagesToolbar = (Toolbar) findViewById(R.id.messages_toolbar);
        setSupportActionBar(messagesToolbar);
        getSupportActionBar().setTitle("DriverX");

        viewPager = (ViewPager) findViewById(R.id.messages_tab_pager);
        myTabsAdaptor = new TabsAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(myTabsAdaptor);

        messagesTablayout = (TabLayout) findViewById(R.id.tablayout_messages);
        messagesTablayout.setupWithViewPager(viewPager);

    }


}