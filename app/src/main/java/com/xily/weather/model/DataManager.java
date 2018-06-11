package com.xily.weather.model;

import com.xily.weather.model.bean.AlarmsBean;
import com.xily.weather.model.bean.CitiesBean;
import com.xily.weather.model.bean.CityBean;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.CountiesBean;
import com.xily.weather.model.bean.CountyBean;
import com.xily.weather.model.bean.ProvinceBean;
import com.xily.weather.model.bean.ProvincesBean;
import com.xily.weather.model.bean.SearchBean;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.bean.WeatherBean;
import com.xily.weather.model.db.DbHelper;
import com.xily.weather.model.network.HttpHelper;
import com.xily.weather.model.prefs.PreferencesHelper;

import java.util.List;

import rx.Observable;

public class DataManager implements HttpHelper, PreferencesHelper, DbHelper {
    private PreferencesHelper mPreferenceHelper;
    private HttpHelper mHttpHelper;
    private DbHelper mDbHelper;

    public DataManager(PreferencesHelper preferencesHelper, HttpHelper httpHelper, DbHelper dbHelper) {
        mPreferenceHelper = preferencesHelper;
        mHttpHelper = httpHelper;
        mDbHelper = dbHelper;
    }

    @Override
    public int getCheckedVersion() {
        return mPreferenceHelper.getCheckedVersion();
    }

    @Override
    public void setCheckVersion(int checkVersion) {
        mPreferenceHelper.setCheckVersion(checkVersion);
    }

    @Override
    public boolean getCheckUpdate() {
        return mPreferenceHelper.getCheckUpdate();
    }

    @Override
    public void setCheckUpdate(boolean checkUpdate) {
        mPreferenceHelper.setCheckUpdate(checkUpdate);
    }

    @Override
    public int getBgMode() {
        return mPreferenceHelper.getBgMode();
    }

    @Override
    public void setBgMode(int mode) {
        mPreferenceHelper.setBgMode(mode);
    }

    @Override
    public String getBingPicTime() {
        return mPreferenceHelper.getBingPicTime();
    }

    @Override
    public void setBingPicTime(String time) {
        mPreferenceHelper.setBingPicTime(time);
    }

    @Override
    public String getBingPicUrl() {
        return mPreferenceHelper.getBingPicUrl();
    }

    @Override
    public void setBingPicUrl(String url) {
        mPreferenceHelper.setBingPicUrl(url);
    }

    @Override
    public String getBgImgPath() {
        return mPreferenceHelper.getBgImgPath();
    }

    @Override
    public void setBgImgPath(String path) {
        mPreferenceHelper.setBgImgPath(path);
    }

    @Override
    public String getNavImgPath() {
        return mPreferenceHelper.getNavImgPath();
    }

    @Override
    public void setNavImgPath(String navImgPath) {
        mPreferenceHelper.setNavImgPath(navImgPath);
    }

    @Override
    public int getNavMode() {
        return mPreferenceHelper.getNavMode();
    }

    @Override
    public void setNavMode(int navMode) {
        mPreferenceHelper.setNavMode(navMode);
    }

    @Override
    public boolean getNotification() {
        return mPreferenceHelper.getNotification();
    }

    @Override
    public void setNotification(boolean notification) {
        mPreferenceHelper.setNotification(notification);
    }

    @Override
    public int getNotificationId() {
        return mPreferenceHelper.getNotificationId();
    }

    @Override
    public void setNotificationId(int id) {
        mPreferenceHelper.setNotificationId(id);
    }

    @Override
    public boolean getRain() {
        return mPreferenceHelper.getRain();
    }

    @Override
    public void setRain(boolean rain) {
        mPreferenceHelper.setRain(rain);
    }

    @Override
    public boolean getAlarm() {
        return mPreferenceHelper.getAlarm();
    }

    @Override
    public void setAlarm(boolean alarm) {
        mPreferenceHelper.setAlarm(alarm);
    }

    @Override
    public boolean getAutoUpdate() {
        return mPreferenceHelper.getAutoUpdate();
    }

    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        mPreferenceHelper.setAutoUpdate(autoUpdate);
    }

    @Override
    public boolean getNightNoUpdate() {
        return mPreferenceHelper.getNightNoUpdate();
    }

    @Override
    public void setNightNoUpdate(boolean nightNoUpdate) {
        mPreferenceHelper.setNightNoUpdate(nightNoUpdate);
    }

    @Override
    public boolean getNotificationChannelCreated() {
        return mPreferenceHelper.getNotificationChannelCreated();
    }

    @Override
    public void setNotificationChannelCreated(boolean channelCreated) {
        mPreferenceHelper.setNotificationChannelCreated(channelCreated);
    }

    @Override
    public String getRainNotificationTime() {
        return mPreferenceHelper.getRainNotificationTime();
    }

    @Override
    public void setRainNotificationTime(String time) {
        mPreferenceHelper.setRainNotificationTime(time);
    }

    @Override
    public List<CityListBean> getCityList() {
        return mDbHelper.getCityList();
    }

    @Override
    public CityListBean getCityById(int id) {
        return mDbHelper.getCityById(id);
    }

    @Override
    public List<CityListBean> getCityByWeatherId(int id) {
        return mDbHelper.getCityByWeatherId(id);
    }

    @Override
    public List<ProvinceBean> getProvince() {
        return mDbHelper.getProvince();
    }

    @Override
    public List<CityBean> getCity(String provinceId) {
        return mDbHelper.getCity(provinceId);
    }

    @Override
    public Observable<VersionBean> checkVersion() {
        return mHttpHelper.checkVersion();
    }

    @Override
    public Observable<WeatherBean> getWeather(String cityId) {
        return mHttpHelper.getWeather(cityId);
    }

    @Override
    public Observable<SearchBean> search(String location) {
        return mHttpHelper.search(location);
    }

    @Override
    public Observable<List<ProvincesBean>> getProvinces() {
        return mHttpHelper.getProvinces();
    }

    @Override
    public Observable<List<CitiesBean>> getCities(String province) {
        return mHttpHelper.getCities(province);
    }

    @Override
    public List<CountyBean> getCounty(String cityId) {
        return mDbHelper.getCounty(cityId);
    }

    @Override
    public void deleteCity(int id) {
        mDbHelper.deleteCity(id);
    }

    @Override
    public List<AlarmsBean> getAlarmsById(String id) {
        return mDbHelper.getAlarmsById(id);
    }

    @Override
    public Observable<List<CountiesBean>> getCounties(String province, String city) {
        return mHttpHelper.getCounties(province, city);
    }
}
