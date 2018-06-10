package com.xily.weather.di.module;

import com.xily.weather.app.App;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.db.DbHelper;
import com.xily.weather.model.db.LitepalHelper;
import com.xily.weather.model.network.HttpHelper;
import com.xily.weather.model.network.RetrofitHelper;
import com.xily.weather.model.prefs.ImplPreferencesHelper;
import com.xily.weather.model.prefs.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final App application;

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    App provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    HttpHelper provideHttpHelper(RetrofitHelper retrofitHelper) {
        return retrofitHelper;
    }

    @Provides
    @Singleton
    DbHelper provideDBHelper(LitepalHelper litepalHelper) {
        return litepalHelper;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(ImplPreferencesHelper implPreferencesHelper) {
        return implPreferencesHelper;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(PreferencesHelper preferencesHelper, HttpHelper httpHelper, DbHelper DBHelper) {
        return new DataManager(preferencesHelper, httpHelper, DBHelper);
    }
}
