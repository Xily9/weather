package com.xily.weather.contract;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.base.BaseView;
import com.xily.weather.model.bean.CityListBean;

import java.util.List;

public interface AddCityContract {
    interface View extends BaseView {
        void showProgressDialog();

        void closeProgressDialog();

        void show(List<String> dataList, List<Integer> codeList);
    }

    interface Presenter extends BasePresenter<View> {
        List<CityListBean> getCityByWeatherId(int id);

        void search(String str);

        void queryProvinces();

        void queryCities(int provinceId);

        void queryCounties(int provinceId, int cityId);

        void addCity(int WeatherId, String countyName);
    }
}
