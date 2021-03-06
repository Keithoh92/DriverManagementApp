package com.example.drivermanagement.DriversFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.drivermanagement.DriversFragments.RecievedMessagesFragment;
import com.example.drivermanagement.MapsFragment2;
import com.example.drivermanagement.fragments.ManagementDash1Fragment;
import com.example.drivermanagement.fragments.ManagementDash2Fragment;
import com.example.drivermanagement.fragments.Management_dashboard4;
import com.example.drivermanagement.fragments.managementDash3Fragment;

/*

THIS IS THE TABS ADAPTOR FOR THE DRIVERS DASHBOARD SO THE USER CAN SWITCH BETWEEN DIFFERENT FRAGMENTS ON THE TOP FRAGMENT OF THE DASHBOARD

 */


public class TabsAdaptorDriversDash extends FragmentStatePagerAdapter {

    public TabsAdaptorDriversDash(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                ManagementDash2Fragment manDash2Fragment = new ManagementDash2Fragment();
                return manDash2Fragment;

            case 1:
                MapsFragHolder mapsFragHolder = new MapsFragHolder();
                return mapsFragHolder;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position)
//    {
//        switch (position)
//        {
//            case 0:
//                return "Dash 1";
//
//            case 1:
//                return "Dash 2";
//
//            case 2:
//                return "Dash 3";
//
//            default:
//                return null;
//
//        }
//    }
}
