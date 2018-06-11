package com.xily.weather.contract;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.base.BaseView;
import com.xily.weather.model.bean.WeatherBean;

import java.util.List;

public interface AlarmContract {
    interface View extends BaseView {
        void show(List<WeatherBean.ValueBean.AlarmsBean> alarmsBeanList);
    }

    interface Presenter extends BasePresenter<View> {
        void getAlarms(int cityId);
    }
}
