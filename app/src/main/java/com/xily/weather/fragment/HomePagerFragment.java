package com.xily.weather.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.activity.AlarmActivity;
import com.xily.weather.adapter.ForecastAdapter;
import com.xily.weather.adapter.SuggestAdapter;
import com.xily.weather.base.RxBaseFragment;
import com.xily.weather.db.CityList;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.network.RetrofitHelper;
import com.xily.weather.utils.ColorUtil;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.SnackbarUtil;
import com.xily.weather.widget.Weather3View;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomePagerFragment extends RxBaseFragment {
    @BindView(R.id.temperature)
    TextView temperature;
    @BindView(R.id.weather)
    TextView weather;
    @BindView(R.id.air)
    TextView air;
    @BindView(R.id.wet)
    TextView wet;
    @BindView(R.id.wind)
    TextView wind;
    @BindView(R.id.sendibleTemp)
    TextView sendibleTemp;
    @BindView(R.id.alarm)
    Button alarm;
    @BindView(R.id.forecast)
    RecyclerView forecast;
    @BindView(R.id.pm25)
    TextView pm25;
    @BindView(R.id.pm10)
    TextView pm10;
    @BindView(R.id.so2)
    TextView so2;
    @BindView(R.id.no2)
    TextView no2;
    @BindView(R.id.co)
    TextView co;
    @BindView(R.id.o3)
    TextView o3;
    @BindView(R.id.weather3)
    Weather3View weather3View;
    @BindView(R.id.suggest)
    RecyclerView suggest;
    @BindView(R.id.layout_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private TextView title;
    private TextView updateTime;
    private CityList cityList;
    private boolean isRefreshing;
    private boolean isVisible, isViewCreated;

    public static HomePagerFragment newInstance() {
        return new HomePagerFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_fragment_homepager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initToolbar();
        Bundle bundle = getArguments();
        int id = bundle.getInt("id");
        LogUtil.d("fragmentId", "" + id);
        cityList = DataSupport.find(CityList.class, id);
        if (cityList != null) {
            swipeRefreshLayout.setColorSchemeColors(ColorUtil.getAttrColor(getActivity(), R.attr.colorAccent));
            swipeRefreshLayout.setOnRefreshListener(() -> {
                isRefreshing = true;
                loadData();
            });
            isViewCreated = true;
            onVisible();
            loadData();
        }
    }

    @Override
    protected void initToolbar() {
        title = getActivity().findViewById(R.id.toolbar_title);
        updateTime = getActivity().findViewById(R.id.updateTime);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
        }
    }

    private void onVisible() {
        if (isVisible && isViewCreated) {
            title.setText(cityList.getCityName());
            updateTime.setText(cityList.getUpdateTimeStr() + "更新");
        }
    }

    @Override
    public void loadData() {
        Observable<WeatherInfo> offline = Observable.just(null)
                .map(o -> {
                    String data = cityList.getWeatherData();
                    if (isRefreshing || System.currentTimeMillis() - cityList.getUpdateTime() > 1000 * 60 * 60 || TextUtils.isEmpty(data)) {
                        return null;
                    } else {
                        return new Gson().fromJson(data, WeatherInfo.class);
                    }
                });
        Observable<WeatherInfo> online = RetrofitHelper.getWeatherApi()
                .getWeather(String.valueOf(cityList.getWeatherId()))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> swipeRefreshLayout.setRefreshing(true))
                .doOnUnsubscribe(() -> {
                    isRefreshing = false;
                    swipeRefreshLayout.setRefreshing(false);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(weatherInfo -> {
                    CityList cityListUpdate = new CityList();
                    cityListUpdate.setWeatherData(new Gson().toJson(weatherInfo));
                    cityListUpdate.setUpdateTime(System.currentTimeMillis());
                    cityListUpdate.setUpdateTimeStr(weatherInfo.getValue().get(0).getRealtime().getTime().substring(11, 16));
                    cityListUpdate.update(cityList.getId());
                    updateTime.setText(cityListUpdate.getUpdateTimeStr() + "更新");
                    cityList = DataSupport.find(CityList.class, cityList.getId());
                    Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".LOCAL_BROADCAST");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    Intent intent2 = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
                    getApplicationContext().sendBroadcast(intent2);
                });
        Observable.concat(offline, online)
                .takeFirst(weatherInfo -> weatherInfo != null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::finishTask, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getActivity().getWindow().getDecorView(), throwable.getMessage());
                });
    }

    public void finishTask(WeatherInfo weatherInfo) {
        WeatherInfo.ValueBean valueBean = weatherInfo.getValue().get(0);
        temperature.setText(valueBean.getRealtime().getTemp());
        weather.setText(valueBean.getRealtime().getWeather());
        air.setText(valueBean.getPm25().getAqi() + " " + valueBean.getPm25().getQuality());
        wet.setText(valueBean.getRealtime().getSd() + "%");
        wind.setText(valueBean.getRealtime().getWd() + valueBean.getRealtime().getWs());
        sendibleTemp.setText(valueBean.getRealtime().getSendibleTemp() + "°C");
        if (!valueBean.getAlarms().isEmpty()) {
            alarm.setVisibility(View.VISIBLE);
            List<String> list = new ArrayList<>();
            for (WeatherInfo.ValueBean.AlarmsBean alarmsBean : valueBean.getAlarms()) {
                list.add(alarmsBean.getAlarmTypeDesc() + "预警");
            }
            StringBuilder stringBuilder = new StringBuilder();
            boolean isFirst = true;
            for (String str : list) {
                if (isFirst) {
                    stringBuilder.append(str);
                    isFirst = false;
                } else {
                    stringBuilder.append(',');
                    stringBuilder.append(str);
                }
            }
            alarm.setText(stringBuilder.toString());
            alarm.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AlarmActivity.class);
                intent.putExtra("alarmId", cityList.getId());
                startActivity(intent);
            });
        }
        forecast.setLayoutManager(new LinearLayoutManager(getActivity()) {{
            setOrientation(LinearLayoutManager.HORIZONTAL);
        }});
        forecast.setAdapter(new ForecastAdapter(getActivity(), valueBean.getWeathers()));
        weather3View.setData(valueBean.getWeatherDetailsInfo().getWeather3HoursDetailsInfos());
        pm25.setText(valueBean.getPm25().getPm25());
        pm10.setText(valueBean.getPm25().getPm10());
        so2.setText(valueBean.getPm25().getSo2());
        no2.setText(valueBean.getPm25().getNo2());
        co.setText(valueBean.getPm25().getCo());
        o3.setText(valueBean.getPm25().getO3());
        List<WeatherInfo.ValueBean.IndexesBean> indexesBeans = valueBean.getIndexes();
        suggest.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        suggest.setAdapter(new SuggestAdapter(getActivity(), indexesBeans));
    }
}
