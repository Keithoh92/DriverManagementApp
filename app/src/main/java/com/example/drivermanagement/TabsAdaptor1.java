package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
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
