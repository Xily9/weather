package com.xily.weather.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.contract.AddCityContract;
import com.xily.weather.model.bean.BusBean;
import com.xily.weather.presenter.AddCityPresenter;
import com.xily.weather.rx.RxBus;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.SnackbarUtil;
import com.xily.weather.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AddCityActivity extends RxBaseActivity<AddCityPresenter> implements AddCityContract.View {
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView title;
    private ArrayAdapter<String> adapter;
    private List<String> mDataList = new ArrayList<>();
    private List<Integer> mCodeList = new ArrayList<>();
    private int level = 0;
    private int provinceId;
    private String provinceName;
    private int cityId;
    private String cityName;
    private int weatherId;
    private String countyName;
    private boolean isSearch;
    private ProgressDialog progressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_addcity;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        initListView();
        loadData();
    }

    private void initListView() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (isSearch || level == 3) {
                if (isSearch)
                    DeviceUtil.hideSoftInput(this);
                weatherId = mCodeList.get(i);
                countyName = mDataList.get(i);
                if (mPresenter.getCityByWeatherId(weatherId).isEmpty()) {
                    mPresenter.addCity(weatherId, countyName);
                    ToastUtil.ShortToast("添加成功!");
                    BusBean busBean = new BusBean();
                    busBean.setStatus(1);
                    RxBus.getInstance().post(busBean);
                    setResult(1);
                    finish();
                } else {
                    SnackbarUtil.showMessage(getWindow().getDecorView(), "该城市已经被添加过!");
                }
            } else if (level == 1) {
                provinceId = mCodeList.get(i);
                provinceName = mDataList.get(i);
                loadData();
            } else if (level == 2) {
                cityId = mCodeList.get(i);
                cityName = mDataList.get(i);
                loadData();
            }
        });
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void loadData() {
        switch (level) {
            case 0:
                title.setText("中国");
                mPresenter.queryProvinces();
                break;
            case 1:
                title.setText(provinceName);
                mPresenter.queryCities(provinceId);
                break;
            case 2:
                title.setText(cityName);
                mPresenter.queryCounties(provinceId, cityId);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        level -= 2;
        if (level < 0) {
            finish();
        } else {
            loadData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    level--;
                    isSearch = false;
                    loadData();
                } else {
                    isSearch = true;
                    mPresenter.search(newText);
                }
                return false;
            }
        });
        mSearchView.setQueryHint("需要查询的城市名称");
        return true;
    }

    /**
     * 显示进度对话框
     */
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void show(List<String> dataList, List<Integer> codeList) {
        level++;
        mDataList.clear();
        mDataList.addAll(dataList);
        mCodeList.clear();
        mCodeList.addAll(codeList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initInject() {
        getActivityComponent().inject(this);
    }
}
