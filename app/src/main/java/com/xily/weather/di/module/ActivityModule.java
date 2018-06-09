package com.xily.weather.di.module;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xily.weather.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private RxAppCompatActivity mActivity;

    public ActivityModule(RxAppCompatActivity activity) {
        this.mActivity = activity;
    }

    @ActivityScope
    @Provides
    RxAppCompatActivity provideActivity() {
        return mActivity;
    }
}
