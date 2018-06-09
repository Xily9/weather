package com.xily.weather.model.bean;

import org.litepal.crud.DataSupport;

public class AlarmsBean extends DataSupport {
    private int id;
    private String notificationId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
