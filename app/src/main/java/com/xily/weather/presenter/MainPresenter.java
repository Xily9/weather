package com.xily.weather.presenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xily.weather.BuildConfig;
import com.xily.weather.app.App;
import com.xily.weather.base.RxBasePresenter;
import com.xily.weather.contract.MainContract;
import com.xily.weather.model.DataManager;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.SearchBean;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.network.OkHttpHelper;
import com.xily.weather.model.network.RetrofitHelper;
import com.xily.weather.rx.RxHelper;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.LocationUtil;
import com.xily.weather.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter extends RxBasePresenter<MainContract.View> implements MainContract.Presenter {
    App mContext;
    DataManager mDataManager;

    @Inject
    public MainPresenter(App context, DataManager dataManager) {
        mContext = context;
        mDataManager = dataManager;
    }

    @Override
    public List<CityListBean> getCityList() {
        return mDataManager.getCityList();
    }

    @Override
    public void checkVersion() {
        int version = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        int checkedVersion = mDataManager.getCheckedVersion();
        RetrofitHelper.getWeatherApi()
                .checkVersion()
                .compose(mView.bindToLifecycle())
                .compose(RxHelper.applySchedulers())
                .subscribe(versionBean -> {
                    LogUtil.d("test", "yes");
                    if (versionBean.getStatus() == 0) {
                        VersionBean.DataBean dataBean = versionBean.getData();
                        if (version < dataBean.getVersion() && dataBean.getVersion() > checkedVersion) {
                            mView.showUpdateDialog(versionName, version, dataBean);
                        }
                    }
                }, Throwable::printStackTrace);
    }

    @Override
    public void update(String url) {
        mView.initProgress();
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
                .compose(mView.bindToLifecycle())
                .compose(RxHelper.applySchedulers())
                .subscribe(integer -> mView.showDownloadProgress(integer), Throwable::printStackTrace, () -> {
                    mView.closeProgress();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(filePath);
                    Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    mContext.startActivity(intent);
                });
    }

    @Override
    public void getBingPic(String day) {
        OkHttpHelper.get("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mView.setBingPic(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String url = response.body().string();
                if (!TextUtils.isEmpty(url)) {
                    mDataManager.setBingPicUrl(url);
                    mDataManager.setBingPicTime(day);
                }
                mView.setBingPic(url);
            }
        });
    }

    @Override
    public void findLocation() {
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
                        .compose(mView.bindToLifecycle())
                        .subscribeOn(Schedulers.io()))
                .doOnSubscribe(() -> {
                    mView.setProgressBar(View.VISIBLE);
                    mView.setEmptyView(View.GONE);
                })
                .doOnUnsubscribe(() -> mView.setProgressBar(View.GONE))
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
                        mView.initCities();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    mView.showErrorMsg(throwable.getMessage());
                    mView.setEmptyView(View.VISIBLE);
                });
    }

    @Override
    public void setCheckVersion(int checkVersion) {
        mDataManager.setCheckVersion(checkVersion);
    }

    @Override
    public boolean getCheckUpdate() {
        return mDataManager.getCheckUpdate();
    }

    @Override
    public int getBgMode() {
        return mDataManager.getBgMode();
    }

    @Override
    public String getBingPicTime() {
        return mDataManager.getBingPicTime();
    }

    @Override
    public String getBingPicUrl() {
        return mDataManager.getBingPicUrl();
    }

    @Override
    public String getBgImgPath() {
        return mDataManager.getBgImgPath();
    }

    @Override
    public String getNavImgPath() {
        return mDataManager.getNavImgPath();
    }

    @Override
    public int getNavMode() {
        return mDataManager.getNavMode();
    }

    @Override
    public void loadBingPic(String url) {
        Glide.with(mContext).load(url).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mView.setBackground(resource);
            }
        });
    }
}
