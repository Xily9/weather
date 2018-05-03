package com.xily.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.xily.weather.R;
import com.xily.weather.adapter.AlarmAdapter;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.db.CityList;
import com.xily.weather.entity.WeatherInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;

public class AlarmActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycle)
    RecyclerView recyclerView;
    private int id;
    private List<WeatherInfo.ValueBean.AlarmsBean> alarmsBeanList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_alarm;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        if (id >= 0) {
            loadData();
        }
    }

    @Override
    public void loadData() {
        CityList cityList = DataSupport.find(CityList.class, id);
        if (cityList != null) {
            WeatherInfo weatherInfo = new Gson().fromJson(cityList.getWeatherData(), WeatherInfo.class);
            alarmsBeanList = weatherInfo.getValue().get(0).getAlarms();
            finishTask();
        }
    }

    @Override
    public void finishTask() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AlarmAdapter(this, alarmsBeanList));
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
