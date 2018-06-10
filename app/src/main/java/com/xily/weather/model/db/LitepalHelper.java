package com.xily.weather.model.db;

import com.xily.weather.model.bean.CityListBean;

import org.litepal.crud.DataSupport;

import java.util.List;

import javax.inject.Inject;

public class LitepalHelper implements DbHelper {

    @Inject
    public LitepalHelper() {
    }

    @Override
    public List<CityListBean> getCityList() {
        return DataSupport.findAll(CityListBean.class);
    }

    @Override
    public CityListBean getCityById(int id) {
        return DataSupport.find(CityListBean.class, id);
    }
}
