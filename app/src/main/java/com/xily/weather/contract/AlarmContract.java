package com.xily.weather.contract;

import com.xily.weather.base.IBasePresenter;
import com.xily.weather.base.IBaseView;
import com.xily.weather.model.bean.WeatherBean;

import java.util.List;

public interface AlarmContract {
    interface View extends IBaseView {
        void show(List<WeatherBean.ValueBean.AlarmsBean> alarmsBeanList);
    }

    interface Presenter extends IBasePresenter<View> {
        void getAlarms(int cityId);
    }
}
