package com.xily.weather.presenter;

import com.xily.weather.base.RxBasePresenter;
import com.xily.weather.contract.SettingsContract;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.bean.CityListBean;

import java.util.List;

import javax.inject.Inject;

public class SettingsPresenter extends RxBasePresenter<SettingsContract.View> implements SettingsContract.Presenter {
    DataManager mDataManager;

    @Inject
    public SettingsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public List<CityListBean> getCityLists() {
        return mDataManager.getCityList();
    }

    @Override
    public void setBgImgPath(String path) {
        mDataManager.setBgImgPath(path);
    }

    @Override
    public void setNavImgPath(String navImgPath) {
        mDataManager.setNavImgPath(navImgPath);
    }

    @Override
    public int getNavMode() {
        return mDataManager.getNavMode();
    }

    @Override
    public void setNavMode(int navMode) {
        mDataManager.setNavMode(navMode);
    }

    @Override
    public boolean getNotification() {
        return mDataManager.getNotification();
    }

    @Override
    public void setNotification(boolean notification) {
        mDataManager.setNotification(notification);
    }

    @Override
    public int getNotificationId() {
        return mDataManager.getNotificationId();
    }

    @Override
    public void setNotificationId(int id) {
        mDataManager.setNotificationId(id);
    }

    @Override
    public boolean getRain() {
        return mDataManager.getRain();
    }

    @Override
    public void setRain(boolean rain) {
        mDataManager.setRain(rain);
    }

    @Override
    public boolean getAlarm() {
        return mDataManager.getAlarm();
    }

    @Override
    public void setAlarm(boolean alarm) {
        mDataManager.setAlarm(alarm);
    }

    @Override
    public boolean getAutoUpdate() {
        return mDataManager.getAutoUpdate();
    }

    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        mDataManager.setAutoUpdate(autoUpdate);
    }

    @Override
    public boolean getNightNoUpdate() {
        return mDataManager.getNightNoUpdate();
    }

    @Override
    public void setNightNoUpdate(boolean nightNoUpdate) {
        mDataManager.setNightNoUpdate(nightNoUpdate);
    }

    @Override
    public boolean getNotificationChannelCreated() {
        return mDataManager.getNotificationChannelCreated();
    }

    @Override
    public void setNotificationChannelCreated(boolean channelCreated) {
        mDataManager.setNotificationChannelCreated(channelCreated);
    }

    @Override
    public void setBgMode(int mode) {
        mDataManager.setBgMode(mode);
    }

    @Override
    public boolean getCheckUpdate() {
        return mDataManager.getCheckUpdate();
    }

    @Override
    public void setCheckUpdate(boolean checkUpdate) {
        mDataManager.setCheckUpdate(checkUpdate);
    }

    @Override
    public int getBgMode() {
        return mDataManager.getBgMode();
    }
}
