package com.xily.weather.di.component;

import com.xily.weather.app.App;
import com.xily.weather.di.module.AppModule;
import com.xily.weather.di.module.HttpModule;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.db.LitepalHelper;
import com.xily.weather.model.network.OkHttpHelper;
import com.xily.weather.model.network.RetrofitHelper;
import com.xily.weather.model.prefs.ImplPreferencesHelper;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent {
    App getContext();

    DataManager getDataManager();

    RetrofitHelper getRetrofitHelper();

    ImplPreferencesHelper getImplPreferenceHelper();

    LitepalHelper getLitepalHelper();

    OkHttpHelper getOkHttpHelper();

    OkHttpClient getOkHttpClient();
}
