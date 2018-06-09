package com.xily.weather.app;

import android.app.Application;

import com.xily.weather.di.component.AppComponent;
import com.xily.weather.di.component.DaggerAppComponent;

import org.litepal.LitePal;

public class App extends Application {
    public static App mInstance;
    public static AppComponent appComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        mInstance = this;
    }

    public static App getInstance() {
        return mInstance;
    }

    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.create();
        }
        return appComponent;
    }
}
