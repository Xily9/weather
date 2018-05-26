package com.xily.weather.network;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpHelper {
    private static OkHttpClient client;

    static {
        setUpOkHttpClient();
    }

    public static OkHttpClient getClient() {
        return client;
    }

    private static void setUpOkHttpClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }

    public static void get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
