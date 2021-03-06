package com.example.subbed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private ActionBar actionBar;

    private BottomNavigationView bottomNavigation;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DotIndicator dotIndicator;

    private SimpleFragmentPagerAdapter adapter;

    private List<Subscription> subs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ParseUser.getCurrentUser() == null)
            goLoginActivity();

        actionBar = this.getSupportActionBar();

        subs = new ArrayList<>();
        querySubscriptions();

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Find the view pager that will allow the user to swipe between fragments
        viewPager = findViewById(R.id.viewpager);
        // Create an adapter that knows which fragment should be shown on each page
        adapter = new SimpleFragmentPagerAdapter(
                getSupportFragmentManager(),
                subs
        );
        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

//        tabLayout = findViewById(R.id.tabDots);
//        tabLayout.setupWithViewPager(viewPager, true);

        dotIndicator = findViewById(R.id.navDots);

        // listener for the bottom navigation view
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_subscription:
                    viewPager.setCurrentItem(0);
                    actionBar.setTitle("Subscriptions");
                    break;
                case R.id.action_dashboard:
                    viewPager.setCurrentItem(1);
                    actionBar.setTitle("Dashboard");
                    break;
                case R.id.action_finance:
                    viewPager.setCurrentItem(2);
                    actionBar.setTitle("Finance");
                    break;
            }
            return true;
        });

        // To select correct tabs after swiping
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigation.getMenu().findItem(R.id.action_subscription).setChecked(true);
                        dotIndicator.setSelectedItem(0, true);
                        actionBar.setTitle("Subscriptions");
                        break;
                    case 1:
                        bottomNavigation.getMenu().findItem(R.id.action_dashboard).setChecked(true);
                        dotIndicator.setSelectedItem(1, true);
                        adapter.notifyDataSetChanged();
                        actionBar.setTitle("Dashboard");
                        break;
                    case 2:
                        bottomNavigation.getMenu().findItem(R.id.action_finance).setChecked(true);
                        dotIndicator.setSelectedItem(2, true);
                        actionBar.setTitle("Finance");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // set default selection
        viewPager.setCurrentItem(1);
    }

    public void goLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void querySubscriptions() {
        ParseQuery<Subscription> query = ParseQuery.getQuery(Subscription.class);
        query.include(Subscription.KEY_USER);
        query.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        try {
            subs.addAll(query.find());
            for(Subscription sub : subs) {
                Log.d(TAG, sub.getName());
            }
        }
        catch (ParseException e) {
            Log.e(TAG, "Issue with getting subscriptions", e);
            // TODO: error handling
        }
    }
}