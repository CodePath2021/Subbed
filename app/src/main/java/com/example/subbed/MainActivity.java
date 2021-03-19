package com.example.subbed;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private ActionBar actionBar;

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigation;
    private SwipeRefreshLayout swipeContainer;

    private SimpleFragmentPagerAdapter adapter;

    private List<Subscription> subs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkAvailable())
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();

        if(ParseUser.getCurrentUser() == null)
            goLoginActivity();

        actionBar = this.getSupportActionBar();

        subs = new ArrayList<>();
        querySubscriptions();

        bottomNavigation = findViewById(R.id.bottomNavigation);

        swipeContainer = findViewById(R.id.swipeContainer);
        // Scheme colors for animation
        swipeContainer.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );
        swipeContainer.setOnRefreshListener(() -> {
            Log.i(TAG, "refreshing");
            refreshSubscriptions();
        });

        // Find the view pager that will allow the user to swipe between fragments
        viewPager = findViewById(R.id.viewPager);
        // Create an adapter that knows which fragment should be shown on each page
        adapter = new SimpleFragmentPagerAdapter(
                getSupportFragmentManager(),
                subs
        );
        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

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
                        actionBar.setTitle("Subscriptions");
                        break;
                    case 1:
                        bottomNavigation.getMenu().findItem(R.id.action_dashboard).setChecked(true);
                        actionBar.setTitle("Dashboard");
                        break;
                    case 2:
                        bottomNavigation.getMenu().findItem(R.id.action_finance).setChecked(true);
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
        query.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        try {
            subs.addAll(query.find());
        }
        catch (ParseException e) {
            Log.e(TAG, "Issue with getting subscriptions", e);
            // TODO: error handling
        }
    }

    private void refreshSubscriptions() {
        ParseQuery<Subscription> query = ParseQuery.getQuery(Subscription.class);
        query.whereEqualTo(Subscription.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground((objects, e) -> {
            if (e != null)
                Log.e(TAG, "Issue with refresh", e);
            subs.clear();
            subs.addAll(objects);
            adapter.notifyDataSetChanged();
            swipeContainer.setRefreshing(false);
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}