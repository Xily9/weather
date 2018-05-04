package com.xily.weather.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.xily.weather.utils.ThemeUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class RxBaseActivity extends RxAppCompatActivity {
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        super.onDestroy();
        bind.unbind();
    }
}
