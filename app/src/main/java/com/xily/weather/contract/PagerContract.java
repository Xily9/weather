package com.xily.weather.contract;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.base.BaseView;
import com.xily.weather.model.bean.WeatherBean;

public interface PagerContract {
    interface View extends BaseView {
        void showWeather(WeatherBean weatherBean);

        void setRefreshing(boolean isRefreshing);

        void setUpdateTime(String updateTime);

        void setTitle(String title);

        void sendBroadcast();
    }

    interface Presenter extends BasePresenter<View> {
        void getWeather(boolean isRefreshing);

        void getCityInfo(int position);

        void getSetTitleUpdateTime();

        int getCityId();
    }
}
