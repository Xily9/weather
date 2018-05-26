package com.xily.weather.network.api;

import com.xily.weather.entity.CitiesInfo;
import com.xily.weather.entity.CountiesInfo;
import com.xily.weather.entity.ProvincesInfo;
import com.xily.weather.entity.SearchInfo;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.entity.versionInfo;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface WeatherApi {
    @GET(ApiConfig.myApiUrl + "checkVersion2")
    Observable<versionInfo> checkVersion();

    @GET(ApiConfig.meiZuApiUrl + "listWeather")
    Observable<WeatherInfo> getWeather(@Query("cityIds") String cityId);

    @GET(ApiConfig.heWeatherApiUrl + "find?key=5ddec80c2a44479083eccb0f5dcfba5b")
    Observable<SearchInfo> search(@Query("location") String location);

    @GET(ApiConfig.guoLinApiUrl + "china/")
    Observable<List<ProvincesInfo>> getProvinces();

    @GET(ApiConfig.guoLinApiUrl + "china/{province}")
    Observable<List<CitiesInfo>> getCities(@Path("province") String province);

    @GET(ApiConfig.guoLinApiUrl + "china/{province}/{city}")
    Observable<List<CountiesInfo>> getCounties(@Path("province") String province, @Path("city") String city);
}
