package com.xily.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.adapter.CityAdapter;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.db.CityList;
import com.xily.weather.entity.BusInfo;
import com.xily.weather.rx.RxBus;
import com.xily.weather.utils.PreferenceUtil;
import com.xily.weather.utils.SnackbarUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class CityActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycle)
    RecyclerView mRecycleView;
    @BindView(R.id.empty)
    TextView empty;
    @BindView(R.id.view)
    CoordinatorLayout coordinatorLayout;
    private List<CityList> cityList = new ArrayList<>();
    private PreferenceUtil preferenceUtil;
    private Subscription subscription;
    private CityAdapter adapter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_city;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        preferenceUtil = PreferenceUtil.getInstance();
        initToolBar();
        initRecycleView();
        loadData();
    }

    private void initRecycleView() {
        adapter = new CityAdapter(this, cityList);
        mRecycleView.setAdapter(adapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(position -> {
            BusInfo busInfo = new BusInfo();
            busInfo.setStatus(2);
            busInfo.setPosition(position);
            RxBus.getInstance().post(busInfo);
            finish();
        });
        adapter.setOnItemLongClickListener(position -> {
            int id = cityList.get(position).getId();
            DataSupport.delete(CityList.class, id);
            SnackbarUtil.showMessage(coordinatorLayout, "删除成功!");
            BusInfo busInfo = new BusInfo();
            busInfo.setStatus(1);
            RxBus.getInstance().post(busInfo);
            cityList.remove(position);
            adapter.notifyDataSetChanged();
            int cityId = preferenceUtil.get("notificationId", 0);
            if (id == cityId) {
                if (cityList.isEmpty()) {
                    preferenceUtil.put("notificationId", 0);
                    preferenceUtil.put("notification", false);
                    preferenceUtil.put("isAutoUpdate", false);
                } else {
                    preferenceUtil.put("notificationId", cityList.get(0).getId());
                }
                Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".LOCAL_BROADCAST");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            return true;
        });
    }

    @Override
    public void loadData() {
        cityList.clear();
        cityList.addAll(DataSupport.findAll(CityList.class));
        finishTask();
    }

    @Override
    public void finishTask() {
        if (cityList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("城市管理");
    }

    @OnClick(R.id.btn_add)
    void addCity() {
        startActivityForResult(new Intent(this, AddCityActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            loadData();
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
