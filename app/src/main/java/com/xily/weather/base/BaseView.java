package com.xily.weather.base;

import rx.Observable;

public interface BaseView {
    void showErrorMsg(String msg);

    <T> Observable.Transformer<T, T> bindToLifecycle();
}
