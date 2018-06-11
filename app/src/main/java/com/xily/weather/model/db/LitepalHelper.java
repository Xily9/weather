package com.xily.weather.model.db;

import com.xily.weather.model.bean.AlarmsBean;
import com.xily.weather.model.bean.CityBean;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.CountyBean;
import com.xily.weather.model.bean.ProvinceBean;

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

    @Override
    public List<CityListBean> getCityByWeatherId(int id) {
        return DataSupport.where("weatherid=?", String.valueOf(id)).find(CityListBean.class);
    }

    @Override
    public List<ProvinceBean> getProvince() {
        return DataSupport.findAll(ProvinceBean.class);
    }

    @Override
    public List<CityBean> getCity(String provinceId) {
        return DataSupport.where("provinceid=?", provinceId).find(CityBean.class);
    }

    @Override
    public List<CountyBean> getCounty(String cityId) {
        return DataSupport.where("cityid=?", cityId).find(CountyBean.class);
    }

    @Override
    public void deleteCity(int id) {
        DataSupport.delete(CityListBean.class, id);
    }

    @Override
    public List<AlarmsBean> getAlarmsById(String id) {
        return DataSupport.where("notificationid=?", id).find(AlarmsBean.class);
    }

}
