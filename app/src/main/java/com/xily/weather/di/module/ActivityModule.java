package com.xily.weather.di.module;

import android.app.Activity;

import com.xily.weather.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @ActivityScope
    @Provides
    Activity provideActivity() {
        return mActivity;
    }
}
