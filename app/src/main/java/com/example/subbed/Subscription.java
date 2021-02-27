package com.example.subbed;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
public class Subscription implements Serializable {

    private String name;
    private String price;
    private String days;
    private int color;

    public Subscription() {
        name = "Spotify";
        price = "$9.99";
        days = "5 days";
        color = Color.parseColor("#1DB954");
    }

    public Subscription(String name, String price, String days, int color) {
        this.name = name;
        this.price = price;
        this.days = days;
        this.color = color;
    }

    public String getName() { return name; }

    public String getPrice() {
        return price;
    }

    public String getDays() {
        return days;
    }

    public int getColor() { return color; }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = '$' + price;
    }

    public void setDays(String days) { this.days = days + " Days"; }

    public void setColor(int color) { this.color = color; }
}
