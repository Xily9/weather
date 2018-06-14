package com.xily.weather.contract;

import com.xily.weather.base.IBasePresenter;
import com.xily.weather.base.IBaseView;

public interface AboutContract {
    interface View extends IBaseView {
    }

    interface Presenter extends IBasePresenter<View> {
    }
}
