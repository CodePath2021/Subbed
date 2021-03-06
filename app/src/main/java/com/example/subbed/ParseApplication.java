package com.example.subbed;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Subscription.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("qWlw9UHeYBJ9Nv1XDXVNKwPXLkAUlV7O0jadVXfF")
                .clientKey("KRKyr4RJDKL0VYSG0OPK8U7tmmaAyzymALYbg9an")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
