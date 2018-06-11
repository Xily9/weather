package com.xily.weather.contract;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.base.BaseView;
import com.xily.weather.model.bean.CityListBean;

import java.util.List;

public interface CityContract {
    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter<View> {
        void deleteCity(int id);

        int getNotificationId();

        void setNotificationId(int id);

        void setAutoUpdate(boolean autoUpdate);

        void setNotification(boolean notification);

        CityListBean addCity(CityListBean cityListBean);

        List<CityListBean> getCityList();
    }
}
