package com.xily.weather.di.component;

import com.xily.weather.app.App;
import com.xily.weather.di.module.AppModule;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.db.LitepalHelper;
import com.xily.weather.model.network.RetrofitHelper;
import com.xily.weather.model.prefs.ImplPreferencesHelper;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    App getContext();

    DataManager getDataManager();

    RetrofitHelper getRetrofitHelper();

    ImplPreferencesHelper getImplPreferenceHelper();

    LitepalHelper getLitepalHelper();
}
