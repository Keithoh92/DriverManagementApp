package com.example.drivermanagement.fragments;

import android.app.Activity;
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
import com.example.drivermanagement.TabsAdaptor1;
import com.example.drivermanagement.TabsAdaptor2;
import com.google.android.material.tabs.TabLayout;

public class chooserDashManFragment2 extends Fragment {

    Activity listener;
    //    Button chooseThisDash;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAdaptor2 myTabsAdaptor;

    public chooserDashManFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ManagementDashboard) {
            this.listener = (ManagementDashboard) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chooser_dash_man2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.dash_chooser2_tab_pager);
        myTabsAdaptor = new TabsAdaptor2(getChildFragmentManager());
        viewPager.setAdapter(myTabsAdaptor);
    }
}