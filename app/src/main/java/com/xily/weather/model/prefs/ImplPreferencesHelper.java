package com.xily.weather.model.prefs;

import com.xily.weather.utils.PreferenceUtil;

import javax.inject.Inject;

public class ImplPreferencesHelper implements PreferencesHelper {
    private PreferenceUtil mPreference;

    @Inject
    public ImplPreferencesHelper(PreferenceUtil preference) {
        mPreference = preference;
    }

    @Override
    public int getCheckedVersion() {
        return mPreference.get("checkedVersion", 0);
    }

    @Override
    public void setCheckVersion(int checkVersion) {
        mPreference.put("checkVersion", checkVersion);
    }

    @Override
    public boolean getCheckUpdate() {
        return mPreference.get("checkUpdate", true);
    }

    @Override
    public void setCheckUpdate(boolean checkUpdate) {
        mPreference.put("checkUpdate", checkUpdate);
    }

    @Override
    public int getBgMode() {
        return mPreference.get("bgMode", 0);
    }

    @Override
    public void setBgMode(int mode) {
        mPreference.put("bgMode", mode);
    }

    @Override
    public String getBingPicTime() {
        return mPreference.get("bingPicTime", "");
    }

    @Override
    public void setBingPicTime(String time) {
        mPreference.put("bingPicTime", time);
    }

    @Override
    public String getBingPicUrl() {
        return mPreference.get("bingPicUrl", "");
    }

    @Override
    public void setBingPicUrl(String url) {
        mPreference.put("bingPicUrl", url);
    }

    @Override
    public String getBgImgPath() {
        return mPreference.get("bgImgPath", "");
    }

    @Override
    public void setBgImgPath(String path) {
        mPreference.put("bgImgPath", path);
    }

    @Override
    public String getNavImgPath() {
        return mPreference.get("navImgPath", "");
    }

    @Override
    public void setNavImgPath(String navImgPath) {
        mPreference.put("navImgPath", navImgPath);
    }

    @Override
    public int getNavMode() {
        return mPreference.get("navMode", 0);
    }

    @Override
    public void setNavMode(int navMode) {
        mPreference.put("navMode", navMode);
    }

    @Override
    public boolean getNotification() {
        return mPreference.get("notification", false);
    }

    @Override
    public void setNotification(boolean notification) {
        mPreference.put("notification", notification);
    }

    @Override
    public int getNotificationId() {
        return mPreference.get("notificationId", 0);
    }

    @Override
    public void setNotificationId(int id) {
        mPreference.put("notificationId", id);
    }

    @Override
    public boolean getRain() {
        return mPreference.get("rain", false);
    }

    @Override
    public void setRain(boolean rain) {
        mPreference.put("rain", rain);
    }

    @Override
    public boolean getAlarm() {
        return mPreference.get("alarm", false);
    }

    @Override
    public void setAlarm(boolean alarm) {
        mPreference.put("alarm", alarm);
    }

    @Override
    public boolean getAutoUpdate() {
        return mPreference.get("autoUpdate", false);
    }

    @Override
    public void setAutoUpdate(boolean autoUpdate) {
        mPreference.put("autoUpdate", autoUpdate);
    }

    @Override
    public boolean getNightNoUpdate() {
        return mPreference.get("nightNoUpdate", false);
    }

    @Override
    public void setNightNoUpdate(boolean nightNoUpdate) {
        mPreference.get("nightNoUpdate", nightNoUpdate);
    }

    @Override
    public boolean getNotificationChannelCreated() {
        return mPreference.get("notificationChannelCreated", false);
    }

    @Override
    public void setNotificationChannelCreated(boolean channelCreated) {
        mPreference.put("notificationChannelCreated", channelCreated);
    }

    @Override
    public String getRainNotificationTime() {
        return mPreference.get("rainNotificationTime", "");
    }

    @Override
    public void setRainNotificationTime(String time) {
        mPreference.put("rainNotificationTime", time);
    }
}
