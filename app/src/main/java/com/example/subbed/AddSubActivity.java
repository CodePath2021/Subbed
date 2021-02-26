package com.example.subbed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.parceler.Parcels;

import java.util.List;

public class AddSubActivity extends AppCompatActivity {

    private static final String TAG = "AddSubActivity";

    EditText etSubscription;
    EditText etCost;
    EditText etDate;

    int next_billing_year;
    int next_billing_month;
    int next_billing_day;

    int date_difference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);

        etSubscription = findViewById(R.id.etSubscription);
        etCost = findViewById(R.id.etCost);
        etDate = findViewById(R.id.etDate);
    }

    public void addNewSub(View view) {
        // TODO: if the input date is not valid, warn the user.

        String date = etDate.getText().toString();
        next_billing_month = Integer.parseInt(date.substring(0, date.indexOf('/')));
        next_billing_day =  Integer.parseInt(date.substring(date.indexOf('/') + 1, date.lastIndexOf('/')));
        next_billing_year = Integer.parseInt(date.substring(date.lastIndexOf('/') + 1));

        DateTime today = new DateTime();
        DateTime next_billing_date = today.withYear(next_billing_year).withMonthOfYear(next_billing_month).withDayOfMonth(next_billing_day);
        date_difference = Days.daysBetween(today, next_billing_date).getDays();

        // TODO: how to update the date_difference when time passes?

        Subscription new_sub = new Subscription();
        new_sub.setName(etSubscription.getText().toString());
        new_sub.setPrice(etCost.getText().toString());
        new_sub.setDays(String.valueOf(date_difference));

        Intent replyIntent = new Intent();
        replyIntent.putExtra("A new subscription", Parcels.wrap(new_sub));
        setResult(RESULT_OK, replyIntent);
        finish();
    }
}