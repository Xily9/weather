package com.xily.weather.network.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface BaiduLocationApiService {
    @GET("geocoder/v2/?callback=renderReverse&output=json&coordtype=wgs84ll&pois=1&ak=x8Vszvqzii0UyemFiYuWzsucrPOmfUg3")
    Observable<ResponseBody> getAddress(@Query("location") String location);
}
