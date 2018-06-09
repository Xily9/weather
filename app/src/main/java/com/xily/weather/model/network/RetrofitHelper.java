package com.xily.weather.model.network;

import com.xily.weather.model.network.api.WeatherApi;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static OkHttpClient client;//okHttpClient单例化
    private static WeatherApi weatherApiInstance;//Retrofit单例化
    static {
        client = OkHttpHelper.getClient();
        weatherApiInstance = createApi(WeatherApi.class);
    }

    public static WeatherApi getWeatherApi() {
        return weatherApiInstance;
    }

    private static <T> T createApi(Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://127.0.0.1/")//随便写一个,不写会报错
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

/*
    private static void setUpOkHttpClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request=chain.request();
                    Request.Builder builder = request.newBuilder();
                    List<String> headerValues = request.headers("baseUrl");
                    if(headerValues != null&& headerValues.size() > 0) {
                        builder.removeHeader("baseUrl");
                        String headerValue = headerValues.get(0);
                        LogUtil.d("value",headerValue);
                        HttpUrl newBaseUrl = null;
                        if("myApi".equals(headerValue)) {
                            newBaseUrl = HttpUrl.parse(myApiUrl);
                        } else if("meiZuApi".equals(headerValue)) {
                            newBaseUrl = HttpUrl.parse(meiZuApiUrl);
                        } else if("guoLinApi".equals(headerValue)){
                            newBaseUrl = HttpUrl.parse(guoLinApiUrl);
                        }else if("heWeatherApi".equals(headerValue)){
                            newBaseUrl = HttpUrl.parse(heWeatherApiUrl);
                        }
                        HttpUrl newFullUrl = request.url().newBuilder()
                                .scheme(newBaseUrl.scheme())
                                .host(newBaseUrl.host())
                                .port(newBaseUrl.port())
                                .encodedPath(newBaseUrl.encodedPath()+request.url())
                                .build();
                        LogUtil.d("url",newFullUrl.toString());
                        return chain.proceed(builder.url(newFullUrl).build());
                    } else {
                        return chain.proceed(request);
                    }
                })
                .build();
    }
    */
}
