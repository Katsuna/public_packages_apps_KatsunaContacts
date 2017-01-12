package com.katsuna.contacts.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> list;

    public TabsPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int index) {
        return list.get(index);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}