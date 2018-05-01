package com.xily.weather;

import android.app.Application;

import org.litepal.LitePal;

public class MyApplication extends Application {
    public static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        mInstance = this;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
