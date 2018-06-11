package com.xily.weather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.app.App;
import com.xily.weather.di.component.DaggerServiceComponent;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.bean.AlarmsBean;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.WeatherBean;
import com.xily.weather.ui.activity.AlarmActivity;
import com.xily.weather.ui.activity.MainActivity;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.WeatherUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public class WeatherService extends Service {
    private myBroadcastReceiver myBroadcastReceiver;
    private PendingIntent pendingIntent;
    private boolean isForeground;
    private int id = 2;
    private Map<String, Integer> map = WeatherUtil.getWeatherIcons();
    @Inject
    DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerServiceComponent.builder()
                .appComponent(App.getAppComponent())
                .build().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isAutoUpdate = mDataManager.getAutoUpdate();
        boolean notification = mDataManager.getNotification();
        if (notification) {
            startNotification(false);
        }
        if (isAutoUpdate) {
            runTask();
        }
        if (!notification && !isAutoUpdate) {
            if (pendingIntent != null) {
                getAlarmManager().cancel(pendingIntent);
            }
            stopSelf();
        } else {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BuildConfig.APPLICATION_ID + ".LOCAL_BROADCAST");
            myBroadcastReceiver = new myBroadcastReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, intentFilter);
            startTimer();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTimer() {
        Intent i = new Intent(this, WeatherService.class);
        pendingIntent = PendingIntent.getService(this, 0, i, 0);
        getAlarmManager().set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 60 * 1000, pendingIntent);
    }

    private void startNotification(boolean isUpdate) {
        CityListBean cityList = mDataManager.getCityById(mDataManager.getNotificationId());
        if (cityList != null) {
            WeatherBean weatherBean = new Gson().fromJson(cityList.getWeatherData(), WeatherBean.class);
            Intent intent1 = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weather")
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
            remoteViews.setTextViewText(R.id.cityName, cityList.getCityName());
            if (weatherBean != null) {
                WeatherBean.ValueBean valueBean = weatherBean.getValue().get(0);
                remoteViews.setTextViewText(R.id.content, valueBean.getRealtime().getWeather() + "   " + valueBean.getPm25().getAqi() + " " + valueBean.getPm25().getQuality() + "   " + valueBean.getRealtime().getWd() + valueBean.getRealtime().getWs());
                if (map.containsKey(valueBean.getRealtime().getImg())) {
                    builder.setSmallIcon(map.get(valueBean.getRealtime().getImg()));
                    remoteViews.setImageViewResource(R.id.icon, map.get(valueBean.getRealtime().getImg()));
                } else {
                    builder.setSmallIcon(R.drawable.weather_na);
                    remoteViews.setImageViewResource(R.id.icon, R.drawable.weather_na);
                    LogUtil.d("unknown", valueBean.getRealtime().getWeather() + valueBean.getRealtime().getImg());
                }
                remoteViews.setTextViewText(R.id.temperature, valueBean.getRealtime().getTemp() + "°");
            } else {
                builder.setContentText("N/A");
                builder.setSmallIcon(R.drawable.weather_na);
                remoteViews.setImageViewResource(R.id.icon, R.drawable.weather_na);
            }/*
            if (!TextUtils.isEmpty(cityList.getUpdateTimeStr())) {
                remoteViews.setTextViewText(R.id.updateTime, "更新于 " + cityList.getUpdateTimeStr());
            }*/
            builder.setCustomContentView(remoteViews);
            Notification notification = builder.build();
            if (isUpdate) {
                getNotificationManager().notify(1, notification);
            } else {
                startForeground(1, notification);
                isForeground = true;
            }
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void runTask() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        boolean nightNoUpdate = mDataManager.getNightNoUpdate();
        if ((nightNoUpdate && hour < 23 && hour >= 6) || !nightNoUpdate) {
            List<CityListBean> cityList = mDataManager.getCityList();
            Observable.from(cityList)
                    .flatMap(cityList1 -> mDataManager
                            .getWeather(String.valueOf(cityList1.getWeatherId()))
                            .subscribeOn(Schedulers.io())
                            .doOnNext(weatherInfo -> {
                                WeatherBean.ValueBean valueBean = weatherInfo.getValue().get(0);
                                CityListBean cityListUpdate = new CityListBean();
                                cityListUpdate.setWeatherData(new Gson().toJson(weatherInfo));
                                cityListUpdate.setUpdateTime(System.currentTimeMillis());
                                cityListUpdate.setUpdateTimeStr(valueBean.getRealtime().getTime().substring(11, 16));
                                cityListUpdate.update(cityList1.getId());
                                if (mDataManager.getAlarm()) {
                                    for (WeatherBean.ValueBean.AlarmsBean alarmsBean : valueBean.getAlarms()) {
                                        List<AlarmsBean> alarms = mDataManager.getAlarmsById(alarmsBean.getAlarmId());
                                        if (alarms.isEmpty()) {
                                            getNotificationManager().notify(id++, getNotification(cityList1.getCityName() + " " + alarmsBean.getAlarmTypeDesc() + "预警", alarmsBean.getAlarmContent(), cityList1.getId()));
                                            AlarmsBean alarmsBean1 = new AlarmsBean();
                                            alarmsBean1.setNotificationId(alarmsBean.getAlarmId());
                                            alarmsBean1.save();
                                        }
                                    }
                                }
                                if (mDataManager.getRain()) {
                                    String day = String.valueOf(calendar.get(Calendar.YEAR)) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH);
                                    String rainNotificationTime = mDataManager.getRainNotificationTime();
                                    if (hour > 6 && !rainNotificationTime.equals(day)) {
                                        mDataManager.setRainNotificationTime(day);
                                        if (valueBean.getWeathers().get(0).getWeather().contains("雨")) {
                                            getNotificationManager().notify(id++, getNotification(cityList1.getCityName() + "今天有雨", "今天天气为" + valueBean.getWeathers().get(0).getWeather() + ",出门记得带伞!"));
                                        }
                                    }
                                }
                                boolean notification = mDataManager.getNotification();
                                if (notification) {
                                    startNotification(true);
                                }
                                Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
                                sendBroadcast(intent);
                            }))
                    .subscribe();
        }
    }

    private Notification getNotification(String title, String content) {
        return getNotification(title, content, 2, 0);
    }

    private Notification getNotification(String title, String content, int id) {
        return getNotification(title, content, 1, id);
    }

    private Notification getNotification(String title, String content, int type, int id) {
        Intent intent;
        if (type == 1) {
            LogUtil.d("alarmId", "" + id);
            intent = new Intent(this, AlarmActivity.class);
            intent.putExtra("alarmId", id);
        } else
            intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, "weather")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentText(content)
                .setAutoCancel(true)
                .build();
    }

    class myBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isAutoUpdate = mDataManager.getAutoUpdate();
            boolean notification = mDataManager.getNotification();
            if (notification) {
                if (!isForeground)
                    startNotification(false);
                else
                    startNotification(true);
            } else {
                if (isForeground) {
                    stopForeground(true);
                    isForeground = false;
                }
            }

            if (!notification && !isAutoUpdate) {
                if (pendingIntent != null) {
                    getAlarmManager().cancel(pendingIntent);
                }
                stopSelf();
            }
        }
    }
}
