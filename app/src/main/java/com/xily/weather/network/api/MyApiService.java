package com.xily.weather.network.api;


import com.xily.weather.entity.versionInfo;

import retrofit2.http.GET;
import rx.Observable;

public interface MyApiService {
    @GET("checkVersion2")
    Observable<versionInfo> checkVersion();
}
