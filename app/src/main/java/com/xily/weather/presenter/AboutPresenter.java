package com.xily.weather.presenter;

import com.xily.weather.base.BasePresenter;
import com.xily.weather.contract.AboutContract;

import javax.inject.Inject;

public class AboutPresenter extends BasePresenter<AboutContract.View> implements AboutContract.Presenter {
    @Inject
    public AboutPresenter() {

    }
}
