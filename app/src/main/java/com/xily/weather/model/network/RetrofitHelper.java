package com.xily.weather.model.network;

import com.xily.weather.model.bean.CitiesBean;
import com.xily.weather.model.bean.CountiesBean;
import com.xily.weather.model.bean.ProvincesBean;
import com.xily.weather.model.bean.SearchBean;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.bean.WeatherBean;
import com.xily.weather.model.network.api.WeatherApi;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class RetrofitHelper implements HttpHelper {

    private WeatherApi weatherApi;

    @Inject
    public RetrofitHelper(WeatherApi weatherApi) {
        this.weatherApi = weatherApi;
    }

    @Override
    public Observable<VersionBean> checkVersion() {
        return weatherApi.checkVersion();
    }

    @Override
    public Observable<WeatherBean> getWeather(String cityId) {
        return weatherApi.getWeather(cityId);
    }

    @Override
    public Observable<SearchBean> search(String location) {
        return weatherApi.search(location);
    }

    @Override
    public Observable<List<ProvincesBean>> getProvinces() {
        return weatherApi.getProvinces();
    }

    @Override
    public Observable<List<CitiesBean>> getCities(String province) {
        return weatherApi.getCities(province);
    }

    @Override
    public Observable<List<CountiesBean>> getCounties(String province, String city) {
        return weatherApi.getCounties(province, city);
    }
}
