package com.xily.weather.contract;

import com.xily.weather.base.IBasePresenter;
import com.xily.weather.base.IBaseView;
import com.xily.weather.model.bean.CityListBean;

import java.util.List;

public interface AddCityContract {
    interface View extends IBaseView {
        void showProgressDialog();

        void closeProgressDialog();

        void show(List<String> dataList, List<Integer> codeList);
    }

    interface Presenter extends IBasePresenter<View> {
        List<CityListBean> getCityByWeatherId(int id);

        void search(String str);

        void queryProvinces();

        void queryCities(int provinceId);

        void queryCounties(int provinceId, int cityId);

        void addCity(int WeatherId, String countyName);
    }
}
