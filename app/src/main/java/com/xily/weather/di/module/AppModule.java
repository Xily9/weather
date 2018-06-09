package com.xily.weather.di.module;

import com.xily.weather.utils.PreferenceUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Singleton
    PreferenceUtil providePreference(PreferenceUtil preferenceUtil) {
        return preferenceUtil;
    }
}
