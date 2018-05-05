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
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.activity.AlarmActivity;
import com.xily.weather.activity.MainActivity;
import com.xily.weather.db.Alarms;
import com.xily.weather.db.CityList;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.network.RetrofitHelper;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.PreferenceUtil;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

public class WeatherService extends Service {
    private myBroadcastReceiver myBroadcastReceiver;
    private PreferenceUtil preferenceUtil;
    private PendingIntent pendingIntent;
    private boolean isForeground;
    private int id = 2;
    private Map<String, Integer> map = new HashMap<String, Integer>() {{
        put("0", R.drawable.weather_0);
        put("1", R.drawable.weather_1);
        put("2", R.drawable.weather_2);
        put("3", R.drawable.weather_3);
        put("4", R.drawable.weather_4);
        put("7", R.drawable.weather_7);
        put("8", R.drawable.weather_8);
        put("9", R.drawable.weather_9);
        put("29", R.drawable.weather_29);
    }};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferenceUtil = PreferenceUtil.getInstance();
        boolean isAutoUpdate = preferenceUtil.get("isAutoUpdate", false);
        boolean notification = preferenceUtil.get("notification", false);
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
        CityList cityList = DataSupport.find(CityList.class, preferenceUtil.get("notificationId", 0));
        if (cityList != null) {
            WeatherInfo weatherInfo = new Gson().fromJson(cityList.getWeatherData(), WeatherInfo.class);
            Intent intent1 = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weather")
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
            remoteViews.setTextViewText(R.id.cityName, cityList.getCityName());
            if (weatherInfo != null) {
                WeatherInfo.ValueBean valueBean = weatherInfo.getValue().get(0);
                remoteViews.setTextViewText(R.id.content, valueBean.getRealtime().getWeather() + " " + valueBean.getRealtime().getTemp() + "°C" + " " + valueBean.getPm25().getQuality());
                if (map.containsKey(valueBean.getRealtime().getImg())) {
                    builder.setSmallIcon(map.get(valueBean.getRealtime().getImg()));
                    remoteViews.setImageViewResource(R.id.icon, map.get(valueBean.getRealtime().getImg()));
                } else {
                    builder.setSmallIcon(R.drawable.weather_na);
                    remoteViews.setImageViewResource(R.id.icon, R.drawable.weather_na);
                    LogUtil.d("unknown", valueBean.getRealtime().getWeather() + valueBean.getRealtime().getImg());
                }
            } else {
                builder.setContentText("N/A");
                builder.setSmallIcon(R.drawable.weather_na);
                remoteViews.setImageViewResource(R.id.icon, R.drawable.weather_na);
            }
            if (!TextUtils.isEmpty(cityList.getUpdateTimeStr())) {
                remoteViews.setTextViewText(R.id.updateTime, "更新于 " + cityList.getUpdateTimeStr());
            }
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
        boolean nightNoUpdate = preferenceUtil.get("nightNoUpdate", false);
        if ((nightNoUpdate && hour < 23 && hour >= 6) || !nightNoUpdate) {
            List<CityList> cityList = DataSupport.findAll(CityList.class);
            Observable.from(cityList)
                    .flatMap(cityList1 -> RetrofitHelper.getMeiZuWeatherApi()
                            .getWeather(String.valueOf(cityList1.getWeatherId()))
                            .subscribeOn(Schedulers.io())
                            .doOnNext(weatherInfo -> {
                                WeatherInfo.ValueBean valueBean = weatherInfo.getValue().get(0);
                                CityList cityListUpdate = new CityList();
                                cityListUpdate.setWeatherData(new Gson().toJson(weatherInfo));
                                cityListUpdate.setUpdateTime(System.currentTimeMillis());
                                cityListUpdate.setUpdateTimeStr(valueBean.getRealtime().getTime().substring(11, 16));
                                cityListUpdate.update(cityList1.getId());
                                if (preferenceUtil.get("alarm", false)) {
                                    for (WeatherInfo.ValueBean.AlarmsBean alarmsBean : valueBean.getAlarms()) {
                                        List<Alarms> alarms = DataSupport.where("notificationid=?", alarmsBean.getAlarmId()).find(Alarms.class);
                                        if (alarms.isEmpty()) {
                                            getNotificationManager().notify(id++, getNotification(alarmsBean.getAlarmTypeDesc() + "预警", alarmsBean.getAlarmContent(), cityList1.getId()));
                                            Alarms alarms1 = new Alarms();
                                            alarms1.setNotificationId(alarmsBean.getAlarmId());
                                            alarms1.save();
                                        }
                                    }
                                }
                                if (preferenceUtil.get("rain", false)) {
                                    String day = String.valueOf(calendar.get(Calendar.YEAR)) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH);
                                    String rainNotificationTime = preferenceUtil.get("rainNotificationTime", "");
                                    if (hour > 6 && !rainNotificationTime.equals(day)) {
                                        preferenceUtil.put("rainNotificationTime", day);
                                        if (valueBean.getWeathers().get(0).getWeather().contains("雨")) {
                                            getNotificationManager().notify(id++, getNotification("今天有雨", "今天天气为" + valueBean.getWeathers().get(0).getWeather() + ",出门记得带伞!"));
                                        }
                                    }
                                }
                                boolean notification = preferenceUtil.get("notification", false);
                                if (notification) {
                                    startNotification(true);
                                }
                                Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".WEATHER_BROADCAST");
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
            intent = new Intent(this, AlarmActivity.class);
            intent.putExtra("id", id);
        } else
            intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weather")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        if (type == 1)
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        else
            builder.setContentText(content);
        return builder.build();
    }

    class myBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isAutoUpdate = preferenceUtil.get("isAutoUpdate", false);
            boolean notification = preferenceUtil.get("notification", false);
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
