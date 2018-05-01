package com.xily.weather.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.adapter.CityAdapter;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.db.CityList;
import com.xily.weather.utils.SnackbarUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CityActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycle)
    RecyclerView mRecycleView;
    @BindView(R.id.empty)
    TextView empty;
    @BindView(R.id.view)
    CoordinatorLayout coordinatorLayout;
    private List<CityList> cityList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_city;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        loadData();
    }

    @Override
    public void loadData() {
        cityList = DataSupport.findAll(CityList.class);
        finishTask();
    }

    @Override
    public void finishTask() {
        if (cityList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            CityAdapter adapter = new CityAdapter(this, cityList);
            mRecycleView.setAdapter(adapter);
            mRecycleView.setLayoutManager(new LinearLayoutManager(this));
            adapter.setOnClicklistener(position -> {
                Intent intent = new Intent();
                intent.putExtra("id", position);
                setResult(1, intent);
                finish();
            });
            adapter.setOnLongClickListener(position -> {
                DataSupport.delete(CityList.class, cityList.get(position).getId());
                SnackbarUtil.showMessage(coordinatorLayout, "删除成功!");
                setResult(2);
                cityList.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            setResult(2);
            recreate();
        }
    }
}
