package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.drivermanagement.fragments.ChatFragment;
import com.example.drivermanagement.fragments.ContactsFragment;
import com.example.drivermanagement.fragments.DashboardMessagesViewPager;
import com.example.drivermanagement.fragments.GroupsFragment;
import com.example.drivermanagement.fragments.ManagementDash1Fragment;
import com.example.drivermanagement.fragments.ManagementDash2Fragment;
import com.example.drivermanagement.fragments.Management_dashboard4;
import com.example.drivermanagement.fragments.managementDash3Fragment;

public class TabsAdaptor2 extends FragmentStatePagerAdapter {

    public TabsAdaptor2(@NonNull FragmentManager fm) {
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
                ManagementDash1Fragment manDash1Fragment = new ManagementDash1Fragment();
                return manDash1Fragment;

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
