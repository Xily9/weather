package com.xily.weather.network.api;

import com.xily.weather.entity.SearchInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface HeWeatherApiService {
    @GET("find")
    Observable<SearchInfo> search(@Query("location") String location, @Query("key") String key);
}
