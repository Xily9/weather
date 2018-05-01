package com.xily.weather.module;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xily.weather.BuildConfig;
import com.xily.weather.R;
import com.xily.weather.base.RxBaseActivity;
import com.xily.weather.db.CityList;
import com.xily.weather.network.RetrofitHelper;
import com.xily.weather.utils.ColorUtil;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.PreferenceUtil;
import com.xily.weather.utils.ThemeUtil;
import com.xily.weather.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
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
    ViewPager viewPager;
    @BindView(R.id.li_dot)
    LinearLayout liDot;
    private AlertDialog dialog;
    private long exitTime;
    private PreferenceUtil data;
    private Subscription subscription;
    private List<CityList> cityList;
    private View[] views;

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
        data = new PreferenceUtil(PreferenceUtil.FILE_SETTING);
        if (!data.contains("permission"))
            checkPermission();
        else {
            initCities();
            initViewPager();
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
    }

    private void initCities() {
        cityList = DataSupport.findAll(CityList.class);
        views = new View[cityList.size()];
    }

    public void loadData(int pos) {

    }

    public void finishTask(int pos) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1)
                viewPager.setCurrentItem(data.getIntExtra("position", 0));
            else if (resultCode == 2)
                recreate();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.city:
                startActivityForResult(new Intent(this, CityActivity.class), 1);
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
                if (views[position] == null) {
                    View view = getLayoutInflater().inflate(R.layout.layout_item_viewpager, null);
                    SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.layout_swipe_refresh);
                    swipeRefreshLayout.setColorSchemeColors(ColorUtil.getAttrColor(MainActivity.this, R.attr.colorAccent));
                    loadData(position);
                    container.addView(view);
                    views[position] = view;
                }
                return views[position];
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
        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);
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
        PreferenceUtil setting = new PreferenceUtil(PreferenceUtil.FILE_SETTING);
        int checkedVersion = setting.get("checkedVersion", 0);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle("权限申请")
                    .setMessage("为了保证程序的正常运行,需要申请以下权限:\n" +
                            "网络定位：通过网络对您的手机进行定位\n" +
                            "需要用您的定位信息用于获取您的当前城市信息")
                    .setPositiveButton("立刻授权", (a, b) -> {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }).setNegativeButton("取消", (a, b) -> {
                data.put("permission", false);
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
                    data.put("permission", true);
                } else {
                    data.put("permission", false);
                    ToastUtil.ShortToast("您已取消权限申请,定位功能将不可用,如有需要,请到设置中开启");
                }
                initCities();
                initViewPager();
                break;
            default:
                break;
        }
    }

}
