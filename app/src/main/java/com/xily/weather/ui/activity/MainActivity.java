package com.xily.weather.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.base.BaseActivity;
import com.xily.weather.contract.MainContract;
import com.xily.weather.model.bean.BusBean;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.SearchBean;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.network.OkHttpHelper;
import com.xily.weather.model.network.RetrofitHelper;
import com.xily.weather.presenter.MainPresenter;
import com.xily.weather.rx.RxBus;
import com.xily.weather.rx.RxHelper;
import com.xily.weather.service.WeatherService;
import com.xily.weather.ui.adapter.HomePagerAdapter;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.LocationUtil;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.PreferenceUtil;
import com.xily.weather.utils.SnackbarUtil;
import com.xily.weather.utils.ThemeUtil;
import com.xily.weather.utils.ToastUtil;
import com.xily.weather.widget.BounceBackViewPager;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity<MainPresenter> implements NavigationView.OnNavigationItemSelectedListener, MainContract.View {
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewPager)
    BounceBackViewPager viewPager;
    @BindView(R.id.li_dot)
    LinearLayout liDot;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    private long exitTime;
    private Subscription subscription;
    private List<CityListBean> cityList;
    private ProgressDialog progressDialog;
    @Inject
    PreferenceUtil data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DeviceUtil.setStatusBarUpper(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadBackgroundImage();
        initToolBar();
        initNavigationView();
        initRxBus();
        initCities();
        if (data.get("checkUpdate", true))
            mPresenter.checkVersion();
    }

    private void loadBackgroundImage() {
        int bgMode = data.get("bgMode", 0);
        switch (bgMode) {
            case 0:
                setDefaultPic();
                break;
            case 1:
                Calendar calendar = Calendar.getInstance();
                String day = String.valueOf(calendar.get(Calendar.YEAR)) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH);
                String bingPicTime = data.get("bingPicTime", "");
                if (!bingPicTime.equals(day)) {
                    OkHttpHelper.get("http://guolin.tech/api/bing_pic", new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            setBingPic(null);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String url = response.body().string();
                            if (!TextUtils.isEmpty(url)) {
                                data.put("bingPicUrl", url);
                                data.put("bingPicTime", day);
                            }
                            setBingPic(url);
                        }
                    });
                } else {
                    setBingPic(null);
                }
                break;
            case 2:
                String imagePath = data.get("bgImgPath", "");
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                mDrawerLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                break;
        }
    }

    private void setBingPic(String url) {
        if (TextUtils.isEmpty(url)) {
            url = data.get("bingPicUrl", "");
            if (url.isEmpty()) {
                setDefaultPic();
                return;
            }
        }
        String finalUrl = url;
        runOnUiThread(() -> Glide.with(this).load(finalUrl).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mDrawerLayout.setBackground(resource);
            }
        }));
    }

    private void setDefaultPic() {
        mDrawerLayout.setBackgroundResource(R.drawable.bg);
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
                .toObservable(BusBean.class)
                .compose(bindToLifecycle())
                .compose(RxHelper.applySchedulers())
                .subscribe(busBean -> {
                    if (busBean.getStatus() == 1)
                        recreate();
                    else if (busBean.getStatus() == 2)
                        viewPager.setCurrentItem(busBean.getPosition());
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
                textView.setTextColor(Color.parseColor("#bbbbbbbb"));
            textView.setTextSize(40);
            textView.setRotation(180);
            liDot.addView(textView, layoutParams);
        }
    }

    private void activeTimer() {
        subscription = Observable.just(null)
                .delay(1000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .compose(RxHelper.applySchedulers())
                .subscribe(o -> {
                    liDot.setVisibility(View.GONE);
                    liDot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_out));
                });
    }

    private void initCities() {
        cityList = DataSupport.findAll(CityListBean.class);
        if (!cityList.isEmpty()) {
            initViewPager();
            setPos(0);
            activeTimer();
            startService();
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.empty)
    void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            findLocation();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("权限申请")
                        .setMessage("为了能够正常定位,需要申请定位权限")
                        .setPositiveButton("立刻授权", (a, b) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1))
                        .setNegativeButton("取消", (a, b) -> ToastUtil.ShortToast("您已取消权限申请,无法定位!"))
                        .setCancelable(false)
                        .show();
            } else {
                findLocation();
            }
        }
    }

    void findLocation() {
        Observable.create((Observable.OnSubscribe<AMapLocation>) subscriber -> LocationUtil.getLocation(location -> {
            if (location == null) {
                subscriber.onError(new RuntimeException("获取定位信息失败!"));
            } else {
                if (location.getErrorCode() == 0) {
                    subscriber.onNext(location);
                    subscriber.onCompleted();
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    subscriber.onError(new RuntimeException("location Error, ErrCode:"
                            + location.getErrorCode() + ", errInfo:"
                            + location.getErrorInfo()));
                }
            }
        }))
                .flatMap(location -> RetrofitHelper.getWeatherApi()
                        .search(location.getDistrict().substring(0, location.getDistrict().length() - 1))
                        .compose(bindToLifecycle())
                        .subscribeOn(Schedulers.io()))
                .doOnSubscribe(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                })
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchBean -> {
                    SearchBean.HeWeather6Bean heWeather6Bean = searchBean.getHeWeather6().get(0);
                    if (heWeather6Bean.getStatus().equals("ok") && !heWeather6Bean.getBasic().isEmpty()) {
                        CityListBean cityList = new CityListBean();
                        cityList.setCityName(heWeather6Bean.getBasic().get(0).getLocation());
                        cityList.setWeatherId(Integer.valueOf(heWeather6Bean.getBasic().get(0).getCid().substring(2)));
                        cityList.save();
                        initCities();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    SnackbarUtil.showMessage(getWindow().getDecorView(), throwable.getMessage());
                    empty.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        Observable.timer(250, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .compose(RxHelper.applySchedulers())
                .subscribe(aLong -> {
                    switch (item.getItemId()) {
                        case R.id.city:
                            startActivity(new Intent(this, CityActivity.class));
                            break;
                        case R.id.nav_setting:
                            startActivity(new Intent(this, SettingsActivity.class));
                            break;
                        case R.id.nav_theme:
                            ThemeUtil.showSwitchThemeDialog(this);
                            break;
                        case R.id.nav_about:
                            startActivity(new Intent(this, AboutActivity.class));
                            break;
                    }
                });
        return true;
    }

    private void initNavigationView() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        View sideMenuView = mNavigationView.inflateHeaderView(R.layout.layout_side_menu);
        String imagePath = data.get("navImgPath", "");
        ImageView imageView = sideMenuView.findViewById(R.id.nav_image);
        if (data.get("navMode", 0) == 0 || TextUtils.isEmpty(imagePath)) {
            imageView.setMaxHeight(DeviceUtil.dp2px(200));
            imageView.setImageResource(R.drawable.bg);
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }

    private void initViewPager() {
        viewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager(), cityList));
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
                } else if (state == 0) {
                    activeTimer();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findLocation();
                } else {
                    ToastUtil.ShortToast("您已取消权限申请,无法定位!");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showUpdateDialog(String versionName, int version, VersionBean.DataBean dataBean) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("版本升级")
                .setMessage("当前版本：" + versionName + "\n" +
                        "新版本：" + dataBean.getVersion_name() + "\n" +
                        "更新时间：" + dataBean.getTime() + "\n" +
                        "更新内容：\n" + dataBean.getText())
                .setPositiveButton("升级", (a, b) -> mPresenter.update(dataBean.getDownload_url()))
                .setCancelable(false);
        if (dataBean.getVersion_force_update_under() <= version) {
            builder.setNegativeButton("取消", null);
            builder.setNeutralButton("该版本不再提示", (a, b) -> data.put("checkedVersion", dataBean.getVersion()));
        }
        builder.show();
    }

    @Override
    public void initProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("下载中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    @Override
    public void showDownloadProgress(int progress) {
        progressDialog.setProgress(progress);
    }

    @Override
    public void closeProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void showErrorMsg(String msg) {
        SnackbarUtil.showMessage(getWindow().getDecorView(), msg);
    }


}
