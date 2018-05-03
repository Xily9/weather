package com.xily.weather.network.api;

import com.xily.weather.entity.SearchInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface HeWeatherApiService {
    @GET("find?key=5ddec80c2a44479083eccb0f5dcfba5b")
    Observable<SearchInfo> search(@Query("location") String location);
}
