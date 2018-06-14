package com.xily.weather.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xily.weather.base.BasePresenter;
import com.xily.weather.contract.PagerContract;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.WeatherBean;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PagerPresenter extends BasePresenter<PagerContract.View> implements PagerContract.Presenter {

    private CityListBean cityList;
    private DataManager mDataManager;
    @Inject
    public PagerPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @SuppressLint("CheckResult")
    @Override
    public void getWeather(final boolean isRefreshing) {
        if (cityList != null) {
            Observable<WeatherBean> offline = Observable.just("")
                    .map(o -> {
                        String data = cityList.getWeatherData();
                        if (isRefreshing || System.currentTimeMillis() - cityList.getUpdateTime() > 1000 * 60 * 60 || TextUtils.isEmpty(data)) {
                            return new WeatherBean();
                        } else {
                            return new Gson().fromJson(data, WeatherBean.class);
                        }
                    });
            Observable<WeatherBean> online = mDataManager
                    .getWeather(String.valueOf(cityList.getWeatherId()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> mView.setRefreshing(true))
                    .doFinally(() -> mView.setRefreshing(false))
                    .doOnNext(weatherInfo -> {
                        CityListBean cityListUpdate = new CityListBean();
                        cityListUpdate.setWeatherData(new Gson().toJson(weatherInfo));
                        cityListUpdate.setUpdateTime(System.currentTimeMillis());
                        cityListUpdate.setUpdateTimeStr(weatherInfo.getValue().get(0).getRealtime().getTime().substring(11, 16));
                        cityListUpdate.update(cityList.getId());
                        mView.setUpdateTime(cityListUpdate.getUpdateTimeStr() + "更新");
                        cityList = mDataManager.getCityById(cityList.getId());
                        mView.sendBroadcast();
                    });
            Observable.concat(offline, online)
                    .filter(weatherInfo -> weatherInfo.getCode() != null)
                    .firstElement()
                    .compose(mView.bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(weatherBean -> mView.showWeather(weatherBean), throwable -> {
                        throwable.printStackTrace();
                        mView.showErrorMsg(throwable.getMessage());
                    });
        }
    }

    @Override
    public void getCityInfo(int position) {
        List<CityListBean> cityLists = mDataManager.getCityList();
        if (cityLists.size() > position) {
            cityList = cityLists.get(position);
        }
    }

    @Override
    public void getSetTitleUpdateTime() {
        mView.setTitle(cityList != null ? cityList.getCityName() : "");
        mView.setUpdateTime(cityList != null ? cityList.getUpdateTimeStr() + "更新" : "");
    }

    @Override
    public int getCityId() {
        return cityList != null ? cityList.getId() : -1;
    }
}
