package com.xily.weather.ui.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.contract.CityContract;
import com.xily.weather.model.bean.BusBean;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.presenter.CityPresenter;
import com.xily.weather.rx.RxBus;
import com.xily.weather.ui.adapter.CityAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CityActivity extends RxBaseActivity<CityPresenter> implements CityContract.View {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycle)
    RecyclerView mRecycleView;
    @BindView(R.id.empty)
    TextView empty;
    @BindView(R.id.view)
    CoordinatorLayout coordinatorLayout;
    private List<CityListBean> cityList = new ArrayList<>();
    private CityAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_city;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        initRecycleView();
        loadData();
    }

    private void initRecycleView() {
        adapter = new CityAdapter(cityList);
        mRecycleView.setAdapter(adapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            //滑动事件完成时回调
            //在这里可以实现撤销操作
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // 处理滑动事件回调
                final int position = viewHolder.getAdapterPosition();
                CityListBean city = cityList.get(position);
                int id = city.getId();
                mPresenter.deleteCity(id);
                Snackbar.make(coordinatorLayout, "删除成功!", Snackbar.LENGTH_SHORT)
                        .setAction("撤销", v -> {
                            CityListBean newCity = mPresenter.addCity(city);
                            cityList.add(position, newCity);
                            adapter.notifyItemInserted(position);
                            adapter.notifyItemRangeChanged(position, cityList.size());
                            BusBean busBean = new BusBean();
                            busBean.setStatus(1);
                            RxBus.getInstance().post(busBean);
                        }).show();
                BusBean busBean = new BusBean();
                busBean.setStatus(1);
                RxBus.getInstance().post(busBean);
                cityList.remove(position);
                adapter.notifyItemRemoved(position);
                int cityId = mPresenter.getNotificationId();
                if (id == cityId) {
                    if (cityList.isEmpty()) {
                        mPresenter.setNotificationId(0);
                        mPresenter.setNotification(false);
                        mPresenter.setAutoUpdate(false);
                    } else {
                        mPresenter.setNotificationId(cityList.get(0).getId());
                    }
                    Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".LOCAL_BROADCAST");
                    LocalBroadcastManager.getInstance(CityActivity.this).sendBroadcast(intent);
                }
            }

            //处理动画
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变 Item 的透明度，以实现滑动过程中实现渐变效果
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

        });
        itemTouchHelper.attachToRecyclerView(mRecycleView);
        adapter.setOnItemClickListener(position -> {
            BusBean busBean = new BusBean();
            busBean.setStatus(2);
            busBean.setPosition(position);
            RxBus.getInstance().post(busBean);
            finish();
        });
    }

    @Override
    public void loadData() {
        cityList.clear();
        cityList.addAll(mPresenter.getCityList());
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

    @Override
    public void initInject() {
        getActivityComponent().inject(this);
    }
}
