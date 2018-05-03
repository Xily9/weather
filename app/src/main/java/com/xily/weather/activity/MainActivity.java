package com.xily.weather.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.adapter.ForecastAdapter;
import com.xily.weather.adapter.Weather3Adapter;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.db.CityList;
import com.xily.weather.entity.BaiduLocationInfo;
import com.xily.weather.entity.BusInfo;
import com.xily.weather.entity.SearchInfo;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.network.RetrofitHelper;
import com.xily.weather.rx.RxBus;
import com.xily.weather.service.WeatherService;
import com.xily.weather.utils.ColorUtil;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.PreferenceUtil;
import com.xily.weather.utils.SnackbarUtil;
import com.xily.weather.utils.ThemeUtil;
import com.xily.weather.utils.ToastUtil;
import com.xily.weather.widget.BounceBackViewPager;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends RxBaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView title;
    @BindView(R.id.updateTime)
    TextView updateTime;
    @BindView(R.id.viewPager)
    BounceBackViewPager viewPager;
    @BindView(R.id.li_dot)
    LinearLayout liDot;
    @BindView(R.id.empty)
    TextView empty;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    private AlertDialog dialog;
    private long exitTime;
    private PreferenceUtil data;
    private Subscription subscription;
    private List<CityList> cityList;
    private boolean isRefreshing;
    private int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DeviceUtil.setStatusBarUpper(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initToolBar();
        initNavigationView();
        data = PreferenceUtil.getInstance();
        initRxBus();
        if (!data.contains("permission"))
            checkPermission();
        else {
            initCities();
        }
    }

    @Override
    public void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("");
    }

    private void initRxBus() {
        RxBus.getInstance()
                .toObservable(BusInfo.class)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(busInfo -> {
                    if (busInfo.getStatus() == 1)
                        recreate();
                    else if (busInfo.getStatus() == 2)
                        viewPager.setCurrentItem(busInfo.getPosition());
                });
    }

    private void startService() {
        if (!DeviceUtil.isServiceRunning(BuildConfig.APPLICATION_ID + ".service.WeatherService")) {
            Intent startIntent = new Intent(this, WeatherService.class);
            startService(startIntent);
        }
    }

    private void setPos(int pos) {
        liDot.removeAllViews();
        currentPos = pos;
        LogUtil.d("tag", String.valueOf(pos));
        for (int i = 0; i < cityList.size(); i++) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = DeviceUtil.dp2px(1);
            layoutParams.rightMargin = DeviceUtil.dp2px(1);
            textView.setText(".");
            if (i == pos)
                textView.setTextColor(Color.parseColor("#ffffff"));
            else
                textView.setTextColor(Color.parseColor("#aaaaaa"));
            textView.setTextSize(40);
            textView.setRotation(180);
            liDot.addView(textView, layoutParams);
        }
        subscription = Observable.just(null)
                .delay(1000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    liDot.setVisibility(View.GONE);
                    liDot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_out));
                });
        title.setText(cityList.get(pos).getCityName());
        if (!TextUtils.isEmpty(cityList.get(pos).getUpdateTimeStr()))
            updateTime.setText(cityList.get(pos).getUpdateTimeStr() + "更新");
    }

    private void initCities() {
        cityList = DataSupport.findAll(CityList.class);
        if (!cityList.isEmpty()) {
            initViewPager();
            setPos(0);
            startService();
        } else {
            if (data.get("permission", false)) {
                empty.setVisibility(View.VISIBLE);
            }
        }
        checkVersion();
    }

    @OnClick(R.id.empty)
    void findLocation() {
        Location location = DeviceUtil.getLocation();
        String str = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
        LogUtil.d("location", str);
        RetrofitHelper.getBaiduLocationApi()
                .getAddress(str)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .flatMap(responseBody -> {
                    try {
                        String jsonStr = responseBody.string();
                        String addrJson = jsonStr.substring(29, jsonStr.length() - 1);
                        LogUtil.d("json", addrJson);
                        BaiduLocationInfo baiduLocationInfo = new Gson().fromJson(addrJson, BaiduLocationInfo.class);
                        if (baiduLocationInfo.getStatus() == 0) {
                            String address = baiduLocationInfo.getResult().getAddressComponent().getDistrict();
                            return RetrofitHelper.getHeWeatherApi()
                                    .search(address.substring(0, address.length() - 1))
                                    .compose(bindToLifecycle())
                                    .subscribeOn(Schedulers.io());
                        } else {
                            return Observable.error(new RuntimeException("定位失败!"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("定位失败!");
                    }
                })
                .doOnSubscribe(() -> progressBar.setVisibility(View.VISIBLE))
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchInfo -> {
                    SearchInfo.HeWeather6Bean heWeather6Bean = searchInfo.getHeWeather6().get(0);
                    if (heWeather6Bean.getStatus().equals("ok") && !heWeather6Bean.getBasic().isEmpty()) {
                        CityList cityList = new CityList();
                        cityList.setCityName(heWeather6Bean.getBasic().get(0).getLocation());
                        cityList.setWeatherId(Integer.valueOf(heWeather6Bean.getBasic().get(0).getCid().substring(2)));
                        cityList.save();
                        empty.setVisibility(View.GONE);
                        initCities();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    public void loadData(int pos, View view) {
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.layout_swipe_refresh);
        Observable<WeatherInfo> offline = Observable.just(null)
                .map(o -> {
                    String data = cityList.get(pos).getWeatherData();
                    if (isRefreshing || System.currentTimeMillis() - cityList.get(pos).getUpdateTime() > 1000 * 60 * 60 || TextUtils.isEmpty(data)) {
                        return null;
                    } else {
                        return new Gson().fromJson(data, WeatherInfo.class);
                    }
                });
        Observable<WeatherInfo> online = RetrofitHelper.getMeiZuWeatherApi()
                .getWeather(String.valueOf(cityList.get(pos).getWeatherId()))
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(() -> swipeRefreshLayout.setRefreshing(true))
                .doOnUnsubscribe(() -> {
                    isRefreshing = false;
                    swipeRefreshLayout.setRefreshing(false);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(weatherInfo -> {
                    CityList cityListUpdate = new CityList();
                    cityListUpdate.setWeatherData(new Gson().toJson(weatherInfo));
                    cityListUpdate.setUpdateTime(System.currentTimeMillis());
                    cityListUpdate.setUpdateTimeStr(weatherInfo.getValue().get(0).getRealtime().getTime().substring(11, 16));
                    cityListUpdate.update(cityList.get(pos).getId());
                    cityList.set(pos, DataSupport.find(CityList.class, cityList.get(pos).getId()));
                    Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".LOCAL_BROADCAST");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    Intent intent2 = new Intent("com.xily.weather.WEATHER_BROADCAST");
                    sendBroadcast(intent2);
                });
        Observable.concat(offline, online)
                .takeFirst(weatherInfo -> weatherInfo != null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherInfo -> finishTask(pos, view, weatherInfo), throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                });
    }

    public void finishTask(int pos, View view, WeatherInfo weatherInfo) {
        WeatherInfo.ValueBean valueBean = weatherInfo.getValue().get(0);
        if (currentPos == pos)
            updateTime.setText(cityList.get(pos).getUpdateTimeStr() + "更新");
        ((TextView) view.findViewById(R.id.temperature)).setText(valueBean.getRealtime().getTemp());
        ((TextView) view.findViewById(R.id.weather)).setText(valueBean.getRealtime().getWeather());
        ((TextView) view.findViewById(R.id.air)).setText(valueBean.getPm25().getAqi() + " " + valueBean.getPm25().getQuality());
        ((TextView) view.findViewById(R.id.wet)).setText(valueBean.getRealtime().getSD() + "%");
        ((TextView) view.findViewById(R.id.wind)).setText(valueBean.getRealtime().getWD() + valueBean.getRealtime().getWS());
        ((TextView) view.findViewById(R.id.sendibleTemp)).setText(valueBean.getRealtime().getSendibleTemp() + "°C");
        if (!valueBean.getAlarms().isEmpty()) {
            Button button = view.findViewById(R.id.alarm);
            button.setVisibility(View.VISIBLE);
            List<String> list = new ArrayList<>();
            for (WeatherInfo.ValueBean.AlarmsBean alarmsBean : valueBean.getAlarms()) {
                list.add(alarmsBean.getAlarmTypeDesc() + "预警");
            }
            StringBuilder stringBuilder = new StringBuilder();
            boolean isFirst = true;
            for (String str : list) {
                if (isFirst) {
                    stringBuilder.append(str);
                    isFirst = false;
                } else {
                    stringBuilder.append(',');
                    stringBuilder.append(str);
                }
            }
            button.setText(stringBuilder.toString());
            button.setOnClickListener(v -> {
                Intent intent = new Intent(this, AlarmActivity.class);
                intent.putExtra("id", cityList.get(pos).getId());
                startActivity(intent);
            });
        }

        RecyclerView recyclerView = view.findViewById(R.id.forecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {{
            setOrientation(LinearLayoutManager.HORIZONTAL);
        }});
        recyclerView.setAdapter(new ForecastAdapter(this, valueBean.getWeathers()));
        RecyclerView recyclerView1 = view.findViewById(R.id.weather3);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this) {{
            setOrientation(LinearLayoutManager.HORIZONTAL);
        }});
        recyclerView1.setAdapter(new Weather3Adapter(this, valueBean.getWeatherDetailsInfo().getWeather3HoursDetailsInfos()));
        ((TextView) view.findViewById(R.id.pm25)).setText(valueBean.getPm25().getPm25());
        ((TextView) view.findViewById(R.id.pm10)).setText(valueBean.getPm25().getPm10());
        ((TextView) view.findViewById(R.id.so2)).setText(valueBean.getPm25().getSo2());
        ((TextView) view.findViewById(R.id.no2)).setText(valueBean.getPm25().getNo2());
        ((TextView) view.findViewById(R.id.co)).setText(valueBean.getPm25().getCo());
        ((TextView) view.findViewById(R.id.o3)).setText(valueBean.getPm25().getO3());
        List<WeatherInfo.ValueBean.IndexesBean> indexesBeans = valueBean.getIndexes();
        ((TextView) view.findViewById(R.id.name1)).setText(indexesBeans.get(0).getName());
        ((TextView) view.findViewById(R.id.value1)).setText(indexesBeans.get(0).getLevel());
        ((TextView) view.findViewById(R.id.name2)).setText(indexesBeans.get(1).getName());
        ((TextView) view.findViewById(R.id.value2)).setText(indexesBeans.get(1).getLevel());
        ((TextView) view.findViewById(R.id.name3)).setText(indexesBeans.get(2).getName());
        ((TextView) view.findViewById(R.id.value3)).setText(indexesBeans.get(2).getLevel());
        ((TextView) view.findViewById(R.id.name4)).setText(indexesBeans.get(3).getName());
        ((TextView) view.findViewById(R.id.value4)).setText(indexesBeans.get(3).getLevel());
        ((TextView) view.findViewById(R.id.name5)).setText(indexesBeans.get(4).getName());
        ((TextView) view.findViewById(R.id.value5)).setText(indexesBeans.get(4).getLevel());
        ((TextView) view.findViewById(R.id.name6)).setText(indexesBeans.get(5).getName());
        ((TextView) view.findViewById(R.id.value6)).setText(indexesBeans.get(5).getLevel());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.city:
                startActivity(new Intent(this, CityActivity.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_theme:
                LinearLayout linearLayout = new LinearLayout(this);
                int padding = DeviceUtil.dp2px(20);
                linearLayout.setPadding(padding, padding, padding, padding);
                linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                FrameLayout frameLayout = new FrameLayout(this);
                int[] colorList = ThemeUtil.getColorList();
                int theme = ThemeUtil.getTheme();
                for (int i = 0; i < colorList.length; i++) {
                    Button button = new Button(this);
                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setColor(getResources().getColor(colorList[i]));
                    gradientDrawable.setShape(GradientDrawable.OVAL);
                    button.setBackground(gradientDrawable);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DeviceUtil.dp2px(40), DeviceUtil.dp2px(40));
                    layoutParams.leftMargin = DeviceUtil.dp2px(50) * (i % 5) + DeviceUtil.dp2px(5);
                    layoutParams.topMargin = DeviceUtil.dp2px(50) * (i / 5) + DeviceUtil.dp2px(5);
                    int finalI = i;
                    button.setOnClickListener(v -> {
                        data.put("theme", finalI);
                        dialog.dismiss();
                        recreate();
                    });
                    if (i == theme) {
                        button.setText("✔");
                        button.setTextColor(Color.parseColor("#ffffff"));
                        button.setTextSize(15);
                        button.setGravity(Gravity.CENTER);
                    }
                    frameLayout.addView(button, layoutParams);
                }
                linearLayout.addView(frameLayout);
                dialog = new AlertDialog.Builder(MainActivity.this).setTitle("设置主题").setView(linearLayout).show();
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNavigationView() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        View sideMenuView = mNavigationView.inflateHeaderView(R.layout.layout_side_menu);
    }

    private void initViewPager() {
        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return cityList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = getLayoutInflater().inflate(R.layout.layout_item_viewpager, null);
                SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.layout_swipe_refresh);
                swipeRefreshLayout.setColorSchemeColors(ColorUtil.getAttrColor(MainActivity.this, R.attr.colorAccent));
                swipeRefreshLayout.setOnRefreshListener(() -> {
                    isRefreshing = true;
                    loadData(position, view);
                });
                container.addView(view);
                loadData(position, view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPos(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 1) {
                    if (subscription != null && !subscription.isUnsubscribed()) {
                        subscription.unsubscribe();
                    }
                    liDot.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (System.currentTimeMillis() - exitTime < 2000) {
            super.onBackPressed();
        } else {
            ToastUtil.ShortToast("再按一次返回键退出程序");
            exitTime = System.currentTimeMillis();
        }
    }

    private void checkVersion() {
        int version = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        int checkedVersion = data.get("checkedVersion", 0);
        RetrofitHelper.getMyApi()
                .checkVersion()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(versionInfo -> {
                    LogUtil.d("test", "yes");
                    if (versionInfo.getStatus() == 0) {
                        com.xily.weather.entity.versionInfo.DataBean dataBean = versionInfo.getData();
                        if (version < dataBean.getVersion() && dataBean.getVersion() > checkedVersion) {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this)
                                    .setTitle("版本升级")
                                    .setMessage("当前版本：" + versionName + "\n" +
                                            "新版本：" + dataBean.getVersion_name() + "\n" +
                                            "更新时间：" + dataBean.getTime() + "\n" +
                                            "更新内容：" + dataBean.getText())
                                    .setPositiveButton("升级", (a, b) -> update(dataBean.getDownload_url()))
                                    .setCancelable(false);
                            if (dataBean.getVersion_force_update_under() <= version) {
                                builder.setNegativeButton("取消", null);
                                builder.setNeutralButton("该版本不再提示", (a, b) -> data.put("checkedVersion", dataBean.getVersion()));
                            }
                            builder.show();
                        }
                    }
                }, Throwable::printStackTrace);

    }

    private void update(String url) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("下载中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
        String filePath = DeviceUtil.getCacheDir() + "/weather.apk";
        Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept-Encoding", "identity")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    inputStream = response.body().byteStream();
                    long length = response.body().contentLength();
                    LogUtil.d("length", String.valueOf(length));
                    outputStream = new FileOutputStream(filePath);
                    byte data[] = new byte[1024];
                    subscriber.onNext(0);
                    long total = 0;
                    int count;
                    while ((count = inputStream.read(data)) != -1) {
                        total += count;
                        // 返回当前实时进度
                        subscriber.onNext((int) (total * 100 / length));
                        outputStream.write(data, 0, count);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                }
            } catch (IOException e) {
                subscriber.onError(e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            subscriber.onCompleted();
        })
                .onBackpressureBuffer()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progressDialog::setProgress, Throwable::printStackTrace, () -> {
                    progressDialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(filePath);
                    Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    startActivity(intent);
                });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            data.put("permission", true);
            initCities();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("权限申请")
                        .setMessage("为了保证程序的正常运行,需要申请以下权限:\n" +
                                "网络定位:用于获取您的当前城市信息")
                        .setPositiveButton("立刻授权", (a, b) -> {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }).setNegativeButton("取消", (a, b) -> {
                    data.put("permission", false);
                    ToastUtil.ShortToast("您已取消权限申请,定位功能将不可用,如有需要,请到设置中开启");
                }).setCancelable(false)
                        .show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    data.put("permission", true);
                } else {
                    data.put("permission", false);
                    ToastUtil.ShortToast("您已取消权限申请,定位功能将不可用,如有需要,请到设置中开启");
                }
                initCities();
                break;
            default:
                break;
        }
    }

}
