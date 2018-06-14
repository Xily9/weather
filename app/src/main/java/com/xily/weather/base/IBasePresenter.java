package com.xily.weather.base;


public interface IBasePresenter<T extends IBaseView> {

    void attachView(T view);

    void detachView();
}
