package com.xily.weather.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.contract.SettingsContract;
import com.xily.weather.model.bean.BusBean;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.presenter.SettingsPresenter;
import com.xily.weather.rx.RxBus;
import com.xily.weather.service.WeatherService;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsActivity extends RxBaseActivity<SettingsPresenter> implements SettingsContract.View {
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
    @BindView(R.id.st_10)
    Switch st10;
    private LocalBroadcastManager localBroadcastManager;
    private List<CityListBean> cityLists;

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        cityLists = mPresenter.getCityLists();
        initItem();
    }

    private void initItem() {
        st1.setChecked(mPresenter.getNotification());
        int cityId = mPresenter.getNotificationId();
        for (CityListBean cityList : cityLists) {
            if (cityList.getId() == cityId) {
                cityName.setText(cityList.getCityName());
                break;
            }
        }
        st3.setChecked(mPresenter.getRain());
        st4.setChecked(mPresenter.getAlarm());
        st5.setChecked(mPresenter.getAutoUpdate());
        st10.setChecked(mPresenter.getCheckUpdate());
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
        mPresenter.setNotification(isChecked);
        if (isChecked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                checkNotificationChannel();
            }
            int cityId = mPresenter.getNotificationId();
            boolean check = false;
            for (CityListBean cityList : cityLists) {
                if (cityList.getId() == cityId) {
                    check = true;
                    cityName.setText(cityList.getCityName());
                    break;
                }
            }
            if (!check && !cityLists.isEmpty()) {
                mPresenter.setNotificationId(cityLists.get(0).getId());
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
                        mPresenter.setNotificationId(cityLists.get(which).getId());
                        cityName.setText(cityLists.get(which).getCityName());
                        sendLocalBroadcast();
                    }).show();
        }
    }

    @OnCheckedChanged(R.id.st_3)
    void setRain(boolean isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkNotificationChannel();
        }
        mPresenter.setRain(isChecked);
    }

    @OnCheckedChanged(R.id.st_4)
    void setAlarm(boolean isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkNotificationChannel();
        }
        mPresenter.setAlarm(isChecked);
    }

    @OnCheckedChanged(R.id.st_5)
    void setAutoUpdate(boolean isChecked) {
        mPresenter.setAutoUpdate(isChecked);
        sendLocalBroadcast();
    }

    @OnCheckedChanged(R.id.st_6)
    void setNightNoUpdate(boolean isChecked) {
        mPresenter.setNightNoUpdate(isChecked);
    }

    @OnClick(R.id.st_8)
    void setBgImg() {
        String[] choices = new String[]{"默认背景", "Bing每日一图", "自定义"};
        new AlertDialog.Builder(this)
                .setTitle("背景图设置")
                .setSingleChoiceItems(choices, mPresenter.getBgMode(), (dialog, which) -> {
                    if (which == 2) {
                        checkPermission(1);
                    } else {
                        mPresenter.setBgMode(which);
                        BusBean busBean = new BusBean();
                        busBean.setStatus(1);
                        RxBus.getInstance().post(busBean);
                    }
                    dialog.dismiss();
                })
                .show();
    }

    @OnClick(R.id.st_9)
    void setNavImg() {
        String[] choices = new String[]{"默认背景", "自定义"};
        new AlertDialog.Builder(this)
                .setTitle("侧栏顶部背景设置")
                .setSingleChoiceItems(choices, mPresenter.getNavMode(), (dialog, which) -> {
                    if (which == 1) {
                        checkPermission(2);
                    } else {
                        mPresenter.setNavMode(which);
                        BusBean busBean = new BusBean();
                        busBean.setStatus(1);
                        RxBus.getInstance().post(busBean);
                    }
                    dialog.dismiss();
                })
                .show();
    }

    @OnCheckedChanged(R.id.st_10)
    void setCheckUpdate(boolean isChecked) {
        mPresenter.setCheckUpdate(isChecked);
    }

    private void checkPermission(int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openAlbum(requestCode);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("权限申请")
                        .setMessage("为了能够自定义图片,需要申请以下权限:\n" +
                                "文件存取:用于读取自定义图片信息")
                        .setPositiveButton("立刻授权", (a, b) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode))
                        .setNegativeButton("取消", (a, b) -> ToastUtil.ShortToast("您已取消权限申请,不能自定义图片"))
                        .setCancelable(false)
                        .show();
            } else {
                openAlbum(requestCode);
            }
        }
    }

    private String getImagePath(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        if (imagePath != null) {
            return imagePath;
        } else {
            return null;
        }
    }

    private void openAlbum(int requestCode) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
            case 2:
                if (resultCode == RESULT_OK) {
                    String imagePath = getImagePath(data);
                    if (TextUtils.isEmpty(imagePath)) {
                        ToastUtil.ShortToast("获取图片失败!");
                    } else {
                        if (requestCode == 1) {
                            mPresenter.setBgMode(2);
                            mPresenter.setBgImgPath(imagePath);
                        } else {
                            mPresenter.setBgMode(1);
                            mPresenter.setNavImgPath(imagePath);
                        }
                        BusBean busBean = new BusBean();
                        busBean.setStatus(1);
                        RxBus.getInstance().post(busBean);
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum(requestCode);
                } else {
                    ToastUtil.ShortToast("您已取消权限申请,不能自定义图片");
                }
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

    @TargetApi(Build.VERSION_CODES.O)
    private void checkNotificationChannel() {
        if (!mPresenter.getNotificationChannelCreated()) {
            String channelId = "weather";
            String channelName = "天气通知";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
        mPresenter.setNotificationChannelCreated(true);
    }

    @Override
    public void initInject() {
        getActivityComponent().inject(this);
    }
}
