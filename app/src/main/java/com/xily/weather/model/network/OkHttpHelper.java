package com.xily.weather.model.network;


import javax.inject.Inject;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpHelper {
    private OkHttpClient okHttpClient;

    @Inject
    public OkHttpHelper(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public void get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
