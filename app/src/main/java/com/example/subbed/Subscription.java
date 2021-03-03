package com.example.subbed;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
public class Subscription implements Serializable {

    // subscription attributes
    String name;
    String type;
    double price;
    int color;

    int next_billing_year;
    int next_billing_month;
    int next_billing_day;

    // An empty constructor
    public Subscription() {
    }

    // constructor with parameters for testing purpose!
    public Subscription(String name, String type, double price, int color) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.color = color;

        // hardcoded for testing purpose!
        next_billing_year = 2021;
        next_billing_month = 4;
        next_billing_day = 5;
    }

    public String getName() { return name; }
    public String getType() {
        return type;
    }
    public double getPrice() {
        return price;
    }
    public int getColor() { return color; }
    public int getNext_billing_day() {
        return next_billing_day;
    }
    public int getNext_billing_month() {
        return next_billing_month;
    }
    public int getNext_billing_year() {
        return next_billing_year;
    }

    /**
     * Get a String representation of the next billing date
     * @return
     */
    public String getNextBillingDate() {
        return next_billing_month + "/" + next_billing_day + "/" + next_billing_year;
    }

    /**
     * Compute the days remaining before the next billing date
     * @return int
     */
    public int computeRemainingDays() {
        DateTime today = new DateTime();
        DateTime next_billing_date = today.withYear(next_billing_year)
                .withMonthOfYear(next_billing_month).withDayOfMonth(next_billing_day);
        return Days.daysBetween(today, next_billing_date).getDays();
    }

    /**
     * Set the next billing date
     * @param year - next billing year
     * @param month - next billing month
     * @param day - next billing day of month
     */
    public void setNextBillingDate(int year, int month, int day) {
        next_billing_year = year;
        next_billing_month = month;
        next_billing_day = day;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setColor(int color) { this.color = color; }
}
