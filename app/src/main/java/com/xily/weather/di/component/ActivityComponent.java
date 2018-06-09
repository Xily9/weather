package com.xily.weather.di.component;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xily.weather.di.module.ActivityModule;
import com.xily.weather.di.scope.ActivityScope;
import com.xily.weather.ui.activity.MainActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    RxAppCompatActivity getActivity();

    void inject(MainActivity mainActivity);
}
