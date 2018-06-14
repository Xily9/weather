package com.xily.weather.contract;

import android.graphics.drawable.Drawable;

import com.xily.weather.base.IBasePresenter;
import com.xily.weather.base.IBaseView;
import com.xily.weather.model.bean.CityListBean;
import com.xily.weather.model.bean.VersionBean;

import java.util.List;

public interface MainContract {
    interface View extends IBaseView {
        void showUpdateDialog(String versionName, int version, VersionBean.DataBean dataBean);
        void initProgress();
        void showDownloadProgress(int progress);
        void closeProgress();

        void setBingPic(String url);

        void setBackground(Drawable resource);

        void setProgressBar(int mode);

        void setEmptyView(int mode);

        void initCities();
    }

    interface Presenter extends IBasePresenter<View> {
        List<CityListBean> getCityList();
        void checkVersion();
        void update(String url);

        void getBingPic(String day);

        void setCheckVersion(int checkVersion);

        boolean getCheckUpdate();

        int getBgMode();

        String getBingPicTime();

        String getBingPicUrl();

        String getBgImgPath();

        String getNavImgPath();

        int getNavMode();

        void loadBingPic(String url);

        void findLocation();
    }
}
