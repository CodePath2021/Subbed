package com.example.subbed;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.subbed.Fragments.DashboardFragment;
import com.example.subbed.Fragments.FinanceFragment;
import com.example.subbed.Fragments.SubscriptionFragment;

import java.util.List;

public class SimpleFragmentPagerAdapter extends SmartFragmentStatePagerAdapter {

    public static final int NUM_ITEMS = 3;

    private List<Subscription> subs;

    public SimpleFragmentPagerAdapter(FragmentManager fm, List<Subscription> subs) {
        super(fm);
        this.subs = subs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case 0:
                fragment = new SubscriptionFragment(subs);
                break;
            case 1:
                fragment = new DashboardFragment(subs);
                break;
            default:
                return new FinanceFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public int getItemPosition(Object object)
    {
        if (object instanceof DashboardFragment)
            return POSITION_NONE;
        return POSITION_UNCHANGED;
    }
}
