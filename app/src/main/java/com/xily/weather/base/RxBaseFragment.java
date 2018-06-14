package com.xily.weather.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;
import com.xily.weather.app.App;
import com.xily.weather.di.component.DaggerFragmentComponent;
import com.xily.weather.di.component.FragmentComponent;
import com.xily.weather.di.module.FragmentModule;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class RxBaseFragment<T extends IBasePresenter> extends RxFragment implements IBaseView {
    @Inject
    protected T mPresenter;
    private FragmentActivity activity;
    private Unbinder bind;

    public abstract
    @LayoutRes
    int getLayoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View parentView = inflater.inflate(getLayoutId(), container, false);
        activity = getSupportActivity();
        return parentView;
    }

    /**
     * 初始化views
     *
     * @param state
     */
    public abstract void finishCreateView(Bundle state);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind.unbind();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }


    public FragmentActivity getSupportActivity() {
        return super.getActivity();
    }


    public android.app.ActionBar getSupportActionBar() {
        return getSupportActivity().getActionBar();
    }


    public Context getApplicationContext() {
        return this.activity == null ? (getActivity() == null ?
                null : getActivity().getApplicationContext()) : this.activity.getApplicationContext();
    }

    /**
     * 加载数据
     */
    protected void loadData() {
    }

    /**
     * 初始化refreshLayout
     */
    protected void initRefreshLayout() {
    }

    /**
     * 设置数据显示
     */
    protected void finishTask() {
    }

    protected void initToolbar() {

    }

    public abstract void initInject();

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initInject();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
        super.onViewCreated(view, savedInstanceState);
        bind = ButterKnife.bind(this, view);
        finishCreateView(savedInstanceState);
    }

    protected FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .appComponent(App.getAppComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }
}
