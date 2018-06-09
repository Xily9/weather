package com.xily.weather.contract;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.model.bean.VersionBean;

public interface MainContract {
    interface View {
        void showUpdateDialog(String versionName, int version, VersionBean.DataBean dataBean);

        void showDownloadProgress(int progress);
    }

    interface Presenter extends BasePresenter<View> {
        void checkVersion();

        void update(String url);
    }
}
