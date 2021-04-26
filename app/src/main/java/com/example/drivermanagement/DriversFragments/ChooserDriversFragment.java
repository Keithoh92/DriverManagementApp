package com.example.drivermanagement.DriversFragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.drivermanagement.DriversDashboardActivity;
import com.example.drivermanagement.R;
import com.google.android.material.tabs.TabLayout;


public class ChooserDriversFragment extends Fragment {

    Activity listener;
    private ViewPager viewPagerDriversChooserFrag1;;
    private TabLayout tabLayout;
    private TabsAdaptorDriversDash DriversTabsAdaptor;

    public ChooserDriversFragment() {
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
        return inflater.inflate(R.layout.drivers_chooser_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPagerDriversChooserFrag1 = (ViewPager) view.findViewById(R.id.drivers_viewpager_1);
        DriversTabsAdaptor = new TabsAdaptorDriversDash(getChildFragmentManager());
        viewPagerDriversChooserFrag1.setAdapter(DriversTabsAdaptor);

    }
}