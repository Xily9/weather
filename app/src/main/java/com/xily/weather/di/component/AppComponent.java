package com.xily.weather.di.component;

import com.xily.weather.app.App;
import com.xily.weather.di.module.AppModule;
import com.xily.weather.di.module.HttpModule;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.network.OkHttpHelper;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent {
    App getContext();

    DataManager getDataManager();

    OkHttpHelper getOkHttpHelper();
}
