package com.xily.weather.contract;

import com.xily.weather.base.IBasePresenter;
import com.xily.weather.base.IBaseView;
import com.xily.weather.model.bean.WeatherBean;

public interface PagerContract {
    interface View extends IBaseView {
        void showWeather(WeatherBean weatherBean);

        void setRefreshing(boolean isRefreshing);

        void setUpdateTime(String updateTime);

        void setTitle(String title);

        void sendBroadcast();
    }

    interface Presenter extends IBasePresenter<View> {
        void getWeather(boolean isRefreshing);

        void getCityInfo(int position);

        void getSetTitleUpdateTime();

        int getCityId();
    }
}
