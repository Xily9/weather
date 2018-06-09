package com.xily.weather.di.component;

import com.xily.weather.utils.PreferenceUtil;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component
public interface AppComponent {
    PreferenceUtil preferenceUtil();
}
