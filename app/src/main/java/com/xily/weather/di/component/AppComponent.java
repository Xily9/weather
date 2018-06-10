package com.xily.weather.di.component;

import com.xily.weather.app.App;
import com.xily.weather.di.module.AppModule;
import com.xily.weather.utils.PreferenceUtil;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    App getContext();
    PreferenceUtil preferenceUtil();
}
