package com.example.drivermanagement.DriversFragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.drivermanagement.MapsFragment2;
import com.example.drivermanagement.fragments.ManagementDash2Fragment;


/*

THIS IS THE TABS ADAPTOR FOR THE DRIVERS DASHBOARD SO THE USER CAN SWITCH BETWEEN DIFFERENT FRAGMENTS ON THE BOTTOM FRAGMENT OF THE DASHBOARD

 */

public class TabsAdaptorDriversDash2 extends FragmentStatePagerAdapter {

    public TabsAdaptorDriversDash2(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                OrdersFragment ordersFragment = new OrdersFragment();
                return ordersFragment;

            case 1:
                RecievedMessagesFragment recievedMessagesFragment = new RecievedMessagesFragment();
                return recievedMessagesFragment;

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
