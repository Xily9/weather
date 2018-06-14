package com.xily.weather.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.base.RxBaseFragment;
import com.xily.weather.contract.PagerContract;
import com.xily.weather.model.bean.WeatherBean;
import com.xily.weather.presenter.PagerPresenter;
import com.xily.weather.ui.activity.AlarmActivity;
import com.xily.weather.ui.adapter.ForecastAdapter;
import com.xily.weather.ui.adapter.SuggestAdapter;
import com.xily.weather.utils.ColorUtil;
import com.xily.weather.utils.SnackbarUtil;
import com.xily.weather.widget.Weather3View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends RxBaseFragment<PagerPresenter> implements PagerContract.View {
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
    private boolean isVisible, isViewCreated;

    public static HomePagerFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        HomePagerFragment fragment = new HomePagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_fragment_homepager;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initToolbar();
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        mPresenter.getCityInfo(position);
        swipeRefreshLayout.setColorSchemeColors(ColorUtil.getAttrColor(getActivity(), R.attr.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(() -> mPresenter.getWeather(true));
        isViewCreated = true;
        onVisible();
        mPresenter.getWeather(false);
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
            mPresenter.getSetTitleUpdateTime();
        }
    }

    @Override
    public void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void showWeather(WeatherBean weatherBean) {
        WeatherBean.ValueBean valueBean = weatherBean.getValue().get(0);
        temperature.setText(valueBean.getRealtime().getTemp());
        weather.setText(valueBean.getRealtime().getWeather());
        air.setText(valueBean.getPm25().getAqi() + " " + valueBean.getPm25().getQuality());
        wet.setText(valueBean.getRealtime().getSd() + "%");
        wind.setText(valueBean.getRealtime().getWd() + valueBean.getRealtime().getWs());
        sendibleTemp.setText(valueBean.getRealtime().getSendibleTemp() + "°C");
        if (!valueBean.getAlarms().isEmpty()) {
            alarm.setVisibility(View.VISIBLE);
            List<String> list = new ArrayList<>();
            for (WeatherBean.ValueBean.AlarmsBean alarmsBean : valueBean.getAlarms()) {
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
                intent.putExtra("alarmId", mPresenter.getCityId());
                startActivity(intent);
            });
        }
        forecast.setLayoutManager(new LinearLayoutManager(getActivity()) {{
            setOrientation(LinearLayoutManager.HORIZONTAL);
        }});
        forecast.setAdapter(new ForecastAdapter(valueBean.getWeathers()));
        weather3View.setData(valueBean.getWeatherDetailsInfo().getWeather3HoursDetailsInfos());
        pm25.setText(valueBean.getPm25().getPm25());
        pm10.setText(valueBean.getPm25().getPm10());
        so2.setText(valueBean.getPm25().getSo2());
        no2.setText(valueBean.getPm25().getNo2());
        co.setText(valueBean.getPm25().getCo());
        o3.setText(valueBean.getPm25().getO3());
        List<WeatherBean.ValueBean.IndexesBean> indexesBeans = valueBean.getIndexes();
        suggest.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        suggest.setAdapter(new SuggestAdapter(indexesBeans));
    }

    @Override
    public void setRefreshing(boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void setUpdateTime(String updateTime) {
        this.updateTime.setText(updateTime);
    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void sendBroadcast() {
        Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".LOCAL_BROADCAST");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        Intent intent2 = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        getApplicationContext().sendBroadcast(intent2);
    }

    @Override
    public void showErrorMsg(String msg) {
        SnackbarUtil.showMessage(getActivity().getWindow().getDecorView(), msg);
    }
}
