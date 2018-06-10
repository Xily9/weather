package com.xily.weather.model.network;

import com.xily.weather.model.bean.CitiesBean;
import com.xily.weather.model.bean.CountiesBean;
import com.xily.weather.model.bean.ProvincesBean;
import com.xily.weather.model.bean.SearchBean;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.bean.WeatherBean;

import java.util.List;

import rx.Observable;

public interface HttpHelper {
    Observable<VersionBean> checkVersion();

    Observable<WeatherBean> getWeather(String cityId);

    Observable<SearchBean> search(String location);

    Observable<List<ProvincesBean>> getProvinces();

    Observable<List<CitiesBean>> getCities(String province);

    Observable<List<CountiesBean>> getCounties(String province, String city);
}
