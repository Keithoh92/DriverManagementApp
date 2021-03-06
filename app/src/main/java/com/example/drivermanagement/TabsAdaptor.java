package com.example.drivermanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.drivermanagement.fragments.ChatFragment;
import com.example.drivermanagement.fragments.ContactsFragment;
import com.example.drivermanagement.fragments.GroupsFragment;


///////////// TABS ADAPTOR FOR MESSAGES ACTIVITY VIEWPAGER


public class TabsAdaptor extends FragmentStatePagerAdapter {

    public TabsAdaptor(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
            ChatFragment chatFragment = new ChatFragment();
            return chatFragment;

            case 1:
                GroupsFragment groupFragment = new GroupsFragment();
                return groupFragment;

            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            default:
                return null;

        }
    }
}
