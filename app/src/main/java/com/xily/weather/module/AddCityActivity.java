package com.xily.weather.module;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.db.City;
import com.xily.weather.db.CityList;
import com.xily.weather.db.County;
import com.xily.weather.db.Province;
import com.xily.weather.entity.CitiesInfo;
import com.xily.weather.entity.CountiesInfo;
import com.xily.weather.entity.ProvincesInfo;
import com.xily.weather.network.RetrofitHelper;
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
    @BindView(R.id.progress)
    ProgressBar progressBar;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Integer> codeList = new ArrayList<>();
    private int level = 0;
    private int provinceId;
    private String provinceName;
    private int cityId;
    private String cityName;
    private int WeatherId;
    private String contryName;

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
            switch (level) {
                case 1:
                    provinceId = codeList.get(i);
                    provinceName = dataList.get(i);
                    break;
                case 2:
                    cityId = codeList.get(i);
                    cityName = dataList.get(i);
                    break;
                case 3:
                    WeatherId = codeList.get(i);
                    contryName = dataList.get(i);
                    break;
                default:
                    break;
            }
            loadData();
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
            case 3:
                if (DataSupport.where("weatherid=?", String.valueOf(WeatherId)).find(CityList.class).isEmpty()) {
                    CityList cityList = new CityList();
                    cityList.setCityName(contryName);
                    cityList.setWeatherId(WeatherId);
                    cityList.save();
                    ToastUtil.ShortToast("添加成功!");
                    setResult(1);
                    finish();
                } else {
                    SnackbarUtil.showMessage(getWindow().getDecorView(), "该城市已经被添加过!");
                }
            default:
                break;
        }
    }

    private void queryProvinces() {
        title.setText("中国");
        List<Province> provinceList = DataSupport.findAll(Province.class);
        if (provinceList.isEmpty()) {
            RetrofitHelper.getGuoLinApi()
                    .getProvinces()
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                    .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
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
                    .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                    .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
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
                    .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                    .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
