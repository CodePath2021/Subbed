package com.example.subbed.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.subbed.MainActivity;
import com.example.subbed.R;
import com.example.subbed.Subscription;
import com.parse.ParseUser;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private ViewPager viewPager;

    private CardView cvChart;
    private PieChart pieChart;
    private TextView tvChartTotal;

    private CardView cvExpensive;
    private TextView tvExpensiveTitle;
    private TextView tvExpensiveSub;
    private TextView tvExpensiveCost;
    private Subscription leastExpensiveSub;
    private Subscription mostExpensiveSub;
    private Subscription nextBillingSub;

    private CardView cvBilling;
    private TextView tvBillingDate;
    private TextView tvBillingSub;
    private TextView tvBillingDue;

    private List<Subscription> allSubs;

    public DashboardFragment(List<Subscription> subs) { allSubs = subs; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        viewPager = getActivity().findViewById(R.id.viewPager);

        cvChart = view.findViewById(R.id.cvChart);
        pieChart = view.findViewById(R.id.piechart);
        tvChartTotal = view.findViewById(R.id.tvChartTotal);

        cvExpensive = view.findViewById(R.id.cvExpensive);
        tvExpensiveTitle = view.findViewById(R.id.tvExpensiveTitle);
        tvExpensiveSub = view.findViewById(R.id.tvExpensiveSub);
        tvExpensiveCost = view.findViewById(R.id.tvExpensiveCost);

        cvBilling = view.findViewById(R.id.cvBilling);
        tvBillingDate = view.findViewById(R.id.tvBillingDate);
        tvBillingSub = view.findViewById(R.id.tvBillingSub);
        tvBillingDue = view.findViewById(R.id.tvBillingDue);

        bind();

        cvChart.setOnClickListener(v -> {
            viewPager.setCurrentItem(0);
        });

        cvExpensive.setOnClickListener(v -> {
            String price = "";
            if(tvExpensiveTitle.getText().equals("Most Expensive Subscription")) {
                tvExpensiveTitle.setText("Least Expensive Subscription");
                tvExpensiveSub.setText(leastExpensiveSub.getName());
                price = String.format("$%.2f", leastExpensiveSub.getPrice());
                if(mostExpensiveSub.getType().equals("Monthly"))
                    tvExpensiveCost.setText(price + " per month");
                else
                    tvExpensiveCost.setText(price + " per year");
            }
            else {
                tvExpensiveTitle.setText("Most Expensive Subscription");
                tvExpensiveSub.setText(mostExpensiveSub.getName());
                price = String.format("$%.2f", mostExpensiveSub.getPrice());
                if(mostExpensiveSub.getType() == "Monthly")
                    tvExpensiveCost.setText(price + " per month");
                else
                    tvExpensiveCost.setText(price + " per year");
            }
        });

        cvBilling.setOnClickListener(v -> {
            viewPager.setCurrentItem(2);
        });

        // TODO: swipe up to refresh
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar.
        inflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.btnLogout:
                // Handle this selection
                ParseUser.logOutInBackground(e -> {
                    if(e != null) {
                        Log.e(TAG, "Error while logging out", e);
                        // TODO: error handling
                        return;
                    }
                    ((MainActivity)getActivity()).goLoginActivity();
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setData() {
        for(Subscription sub : allSubs) {
            // Set the data and color to the pie chart
            pieChart.addPieSlice(
                    new PieModel(
                            sub.getName(),
                            (float) sub.getPrice(),
                            Color.parseColor(sub.getColor()))
            );
        }
        pieChart.startAnimation();
    }

    private Subscription findMostExpensive(List<Subscription> subs) {
        Subscription mostExpensiveSub = null;
        double mostExpensiveCost = 0.0;
        double price;

        for(Subscription sub : subs) {
            price = sub.getPrice();
            if(price >= mostExpensiveCost) {
                mostExpensiveCost = price;
                mostExpensiveSub = sub;
            }
        }
        return mostExpensiveSub;
    }

    private Subscription findLeastExpensive(List<Subscription> subs) {
        Subscription leastExpensiveSub = null;
        double leastExpensiveCost = Double.MAX_VALUE;
        double price;

        for(Subscription sub : subs) {
            price = sub.getPrice();
            if(price <= leastExpensiveCost) {
                leastExpensiveCost = price;
                leastExpensiveSub = sub;
            }
        }

        return leastExpensiveSub;
    }

    private Subscription findNextBillingSub(List<Subscription> subs) {
        Subscription nextBillingSub = null;
        int nearestBillingDate = Integer.MAX_VALUE;
        int days;

        for(Subscription sub : subs) {
            days = sub.computeRemainingDays();
            if(days <= nearestBillingDate) {
                nearestBillingDate = days;
                nextBillingSub = sub;
            }
        }

        return nextBillingSub;
    }

    public void bind() {
        // no subscriptions
        if (allSubs.isEmpty()) {
            tvChartTotal.setText("Total of " + allSubs.size() + " subscriptions");
            tvExpensiveTitle.setText("Most Expensive Subscription");
            return;
        }

        mostExpensiveSub = findMostExpensive(allSubs);
        leastExpensiveSub = findLeastExpensive(allSubs);
        nextBillingSub = findNextBillingSub(allSubs);

        setData();
        tvChartTotal.setText("Total of " + allSubs.size() + " subscriptions");

        tvExpensiveSub.setText(mostExpensiveSub.getName());
        String price = String.format("$%.2f", mostExpensiveSub.getPrice());
        if(mostExpensiveSub.getType().equals("Monthly"))
            tvExpensiveCost.setText(price + " per month");
        else
            tvExpensiveCost.setText(price + " per year");

        int remainingDays = nextBillingSub.computeRemainingDays();
        if(remainingDays == 0)
            tvBillingDate.setText("Today");
        else if(remainingDays == 1)
            tvBillingDate.setText(nextBillingSub.computeRemainingDays() + " day");
        else
            tvBillingDate.setText(nextBillingSub.computeRemainingDays() + " days");
        tvBillingSub.setText(nextBillingSub.getName());
        tvBillingDue.setText("Amount Due: " + String.format("$%.2f", nextBillingSub.getPrice()));
    }
}