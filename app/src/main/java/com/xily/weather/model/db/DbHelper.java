package com.xily.weather.model.db;

import com.xily.weather.model.bean.CityListBean;

import java.util.List;

public interface DbHelper {
    List<CityListBean> getCityList();

    CityListBean getCityById(int id);
}
