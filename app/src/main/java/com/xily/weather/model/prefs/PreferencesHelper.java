package com.xily.weather.model.prefs;

public interface PreferencesHelper {
    int getCheckedVersion();

    void setCheckVersion(int checkVersion);

    boolean getCheckUpdate();

    void setCheckUpdate(boolean checkUpdate);

    int getBgMode();

    void setBgMode(int mode);

    String getBingPicTime();

    void setBingPicTime(String time);

    String getBingPicUrl();

    void setBingPicUrl(String url);

    String getBgImgPath();

    void setBgImgPath(String path);

    String getNavImgPath();

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

}
