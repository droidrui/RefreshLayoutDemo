package com.droidrui.refreshlayoutdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/20.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragmentList;
    private String[] mTitles;

    public TabPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList, String[] titles) {
        super(fm);
        mFragmentList = fragmentList;
        mTitles = titles;
        if (mFragmentList.size() != mTitles.length) {
            throw new RuntimeException("fragment list size != title length");
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
