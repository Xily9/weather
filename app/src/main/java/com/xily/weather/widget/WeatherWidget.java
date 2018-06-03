package com.xily.weather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.activity.MainActivity;
import com.xily.weather.db.CityList;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.service.WeatherService;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.PreferenceUtil;
import com.xily.weather.utils.WeatherUtil;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Map;

public class WeatherWidget extends AppWidgetProvider {
    private Map<String, Integer> map = WeatherUtil.getWeatherIcons();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (!DeviceUtil.isServiceRunning(BuildConfig.APPLICATION_ID + ".service.WeatherService")) {
            Intent startIntent = new Intent(context, WeatherService.class);
            context.startService(startIntent);
        }
        PreferenceUtil preferenceUtil = PreferenceUtil.getInstance();
        List<CityList> cityLists = DataSupport.findAll(CityList.class);
        int cityId = preferenceUtil.get("notificationId", 0);
        boolean check = false;
        for (CityList cityList : cityLists) {
            if (cityList.getId() == cityId) {
                check = true;
                break;
            }
        }
        if (!check && !cityLists.isEmpty()) {
            preferenceUtil.put("notificationId", cityLists.get(0).getId());
        }
        CityList cityList = DataSupport.find(CityList.class, preferenceUtil.get("notificationId", 0));
        if (cityList != null) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
            WeatherInfo weatherInfo = new Gson().fromJson(cityList.getWeatherData(), WeatherInfo.class);
            remoteViews.setTextViewText(R.id.cityName, cityList.getCityName());
            if (weatherInfo != null) {
                WeatherInfo.ValueBean valueBean = weatherInfo.getValue().get(0);
                remoteViews.setTextViewText(R.id.content, valueBean.getRealtime().getWeather() + "   " + valueBean.getPm25().getAqi() + " " + valueBean.getPm25().getQuality() + "   " + valueBean.getRealtime().getWd() + valueBean.getRealtime().getWs());
                if (map.containsKey(valueBean.getRealtime().getImg())) {
                    remoteViews.setImageViewResource(R.id.icon, map.get(valueBean.getRealtime().getImg()));
                } else {
                    remoteViews.setImageViewResource(R.id.icon, R.drawable.weather_na);
                    LogUtil.d("unknown", valueBean.getRealtime().getWeather() + valueBean.getRealtime().getImg());
                }
                remoteViews.setTextViewText(R.id.temperature, valueBean.getRealtime().getTemp() + "°");
            }
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.root, pendingIntent);
            for (int appWidgetId : appWidgetIds) {
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
    }
/*
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("android.appwidget.action.APPWIDGET_UPDATE".equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
                }
            }
        }
    }
*/
}
