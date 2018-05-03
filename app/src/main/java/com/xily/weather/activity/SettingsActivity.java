package com.xily.weather.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.db.CityList;
import com.xily.weather.service.WeatherService;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.PreferenceUtil;
import com.xily.weather.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.st_1)
    Switch st1;
    @BindView(R.id.cityName)
    TextView cityName;
    @BindView(R.id.st_3)
    Switch st3;
    @BindView(R.id.st_4)
    Switch st4;
    @BindView(R.id.st_5)
    Switch st5;
    @BindView(R.id.st_6)
    Switch st6;
    @BindView(R.id.location)
    TextView location;
    private PreferenceUtil preferenceUtil;
    private LocalBroadcastManager localBroadcastManager;
    private List<CityList> cityLists;

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        preferenceUtil = PreferenceUtil.getInstance();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        cityLists = DataSupport.findAll(CityList.class);
        initItem();
    }

    private void initItem() {
        st1.setChecked(preferenceUtil.get("notification", false));
        int cityId = preferenceUtil.get("notificationId", 0);
        for (CityList cityList : cityLists) {
            if (cityList.getId() == cityId) {
                cityName.setText(cityList.getCityName());
                break;
            }
        }
        st3.setChecked(preferenceUtil.get("rain", false));
        st4.setChecked(preferenceUtil.get("alarm", false));
        st5.setChecked(preferenceUtil.get("isAutoUpdate", false));
        st6.setChecked(preferenceUtil.get("nightNoUpdate", false));
        location.setText(preferenceUtil.get("permission", false) ? "已申请" : "未申请");
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        setTitle("设置");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void sendLocalBroadcast() {
        Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".LOCAL_BROADCAST");
        localBroadcastManager.sendBroadcast(intent);
    }

    @OnCheckedChanged(R.id.st_1)
    void setNotification(boolean isChecked) {
        preferenceUtil.put("notification", isChecked);
        if (isChecked) {
            int cityId = preferenceUtil.get("notificationId", 0);
            boolean check = false;
            for (CityList cityList : cityLists) {
                if (cityList.getId() == cityId) {
                    check = true;
                    cityName.setText(cityList.getCityName());
                    break;
                }
            }
            if (!check && !cityLists.isEmpty()) {
                preferenceUtil.put("notificationId", cityLists.get(0).getId());
                cityName.setText(cityLists.get(0).getCityName());
            }
        }
        if (DeviceUtil.isServiceRunning(BuildConfig.APPLICATION_ID + ".service.WeatherService")) {
            sendLocalBroadcast();
        } else {
            if (isChecked) {
                Intent startIntent = new Intent(this, WeatherService.class);
                startService(startIntent);
            }
        }
    }

    @OnClick(R.id.st_2)
    void selectCity() {
        if (!cityLists.isEmpty()) {
            String[] str = new String[cityLists.size()];
            for (int i = 0; i < cityLists.size(); i++) {
                str[i] = cityLists.get(i).getCityName();
            }
            new AlertDialog.Builder(this)
                    .setTitle("城市选择")
                    .setItems(str, (dialog, which) -> {
                        preferenceUtil.put("notificationId", cityLists.get(which).getId());
                        cityName.setText(cityLists.get(which).getCityName());
                        sendLocalBroadcast();
                    }).show();
        }
    }

    @OnCheckedChanged(R.id.st_3)
    void setRain(boolean isChecked) {
        preferenceUtil.put("rain", isChecked);
    }

    @OnCheckedChanged(R.id.st_4)
    void setAlarm(boolean isChecked) {
        preferenceUtil.put("alarm", isChecked);
    }

    @OnCheckedChanged(R.id.st_5)
    void setAutoUpdate(boolean isChecked) {
        preferenceUtil.put("isAutoUpdate", isChecked);
        sendLocalBroadcast();
    }

    @OnCheckedChanged(R.id.st_6)
    void setNightNoUpdate(boolean isChecked) {
        preferenceUtil.put("nightNoUpdate", isChecked);
    }

    @OnClick(R.id.st_7)
    void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("权限申请")
                    .setMessage("为了保证程序的正常运行,需要申请以下权限:\n" +
                            "网络定位:用于获取您的当前城市信息")
                    .setPositiveButton("立刻授权", (a, b) -> {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }).setNegativeButton("取消", (a, b) -> {
                preferenceUtil.put("permission", false);
                ToastUtil.ShortToast("您已取消权限申请,定位功能将不可用,如有需要,请到设置中开启");
            }).setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    preferenceUtil.put("permission", true);
                } else {
                    preferenceUtil.put("permission", false);
                    ToastUtil.ShortToast("您已取消权限申请,定位功能将不可用,如有需要,请到设置中开启");
                }
                location.setText(preferenceUtil.get("permission", false) ? "已申请" : "未申请");
                break;
            default:
                break;
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
