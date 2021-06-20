package com.example.whatsapp.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.whatsapp.Fragments.CallsFragment;
import com.example.whatsapp.Fragments.ChatsFragment;
import com.example.whatsapp.Fragments.StatusFragment;

// Here we are extending FragmentPagerAdapter class
public class FragmentAdapter extends FragmentPagerAdapter {

    // Constructor
    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    // Loading the fragment on the "MainActivity" page
    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return new ChatsFragment();

            case 1:
                return new StatusFragment();

            case 2:
                return new CallsFragment();

            default:
                return new ChatsFragment();

        }

    }

    // Setting the size of tab layout
    @Override
    public int getCount() {
        return 3;
    }



    // Setting the title of tab layout
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title = null;

        if (position == 0) {
            title = "CHATS";
        }

        else if (position == 1) {
            title = "STATUS";
        }

        else if (position == 2) {
            title = "CALLS";
        }

        return title;

    }

}
