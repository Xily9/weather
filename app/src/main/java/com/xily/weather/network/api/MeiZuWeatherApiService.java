package com.xily.weather.network.api;

import com.xily.weather.entity.WeatherInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface MeiZuWeatherApiService {
    @GET("listWeather")
    Observable<WeatherInfo> getWeather(@Query("cityIds") String cityId);
}
