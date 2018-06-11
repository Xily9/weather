package com.xily.weather.di.component;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xily.weather.di.module.ActivityModule;
import com.xily.weather.di.scope.ActivityScope;
import com.xily.weather.ui.activity.AddCityActivity;
import com.xily.weather.ui.activity.AlarmActivity;
import com.xily.weather.ui.activity.CityActivity;
import com.xily.weather.ui.activity.MainActivity;
import com.xily.weather.ui.activity.SettingsActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    RxAppCompatActivity getActivity();

    void inject(MainActivity mainActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(AddCityActivity addCityActivity);

    void inject(CityActivity cityActivity);

    void inject(AlarmActivity alarmActivity);
}
