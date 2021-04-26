package com.example.drivermanagement.DriversFragments;

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

import com.example.drivermanagement.DriversDashboardActivity;
import com.example.drivermanagement.R;
import com.google.android.material.tabs.TabLayout;


public class ChooserDriversFragment2 extends Fragment {

    Activity listener;
    private ViewPager viewPagerDriversChooserFrag2;
    private TabLayout tabLayout;
    private TabsAdaptorDriversDash2 DriversTabsAdaptor2;

    public ChooserDriversFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DriversDashboardActivity) {
            this.listener = (DriversDashboardActivity) context;
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
        return inflater.inflate(R.layout.fragment_chooser_drivers2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPagerDriversChooserFrag2 = (ViewPager) view.findViewById(R.id.drivers_viewpager_2);
        DriversTabsAdaptor2 = new TabsAdaptorDriversDash2(getChildFragmentManager());
        viewPagerDriversChooserFrag2.setAdapter(DriversTabsAdaptor2);
    }
}