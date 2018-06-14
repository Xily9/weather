package com.xily.weather.presenter;

import android.annotation.SuppressLint;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.contract.AddCityContract;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.bean.CitiesBean;
import com.xily.weather.model.bean.CityBean;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.CountiesBean;
import com.xily.weather.model.bean.CountyBean;
import com.xily.weather.model.bean.ProvinceBean;
import com.xily.weather.model.bean.ProvincesBean;
import com.xily.weather.model.bean.SearchBean;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddCityPresenter extends BasePresenter<AddCityContract.View> implements AddCityContract.Presenter {

    private DataManager mDataManager;

    @Inject
    public AddCityPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public List<CityListBean> getCityByWeatherId(int id) {
        return null;
    }

    @SuppressLint("CheckResult")
    @Override
    public void search(String str) {
        List<String> dataList = new ArrayList<>();
        List<Integer> codeList = new ArrayList<>();
        mDataManager.search(str)
                .compose(mView.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showProgressDialog())
                .doFinally(() -> mView.closeProgressDialog())
                .subscribe(searchBean -> {
                    SearchBean.HeWeather6Bean heWeather6Bean = searchBean.getHeWeather6().get(0);
                    if (heWeather6Bean.getStatus().equals("ok")) {
                        dataList.clear();
                        codeList.clear();
                        for (SearchBean.HeWeather6Bean.BasicBean basicBean : heWeather6Bean.getBasic()) {
                            dataList.add(basicBean.getLocation());
                            codeList.add(Integer.valueOf(basicBean.getCid().substring(2)));
                        }
                        mView.show(dataList, codeList);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    mView.showErrorMsg(throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void queryProvinces() {
        List<String> dataList = new ArrayList<>();
        List<Integer> codeList = new ArrayList<>();
        List<ProvinceBean> provinceList = mDataManager.getProvince();
        if (provinceList.isEmpty()) {
            mDataManager.getProvinces()
                    .compose(mView.bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> mView.showProgressDialog())
                    .doFinally(() -> mView.closeProgressDialog())
                    .subscribe(provincesInfoList -> {
                        for (ProvincesBean provincesBean : provincesInfoList) {
                            ProvinceBean province = new ProvinceBean();
                            province.setProvinceCode(provincesBean.getId());
                            province.setProvinceName(provincesBean.getName());
                            province.save();
                            dataList.add(provincesBean.getName());
                            codeList.add(provincesBean.getId());
                        }
                        mView.show(dataList, codeList);
                    }, throwable -> {
                        throwable.printStackTrace();
                        mView.showErrorMsg(throwable.getMessage());
                    });
        } else {
            for (ProvinceBean province : provinceList) {
                dataList.add(province.getProvinceName());
                codeList.add(province.getProvinceCode());
            }
            mView.show(dataList, codeList);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void queryCities(int provinceId) {
        List<String> dataList = new ArrayList<>();
        List<Integer> codeList = new ArrayList<>();
        String provinceIdStr = String.valueOf(provinceId);
        List<CityBean> cityList = mDataManager.getCity(provinceIdStr);
        if (cityList.isEmpty()) {
            mDataManager.getCities(provinceIdStr)
                    .compose(mView.bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> mView.showProgressDialog())
                    .doFinally(() -> mView.closeProgressDialog())
                    .subscribe(citiesInfoList -> {
                        for (CitiesBean citiesBean : citiesInfoList) {
                            CityBean city = new CityBean();
                            city.setCityCode(citiesBean.getId());
                            city.setCityName(citiesBean.getName());
                            city.setProvinceId(provinceId);
                            city.save();
                            dataList.add(citiesBean.getName());
                            codeList.add(citiesBean.getId());
                        }
                        mView.show(dataList, codeList);
                    }, throwable -> {
                        throwable.printStackTrace();
                        mView.showErrorMsg(throwable.getMessage());
                    });
        } else {
            for (CityBean city : cityList) {
                dataList.add(city.getCityName());
                codeList.add(city.getCityCode());
            }
            mView.show(dataList, codeList);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void queryCounties(int provinceId, int cityId) {
        List<String> dataList = new ArrayList<>();
        List<Integer> codeList = new ArrayList<>();
        String cityIdStr = String.valueOf(cityId);
        List<CountyBean> countyList = mDataManager.getCounty(cityIdStr);
        if (countyList.isEmpty()) {
            mDataManager.getCounties(String.valueOf(provinceId), cityIdStr)
                    .compose(mView.bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> mView.showProgressDialog())
                    .doFinally(() -> mView.closeProgressDialog())
                    .subscribe(countiesInfoList -> {
                        for (CountiesBean countiesBean : countiesInfoList) {
                            CountyBean county = new CountyBean();
                            int weatherId = Integer.valueOf(countiesBean.getWeather_id().substring(2));
                            county.setWeatherId(weatherId);
                            county.setCountyName(countiesBean.getName());
                            county.setCityId(cityId);
                            county.save();
                            dataList.add(countiesBean.getName());
                            codeList.add(weatherId);
                        }
                        mView.show(dataList, codeList);
                    }, throwable -> {
                        throwable.printStackTrace();
                        mView.showErrorMsg(throwable.getMessage());
                    });
        } else {
            for (CountyBean county : countyList) {
                dataList.add(county.getCountyName());
                codeList.add(county.getWeatherId());
            }
            mView.show(dataList, codeList);
        }
    }

    @Override
    public void addCity(int WeatherId, String countyName) {
        CityListBean cityList = new CityListBean();
        cityList.setCityName(countyName);
        cityList.setWeatherId(WeatherId);
        cityList.save();
    }
}
