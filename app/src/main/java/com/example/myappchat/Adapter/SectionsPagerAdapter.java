package com.example.myappchat.Adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myappchat.fragments.ChatFragment;
import com.example.myappchat.fragments.FriendsFragment;
import com.example.myappchat.fragments.RequiresFragment;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {


    public SectionsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatFragment();

            case 1:
                return new FriendsFragment();

            case 2:
                return new RequiresFragment();

            default:
                return new ChatFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "Trò chuyện";
                break;
            case 1:
                title = "Bạn bè";
                break;
            case 2:
                title = "Kết bạn";
                break;
        }
        return title;
    }
}
