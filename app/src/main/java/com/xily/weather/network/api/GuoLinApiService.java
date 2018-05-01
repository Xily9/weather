package com.xily.weather.network.api;

import com.xily.weather.entity.CitiesInfo;
import com.xily.weather.entity.CountiesInfo;
import com.xily.weather.entity.ProvincesInfo;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GuoLinApiService {
    @GET("china/")
    Observable<List<ProvincesInfo>> getProvinces();

    @GET("china/{province}")
    Observable<List<CitiesInfo>> getCities(@Path("province") String province);

    @GET("china/{province}/{city}")
    Observable<List<CountiesInfo>> getCounties(@Path("province") String province, @Path("city") String city);
}
