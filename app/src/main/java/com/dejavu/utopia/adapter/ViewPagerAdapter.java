package com.dejavu.utopia.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;


public class ViewPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments;

    public ViewPagerAdapter(FragmentActivity fragment, List<Fragment> list) {
        super(fragment);
        this.fragments = list;
    }


    @NonNull
    @Override
    public Fragment createFragment(int i) {
        return fragments.get(i);
    }

    @Override
    public int getItemCount() {
        return fragments != null ? fragments.size() : 0;
    }
}
