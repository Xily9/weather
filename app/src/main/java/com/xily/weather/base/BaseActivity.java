package com.xily.weather.base;

import android.os.Bundle;

import com.xily.weather.app.App;
import com.xily.weather.di.component.ActivityComponent;
import com.xily.weather.di.component.DaggerActivityComponent;
import com.xily.weather.di.module.ActivityModule;

import javax.inject.Inject;

public abstract class BaseActivity<T extends BasePresenter> extends RxBaseActivity implements BaseView {
    @Inject
    protected T mPresenter;

    public abstract void initInject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initInject();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        super.onCreate(savedInstanceState);
    }

    protected ActivityComponent getActivityComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(App.getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }
}
