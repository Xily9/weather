package com.xily.weather.contract;

import com.xily.weather.base.IBasePresenter;
import com.xily.weather.base.IBaseView;
import com.xily.weather.model.bean.CityListBean;

import java.util.List;

public interface SettingsContract {
    interface View extends IBaseView {

    }

    interface Presenter extends IBasePresenter<View> {
        List<CityListBean> getCityLists();

        void setBgImgPath(String path);

        void setNavImgPath(String navImgPath);

        int getNavMode();

        void setNavMode(int navMode);

        boolean getNotification();

        void setNotification(boolean notification);

        int getNotificationId();

        void setNotificationId(int id);

        boolean getRain();

        void setRain(boolean rain);

        boolean getAlarm();

        void setAlarm(boolean alarm);

        boolean getAutoUpdate();

        void setAutoUpdate(boolean autoUpdate);

        boolean getNightNoUpdate();

        void setNightNoUpdate(boolean nightNoUpdate);

        boolean getNotificationChannelCreated();

        void setNotificationChannelCreated(boolean channelCreated);

        void setBgMode(int mode);

        boolean getCheckUpdate();

        void setCheckUpdate(boolean checkUpdate);

        int getBgMode();
    }
}
