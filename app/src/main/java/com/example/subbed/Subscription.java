package com.example.subbed;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
public class Subscription implements Serializable {

    private String name;
    private String price;
    private String days;

    public Subscription() {
        name = "Spotify";
        price = "$9.99";
        days = "5 Days";
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDays() {
        return days;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = '$' + price;
    }

    public void setDays(String days) {
        this.days = days + " Days";
    }

}
