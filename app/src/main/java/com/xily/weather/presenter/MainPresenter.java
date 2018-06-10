package com.xily.weather.presenter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.xily.weather.BuildConfig;
import com.xily.weather.app.App;
import com.xily.weather.base.RxBasePresenter;
import com.xily.weather.contract.MainContract;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.network.RetrofitHelper;
import com.xily.weather.rx.RxHelper;
import com.xily.weather.utils.DeviceUtil;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.PreferenceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;

public class MainPresenter extends RxBasePresenter<MainContract.View> implements MainContract.Presenter {
    @Inject
    PreferenceUtil data;
    @Inject
    App mContext;
    @Inject
    public MainPresenter() {
    }

    @Override
    public void checkVersion() {
        int version = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        int checkedVersion = data.get("checkedVersion", 0);
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
}
