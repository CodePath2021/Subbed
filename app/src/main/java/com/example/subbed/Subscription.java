package com.example.subbed;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.Days;

@ParseClassName("Subscription")
public class Subscription extends ParseObject {

    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PRICE = "price";
    public static final String KEY_COLOR = "color";
    public static final String KEY_YEAR = "nextBillingYear";
    public static final String KEY_MONTH = "nextBillingMonth";
    public static final String KEY_DAY = "nextBillingDay";
    public static final String KEY_USER = "user";
    public static final String KEY_ICON = "icon";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public double getPrice() {
        Number price = getNumber(KEY_PRICE);
        if(price instanceof Double)
            return (double)price;
        else
            return (double)price.intValue();
    }

    public void setPrice(double price) {
        put(KEY_PRICE, price);
    }

    public String getColor() { return getString(KEY_COLOR); }

    public void setColor(String color) {
        put(KEY_COLOR, color);
    }

    public int getNextBillingYear() {
        return (int)getNumber(KEY_YEAR);
    }

    public void setNextBillingYear(int year) {
        put(KEY_YEAR, year);
    }

    public int getNextBillingMonth() {
        return (int)getNumber(KEY_MONTH);
    }

    public void setNextBillingMonth(int month) {
        put(KEY_MONTH, month);
    }

    public int getNextBillingDay() {
        return (int)getNumber(KEY_DAY);
    }

    public void setNextBillingDay(int day) {
        put(KEY_DAY, day);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getIconId() {
        return (int)getNumber(KEY_ICON);
    }

    public void setIconId(int icon) {
        put(KEY_ICON, icon);
    }

    /**
     * Get a String representation of the next billing date
     * @return
     */
    public String getNextBillingDate() {
        return getNextBillingMonth() + "/" + getNextBillingDay() + "/" + getNextBillingYear();
    }

    /**
     * Set the next billing date
     * @param year - next billing year
     * @param month - next billing month
     * @param day - next billing day of month
     */
    public void setNextBillingDate(int year, int month, int day) {
        setNextBillingYear(year);
        setNextBillingMonth(month);
        setNextBillingDay(day);
    }

    /**
     * Compute the days remaining before the next billing date
     * @return int
     */
    public int computeRemainingDays() {
        DateTime today = new DateTime();
        DateTime next_billing_date = today.withYear(getNextBillingYear())
                .withMonthOfYear(getNextBillingMonth()).withDayOfMonth(getNextBillingDay());

        int result = Days.daysBetween(today, next_billing_date).getDays();

        // if remaining days is 0, will set the new next billing dates and recompute remaining days
        if (result == 0) {

            // monthly type
            if (getType().equals("Monthly")) {
                // if the current month is December, need to increment the year, set the month to be January
                if (getNextBillingMonth() == 12) {
                    setNextBillingYear(getNextBillingYear() + 1);
                    setNextBillingMonth(1);
                }
                // in other cases, simply increment the month by 1
                // Don't need to dive much into the calculation details cuz the benefit is pretty marginal
                else {
                    setNextBillingMonth(getNextBillingMonth() + 1);
                    // compute remaining days again with the new next billing date
                    next_billing_date = today.withYear(getNextBillingYear())
                            .withMonthOfYear(getNextBillingMonth()).withDayOfMonth(getNextBillingDay());
                    result = Days.daysBetween(today, next_billing_date).getDays();
                }
            } else if (getType().equals("Yearly")) {
                setNextBillingYear(getNextBillingYear() + 1);
                // compute remaining days again with the new next billing date
                next_billing_date = today.withYear(getNextBillingYear())
                        .withMonthOfYear(getNextBillingMonth()).withDayOfMonth(getNextBillingDay());
                result = Days.daysBetween(today, next_billing_date).getDays();
            }
        }
        return result;
    }
}
