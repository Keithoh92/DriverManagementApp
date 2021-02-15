package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.drivermanagement.fragments.ManagementDash1Fragment;
import com.example.drivermanagement.fragments.ManagementDash2Fragment;
import com.example.drivermanagement.fragments.Management_dashboard4;
import com.example.drivermanagement.fragments.managementDash3Fragment;

public class TabsAdaptor1 extends FragmentStatePagerAdapter {

    public TabsAdaptor1(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                ManagementDash1Fragment manDash1Fragment = new ManagementDash1Fragment();
                return manDash1Fragment;

            case 1:
                ManagementDash2Fragment manDash2Fragment = new ManagementDash2Fragment();
                return manDash2Fragment;

            case 2:
                managementDash3Fragment manDash3Fragment = new managementDash3Fragment();
                return manDash3Fragment;

            case 3:
                Management_dashboard4 manDash4Fragment = new Management_dashboard4();
                return manDash4Fragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 4;
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
