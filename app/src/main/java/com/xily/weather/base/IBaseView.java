package com.xily.weather.base;


import com.trello.rxlifecycle2.LifecycleTransformer;

public interface IBaseView {
    void showErrorMsg(String msg);

    <T> LifecycleTransformer<T> bindToLifecycle();
}
