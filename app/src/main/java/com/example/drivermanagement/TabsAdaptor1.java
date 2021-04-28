package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.drivermanagement.fragments.ManagementDash1Fragment;
import com.example.drivermanagement.fragments.ManagementDash2Fragment;
import com.example.drivermanagement.fragments.Management_dashboard4;
import com.example.drivermanagement.fragments.managementDash3Fragment;

///////////////// TABS ADAPTOR FOR MANAGERS DASHBOARD //////////////////

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
                managementDash3Fragment manDash3Fragment = new managementDash3Fragment();
                return manDash3Fragment;

            case 1:
                Management_dashboard4 manDash4Fragment = new Management_dashboard4();
                return manDash4Fragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

}
