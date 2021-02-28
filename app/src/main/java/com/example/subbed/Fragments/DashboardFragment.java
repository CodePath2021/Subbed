package com.example.subbed.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.subbed.R;
import com.example.subbed.Subscription;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    private CardView cvChart;
    private PieChart pieChart;
    private TextView tvChartTotal;

    private CardView cvExpensive;
    private TextView tvExpensiveTitle;
    private TextView tvExpensiveSub;
    private TextView tvExpensiveCost;
    private Subscription leastExpensiveSub;
    private Subscription mostExpensiveSub;

    private CardView cvBilling;
    private TextView tvBillingDate;
    private TextView tvBillingSub;
    private TextView tvBillingDue;

    private List<Subscription> allSubs;

    public DashboardFragment(List<Subscription> subs) {
        allSubs = subs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Dashboard");

        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);

        cvChart = view.findViewById(R.id.cvChart);
        pieChart = view.findViewById(R.id.piechart);
        tvChartTotal = view.findViewById(R.id.tvChartTotal);

        cvExpensive = view.findViewById(R.id.cvExpensive);
        tvExpensiveTitle = view.findViewById(R.id.tvExpensiveTitle);
        tvExpensiveSub = view.findViewById(R.id.tvExpensiveSub);
        tvExpensiveCost = view.findViewById(R.id.tvExpensiveCost);
        mostExpensiveSub = findMostExpensive(allSubs);
        leastExpensiveSub = findLeastExpensive(allSubs);

        cvBilling = view.findViewById(R.id.cvBilling);
        tvBillingDate = view.findViewById(R.id.tvBillingDate);
        tvBillingSub = view.findViewById(R.id.tvBillingSub);
        tvBillingDue = view.findViewById(R.id.tvBillingDue);

        setData();
        cvChart.setOnClickListener(v -> {
            bottomNavigationView.setSelectedItemId(R.id.action_subscription);
        });
        tvChartTotal.setText("Total of " + allSubs.size() + " subscriptions");

        cvExpensive.setOnClickListener(v -> {
            if((tvExpensiveSub.getText()).equals(mostExpensiveSub.getName())) {
                tvExpensiveTitle.setText("Least Expensive Subscription");
                tvExpensiveSub.setText(leastExpensiveSub.getName());
                tvExpensiveCost.setText(leastExpensiveSub.getPrice() + " per month");
            }
            else {
                tvExpensiveTitle.setText("Most Expensive Subscription");
                tvExpensiveSub.setText(mostExpensiveSub.getName());
                tvExpensiveCost.setText(mostExpensiveSub.getPrice() + " per month");
            }
        });

        cvBilling.setOnClickListener(v -> {
            bottomNavigationView.setSelectedItemId(R.id.action_finance);
        });
        Subscription nextBillingSub = findNextBillingDate(allSubs);
        tvBillingDate.setText(nextBillingSub.getDays());
        tvBillingSub.setText("for " + nextBillingSub.getName());
        tvBillingDue.setText("Amount Due: " + nextBillingSub.getPrice());
    }

    private void setData() {
        for(Subscription sub : allSubs) {
            // Set the data and color to the pie chart
            pieChart.addPieSlice(
                    new PieModel(
                            sub.getName(),
                            Float.parseFloat(sub.getPrice().substring(sub.getPrice().indexOf("$") + 1)),
                            sub.getColor()));
        }
        pieChart.startAnimation();
    }

    private Subscription findMostExpensive(List<Subscription> subs) {
        Subscription mostExpensiveSub = null;
        double mostExpensiveCost = 0.0;
        String priceStr = "";
        double price = 0;

        for(Subscription sub : subs) {
            priceStr = sub.getPrice();
            price = Double.parseDouble(priceStr.substring(priceStr.indexOf("$") + 1));
            if(price > mostExpensiveCost) {
                mostExpensiveCost = price;
                mostExpensiveSub = sub;
            }
        }

        return mostExpensiveSub;
    }

    private Subscription findLeastExpensive(List<Subscription> subs) {
        Subscription leastExpensiveSub = null;
        double leastExpensiveCost = Double.MAX_VALUE;
        String priceStr = "";
        double price = 0;

        for(Subscription sub : subs) {
            priceStr = sub.getPrice();
            price = Double.parseDouble(priceStr.substring(priceStr.indexOf("$") + 1));
            if(price < leastExpensiveCost) {
                leastExpensiveCost = price;
                leastExpensiveSub = sub;
            }
        }

        return leastExpensiveSub;
    }

    private Subscription findNextBillingDate(List<Subscription> subs) {
        Subscription nextBillingSub = null;
        int nearestBillingDate = 0;
        String daysStr = "";
        int days = 0;

        for(Subscription sub : subs) {
            daysStr = sub.getDays();
            days = Integer.parseInt(daysStr.substring(0,1));
            if(days > nearestBillingDate) {
                nearestBillingDate = days;
                nextBillingSub = sub;
            }
        }

        return nextBillingSub;
    }
}