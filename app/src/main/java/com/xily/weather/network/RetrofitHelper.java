package com.xily.weather.network;

import com.xily.weather.network.api.BaiduLocationApiService;
import com.xily.weather.network.api.GuoLinApiService;
import com.xily.weather.network.api.HeWeatherApiService;
import com.xily.weather.network.api.MeiZuWeatherApiService;
import com.xily.weather.network.api.MyApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static OkHttpClient client;
    private static final String myApiUrl = "https://xilym.tk/api/";
    private static final String meiZuApiUrl = "http://aider.meizu.com/app/weather/";
    private static final String guoLinApiUrl = "http://guolin.tech/api/";
    private static final String heWeatherApiUrl = "https://search.heweather.com/";
    private static final String baiduApiUrl = "http://api.map.baidu.com/";
    private static MyApiService myApiServiceInstance;
    private static MeiZuWeatherApiService meiZuWeatherApiServiceInstance;
    private static GuoLinApiService guoLinApiServiceInstance;
    private static HeWeatherApiService heWeatherApiServiceInstance;
    private static BaiduLocationApiService baiduLocationApiServiceInstance;
    static {
        setUpOkHttpClient();
    }

    public static BaiduLocationApiService getBaiduLocationApi() {
        if (baiduLocationApiServiceInstance == null) {
            baiduLocationApiServiceInstance = new Retrofit.Builder()
                    .baseUrl(baiduApiUrl)
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(BaiduLocationApiService.class);
        }
        return baiduLocationApiServiceInstance;
    }

    public static HeWeatherApiService getHeWeatherApi() {
        if (heWeatherApiServiceInstance == null) {
            heWeatherApiServiceInstance = createApi(HeWeatherApiService.class, heWeatherApiUrl);
        }
        return heWeatherApiServiceInstance;
    }

    public static MyApiService getMyApi() {
        if (myApiServiceInstance == null) {
            myApiServiceInstance = createApi(MyApiService.class, myApiUrl);
        }
        return myApiServiceInstance;
    }

    public static MeiZuWeatherApiService getMeiZuWeatherApi() {
        if (meiZuWeatherApiServiceInstance == null) {
            meiZuWeatherApiServiceInstance = createApi(MeiZuWeatherApiService.class, meiZuApiUrl);
        }
        return meiZuWeatherApiServiceInstance;
    }

    public static GuoLinApiService getGuoLinApi() {
        if (guoLinApiServiceInstance == null) {
            guoLinApiServiceInstance = createApi(GuoLinApiService.class, guoLinApiUrl);
        }
        return guoLinApiServiceInstance;
    }

    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    private static <T> T createApi(Class<T> clazz, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    private static void setUpOkHttpClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }
}
