package com.xily.weather.di.component;

import com.xily.weather.di.scope.ServiceScope;
import com.xily.weather.service.WeatherService;

import dagger.Component;

@ServiceScope
@Component(dependencies = AppComponent.class)
public interface ServiceComponent {
    void inject(WeatherService weatherService);
}
