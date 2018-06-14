package com.xily.weather.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xily.weather.R;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.contract.AlarmContract;
import com.xily.weather.model.bean.WeatherBean;
import com.xily.weather.presenter.AlarmPresenter;
import com.xily.weather.ui.adapter.AlarmAdapter;
import com.xily.weather.utils.LogUtil;

import java.util.List;

import butterknife.BindView;

public class AlarmActivity extends RxBaseActivity<AlarmPresenter> implements AlarmContract.View {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycle)
    RecyclerView recyclerView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_alarm;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        Intent intent = getIntent();
        int id = intent.getIntExtra("alarmId", -1);
        LogUtil.d("id", "" + id);
        if (id >= 0) {
            mPresenter.getAlarms(id);
        }
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

    @Override
    public void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    public void show(List<WeatherBean.ValueBean.AlarmsBean> alarmsBeanList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AlarmAdapter(alarmsBeanList));
    }
}
