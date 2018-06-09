package com.xily.weather.model.network.api;

import com.xily.weather.model.bean.CitiesBean;
import com.xily.weather.model.bean.CountiesBean;
import com.xily.weather.model.bean.ProvincesBean;
import com.xily.weather.model.bean.SearchBean;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.bean.WeatherBean;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface WeatherApi {
    @GET(ApiConfig.myApiUrl + "checkVersion2")
    Observable<VersionBean> checkVersion();

    @GET(ApiConfig.meiZuApiUrl + "listWeather")
    Observable<WeatherBean> getWeather(@Query("cityIds") String cityId);

    @GET(ApiConfig.heWeatherApiUrl + "find?key=5ddec80c2a44479083eccb0f5dcfba5b")
    Observable<SearchBean> search(@Query("location") String location);

    @GET(ApiConfig.guoLinApiUrl + "china/")
    Observable<List<ProvincesBean>> getProvinces();

    @GET(ApiConfig.guoLinApiUrl + "china/{province}")
    Observable<List<CitiesBean>> getCities(@Path("province") String province);

    @GET(ApiConfig.guoLinApiUrl + "china/{province}/{city}")
    Observable<List<CountiesBean>> getCounties(@Path("province") String province, @Path("city") String city);
}
