package com.example.subbed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.subbed.Fragments.DashboardFragment;
import com.example.subbed.Fragments.FinanceFragment;
import com.example.subbed.Fragments.SubscriptionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigation;

    private List<Subscription> subs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        subs = new ArrayList<Subscription>();
        createTestData();

        // listener for the bottom navigation view
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_dashboard:
                        fragment = new DashboardFragment(subs);
                        break;
                    case R.id.action_subscription:
                        fragment = new SubscriptionFragment(subs);
                        break;
                    case R.id.action_finance:
                    default:
                       fragment = new FinanceFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigation.setSelectedItemId(R.id.action_dashboard);
    }

    private void createTestData() {
        subs.add(new Subscription());
        subs.add(new Subscription("Netflix", "$13.99", "21 days", Color.parseColor("#E50914")));
    }
}