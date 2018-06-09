package com.xily.weather.presenter;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xily.weather.BuildConfig;
import com.xily.weather.base.RxBasePresenter;
import com.xily.weather.contract.MainContract;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.network.RetrofitHelper;
import com.xily.weather.rx.RxHelper;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.PreferenceUtil;

import javax.inject.Inject;

public class MainPresenter extends RxBasePresenter<MainContract.View> implements MainContract.Presenter {
    @Inject
    PreferenceUtil data;
    @Inject
    RxAppCompatActivity activity;

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
                .compose(activity.bindToLifecycle())
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

    }
}
