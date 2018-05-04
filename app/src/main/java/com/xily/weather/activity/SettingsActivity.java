package com.xily.weather.activity;

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
import com.xily.weather.db.CityList;
import com.xily.weather.entity.BusInfo;
import com.xily.weather.rx.RxBus;
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                checkNotificationChannel();
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkNotificationChannel();
        }
        preferenceUtil.put("rain", isChecked);
    }

    @OnCheckedChanged(R.id.st_4)
    void setAlarm(boolean isChecked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkNotificationChannel();
        }
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

    @OnClick(R.id.st_8)
    void setBgImg() {
        checkPermission(1);
    }

    @OnClick(R.id.st_9)
    void setNavImg() {
        checkPermission(2);
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
                            preferenceUtil.put("bgImgPath", imagePath);
                        } else {
                            preferenceUtil.put("navImgPath", imagePath);
                        }
                        BusInfo busInfo = new BusInfo();
                        busInfo.setStatus(1);
                        RxBus.getInstance().post(busInfo);
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
        if (!preferenceUtil.get("notificationChannelCreated", false)) {
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
        preferenceUtil.put("notificationChannelCreated", true);
    }
}
