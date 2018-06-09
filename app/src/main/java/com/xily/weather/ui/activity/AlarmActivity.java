package com.xily.weather.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.xily.weather.R;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.WeatherBean;
import com.xily.weather.ui.adapter.AlarmAdapter;
import com.xily.weather.utils.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;

public class AlarmActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycle)
    RecyclerView recyclerView;
    private int id;
    private List<WeatherBean.ValueBean.AlarmsBean> alarmsBeanList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_alarm;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        Intent intent = getIntent();
        id = intent.getIntExtra("alarmId", -1);
        LogUtil.d("id", "" + id);
        if (id >= 0) {
            loadData();
        }
    }

    @Override
    public void loadData() {
        CityListBean cityList = DataSupport.find(CityListBean.class, id);
        if (cityList != null) {
            WeatherBean weatherBean = new Gson().fromJson(cityList.getWeatherData(), WeatherBean.class);
            alarmsBeanList = weatherBean.getValue().get(0).getAlarms();
            finishTask();
        }
    }

    @Override
    public void finishTask() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AlarmAdapter(alarmsBeanList));
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("预警信息");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
