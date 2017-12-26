package com.penghaonan.painting;

import android.app.Application;

import com.penghaonan.appframework.AppDelegate;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppDelegate.init(this);
        AppDelegate.setDebug(BuildConfig.DEBUG);
    }
}
