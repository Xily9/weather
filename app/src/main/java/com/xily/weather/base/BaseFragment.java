package com.xily.weather.base;

import android.os.Bundle;
import android.view.View;

import com.xily.weather.app.App;
import com.xily.weather.di.component.DaggerFragmentComponent;
import com.xily.weather.di.component.FragmentComponent;
import com.xily.weather.di.module.FragmentModule;

import javax.inject.Inject;

public abstract class BaseFragment<T extends BasePresenter> extends RxBaseFragment implements BaseView {
    @Inject
    protected T mPresenter;

    public abstract void initInject();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initInject();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    protected FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .appComponent(App.getAppComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }
}
