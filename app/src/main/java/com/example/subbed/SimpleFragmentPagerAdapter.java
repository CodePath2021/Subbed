package com.example.subbed;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
        Fragment fragment;
        switch(position) {
            case 0:
                fragment = new SubscriptionFragment(subs, this);
                break;
            case 1:
                fragment = new DashboardFragment(subs);
                break;
            default:
                fragment = new FinanceFragment();
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
        if (object instanceof DashboardFragment || object instanceof SubscriptionFragment)
            return POSITION_NONE;
        return POSITION_UNCHANGED;
    }
}
