package com.xily.weather.model.db;

import com.xily.weather.model.bean.AlarmsBean;
import com.xily.weather.model.bean.CityBean;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.CountyBean;
import com.xily.weather.model.bean.ProvinceBean;

import java.util.List;

public interface DbHelper {
    List<CityListBean> getCityList();

    CityListBean getCityById(int id);

    List<CityListBean> getCityByWeatherId(int id);

    List<ProvinceBean> getProvince();

    List<CityBean> getCity(String provinceId);

    List<CountyBean> getCounty(String cityId);

    void deleteCity(int id);

    List<AlarmsBean> getAlarmsById(String id);
}
