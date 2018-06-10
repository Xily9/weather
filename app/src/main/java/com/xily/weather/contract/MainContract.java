package com.xily.weather.contract;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.base.BaseView;
import com.xily.weather.model.bean.VersionBean;

public interface MainContract {
    interface View extends BaseView {
        void showUpdateDialog(String versionName, int version, VersionBean.DataBean dataBean);

        void initProgress();
        void showDownloadProgress(int progress);

        void closeProgress();
    }

    interface Presenter extends BasePresenter<View> {
        void checkVersion();

        void update(String url);
    }
}
