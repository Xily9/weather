package com.xily.weather.presenter;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.contract.CityContract;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.bean.CityListBean;

import java.util.List;

import javax.inject.Inject;

public class CityPresenter extends BasePresenter<CityContract.View> implements CityContract.Presenter {
    private DataManager mDataManager;

    @Inject
    public CityPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void deleteCity(int id) {
        mDataManager.deleteCity(id);
    }

    @Override
    public int getNotificationId() {
        return mDataManager.getNotificationId();
    }

    @Override
    public void setNotificationId(int id) {
        mDataManager.setNotificationId(id);
    }

    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        mDataManager.setAutoUpdate(autoUpdate);
    }

    @Override
    public void setNotification(boolean notification) {
        mDataManager.setNotification(notification);
    }

    @Override
    public CityListBean addCity(CityListBean city) {
        CityListBean newCity = new CityListBean();
        newCity.setCityName(city.getCityName());
        newCity.setUpdateTime(city.getUpdateTime());
        newCity.setUpdateTimeStr(city.getUpdateTimeStr());
        newCity.setWeatherData(city.getWeatherData());
        newCity.setWeatherId(city.getWeatherId());
        newCity.save();
        return newCity;
    }

    @Override
    public List<CityListBean> getCityList() {
        return mDataManager.getCityList();
    }
}
