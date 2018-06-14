package com.xily.weather.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xily.weather.app.App;
import com.xily.weather.di.component.ActivityComponent;
import com.xily.weather.di.component.DaggerActivityComponent;
import com.xily.weather.di.module.ActivityModule;
import com.xily.weather.utils.SnackbarUtil;
import com.xily.weather.utils.ThemeUtil;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class RxBaseActivity<T extends IBasePresenter> extends RxAppCompatActivity implements IBaseView {
    @Inject
    protected T mPresenter;
    private Unbinder bind;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInject();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        //设置主题
        ThemeUtil.setTheme(this);
        //设置布局内容
        setContentView(getLayoutId());
        //初始化黄油刀控件绑定框架
        bind = ButterKnife.bind(this);
        //初始化控件
        initViews(savedInstanceState);
    }


    /**
     * 设置布局layout
     *
     * @return
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * 初始化views
     *
     * @param savedInstanceState
     */
    public abstract void initViews(Bundle savedInstanceState);

    /**
     * 初始化toolbar
     */
    public void initToolBar() {

    }

    /**
     * 加载数据
     */
    public void loadData() {
    }

    /**
     * 设置数据显示
     */
    public void finishTask() {
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
        bind.unbind();
    }

    public abstract void initInject();

    protected ActivityComponent getActivityComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(App.getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    @Override
    public void showErrorMsg(String msg) {
        SnackbarUtil.showMessage(getWindow().getDecorView(), msg);
    }
}
