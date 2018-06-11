package com.xily.weather.presenter;

import com.google.gson.Gson;
import com.xily.weather.base.RxBasePresenter;
import com.xily.weather.contract.AlarmContract;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.WeatherBean;

import javax.inject.Inject;

public class AlarmPresenter extends RxBasePresenter<AlarmContract.View> implements AlarmContract.Presenter {
    private DataManager mDataManager;

    @Inject
    public AlarmPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void getAlarms(int cityId) {
        CityListBean cityList = mDataManager.getCityById(cityId);
        if (cityList != null) {
            WeatherBean weatherBean = new Gson().fromJson(cityList.getWeatherData(), WeatherBean.class);
            mView.show(weatherBean.getValue().get(0).getAlarms());
        }
    }
}
