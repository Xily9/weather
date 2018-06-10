package com.xily.weather.di.component;

import android.app.Activity;

import com.xily.weather.di.module.FragmentModule;
import com.xily.weather.di.scope.FragmentScope;
import com.xily.weather.ui.fragment.HomePagerFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {
    Activity getActivity();

    void inject(HomePagerFragment homePagerFragment);
}
