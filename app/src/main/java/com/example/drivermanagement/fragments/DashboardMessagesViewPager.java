package com.example.drivermanagement.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.drivermanagement.R;
import com.example.drivermanagement.TabsAdaptor;
import com.google.android.material.tabs.TabLayout;

/*

THIS IS THE FRAGMENT THAT IS ATTACHED TO THE DASHBOARD ON THE DRIVERS SYSTEM MAIN ACTIVITY
EACH FRAGMENT THAT APPEARS ON THE MAIN DRIVER DASHBOARD IS ADDED TO THE VIEWPAGER DEFINED BELOW AND THEN THIS FRAGMENT
IS ADDED TO THE DRIVERS  MAIN DASHBOARD
THE USER CAN THEN FLICK THROUGH THE FRAGMENTS

 */

public class DashboardMessagesViewPager extends Fragment {

    private ViewPager viewPager;
    private TabLayout messagesTablayout;
    private TabsAdaptor myTabsAdaptor;

    public DashboardMessagesViewPager() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_messages_view_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = (ViewPager) view.findViewById(R.id.messages_tab_pager);
        myTabsAdaptor = new TabsAdaptor(getChildFragmentManager());
        viewPager.setAdapter(myTabsAdaptor);

        messagesTablayout = (TabLayout) view.findViewById(R.id.tablayout_messages);
        messagesTablayout.setupWithViewPager(viewPager);
    }
}