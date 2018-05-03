package com.xily.weather.activity;

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
import com.xily.weather.db.City;
import com.xily.weather.db.CityList;
import com.xily.weather.db.County;
import com.xily.weather.db.Province;
import com.xily.weather.entity.BusInfo;
import com.xily.weather.entity.CitiesInfo;
import com.xily.weather.entity.CountiesInfo;
import com.xily.weather.entity.ProvincesInfo;
import com.xily.weather.entity.SearchInfo;
import com.xily.weather.network.RetrofitHelper;
import com.xily.weather.rx.RxBus;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.SnackbarUtil;
import com.xily.weather.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddCityActivity extends RxBaseActivity {
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView title;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Integer> codeList = new ArrayList<>();
    private int level = 0;
    private int provinceId;
    private String provinceName;
    private int cityId;
    private String cityName;
    private int WeatherId;
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
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (isSearch || level == 3) {
                if (isSearch)
                    DeviceUtil.hideSoftInput(this);
                WeatherId = codeList.get(i);
                countyName = dataList.get(i);
                if (DataSupport.where("weatherid=?", String.valueOf(WeatherId)).find(CityList.class).isEmpty()) {
                    CityList cityList = new CityList();
                    cityList.setCityName(countyName);
                    cityList.setWeatherId(WeatherId);
                    cityList.save();
                    ToastUtil.ShortToast("添加成功!");
                    BusInfo busInfo = new BusInfo();
                    busInfo.setStatus(1);
                    RxBus.getInstance().post(busInfo);
                    finish();
                } else {
                    SnackbarUtil.showMessage(getWindow().getDecorView(), "该城市已经被添加过!");
                }
            } else if (level == 1) {
                provinceId = codeList.get(i);
                provinceName = dataList.get(i);
                loadData();
            } else if (level == 2) {
                cityId = codeList.get(i);
                cityName = dataList.get(i);
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
                queryProvinces();
                break;
            case 1:
                queryCities();
                break;
            case 2:
                queryCounties();
                break;
            default:
                break;
        }
    }

    private void search(String str) {
        RetrofitHelper.getHeWeatherApi()
                .search(str)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(this::showProgressDialog)
                .doOnUnsubscribe(this::closeProgressDialog)
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchInfo -> {
                    SearchInfo.HeWeather6Bean heWeather6Bean = searchInfo.getHeWeather6().get(0);
                    if (heWeather6Bean.getStatus().equals("ok")) {
                        dataList.clear();
                        codeList.clear();
                        for (SearchInfo.HeWeather6Bean.BasicBean basicBean : heWeather6Bean.getBasic()) {
                            dataList.add(basicBean.getLocation());
                            codeList.add(Integer.valueOf(basicBean.getCid().substring(2)));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    private void queryProvinces() {
        title.setText("中国");
        List<Province> provinceList = DataSupport.findAll(Province.class);
        if (provinceList.isEmpty()) {
            RetrofitHelper.getGuoLinApi()
                    .getProvinces()
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnUnsubscribe(this::closeProgressDialog)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(provincesInfoList -> {
                        dataList.clear();
                        codeList.clear();
                        for (ProvincesInfo provincesInfo : provincesInfoList) {
                            Province province = new Province();
                            province.setProvinceCode(provincesInfo.getId());
                            province.setProvinceName(provincesInfo.getName());
                            province.save();
                            dataList.add(provincesInfo.getName());
                            codeList.add(provincesInfo.getId());
                        }
                        finishTask();
                    }, throwable -> {
                        throwable.printStackTrace();
                        SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                    });
        } else {
            dataList.clear();
            codeList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
                codeList.add(province.getProvinceCode());
            }
            finishTask();
        }
    }

    @Override
    public void finishTask() {
        level++;
        adapter.notifyDataSetChanged();
    }

    private void queryCities() {
        title.setText(provinceName);
        String provinceIdStr = String.valueOf(provinceId);
        List<City> cityList = DataSupport.where("provinceid=?", provinceIdStr).find(City.class);
        if (cityList.isEmpty()) {
            RetrofitHelper.getGuoLinApi()
                    .getCities(provinceIdStr)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnUnsubscribe(this::closeProgressDialog)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(citiesInfoList -> {
                        dataList.clear();
                        codeList.clear();
                        for (CitiesInfo citiesInfo : citiesInfoList) {
                            City city = new City();
                            city.setCityCode(citiesInfo.getId());
                            city.setCityName(citiesInfo.getName());
                            city.setProvinceId(provinceId);
                            city.save();
                            dataList.add(citiesInfo.getName());
                            codeList.add(citiesInfo.getId());
                        }
                        finishTask();
                    }, throwable -> {
                        throwable.printStackTrace();
                        SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                    });
        } else {
            dataList.clear();
            codeList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
                codeList.add(city.getCityCode());
            }
            finishTask();
        }
    }

    private void queryCounties() {
        title.setText(cityName);
        String cityIdStr = String.valueOf(cityId);
        List<County> countyList = DataSupport.where("cityid=?", cityIdStr).find(County.class);
        if (countyList.isEmpty()) {
            RetrofitHelper.getGuoLinApi()
                    .getCounties(String.valueOf(provinceId), cityIdStr)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(this::showProgressDialog)
                    .doOnUnsubscribe(this::closeProgressDialog)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(countiesInfoList -> {
                        dataList.clear();
                        codeList.clear();
                        for (CountiesInfo countiesInfo : countiesInfoList) {
                            County county = new County();
                            int weatherId = Integer.valueOf(countiesInfo.getWeather_id().substring(2));
                            county.setWeatherId(weatherId);
                            county.setCountyName(countiesInfo.getName());
                            county.setCityId(cityId);
                            county.save();
                            dataList.add(countiesInfo.getName());
                            codeList.add(weatherId);
                        }
                        finishTask();
                    }, throwable -> {
                        throwable.printStackTrace();
                        SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                    });
        } else {
            dataList.clear();
            codeList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
                codeList.add(county.getWeatherId());
            }
            finishTask();
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
                    search(newText);
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
    private void showProgressDialog() {
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
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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
}
