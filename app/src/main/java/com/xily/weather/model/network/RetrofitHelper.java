package com.xily.weather.model.network;

import com.xily.weather.model.bean.CitiesBean;
import com.xily.weather.model.bean.CountiesBean;
import com.xily.weather.model.bean.ProvincesBean;
import com.xily.weather.model.bean.SearchBean;
import com.xily.weather.model.bean.VersionBean;
import com.xily.weather.model.bean.WeatherBean;
import com.xily.weather.model.network.api.WeatherApi;

import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RetrofitHelper implements HttpHelper {

    private static OkHttpClient client;//okHttpClient单例化
    private static WeatherApi weatherApiInstance;//Retrofit单例化
    static {
        client = OkHttpHelper.getClient();
        weatherApiInstance = createApi(WeatherApi.class);
    }

    @Inject
    public RetrofitHelper() {

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

    @Override
    public Observable<VersionBean> checkVersion() {
        return weatherApiInstance.checkVersion();
    }

    @Override
    public Observable<WeatherBean> getWeather(String cityId) {
        return weatherApiInstance.getWeather(cityId);
    }

    @Override
    public Observable<SearchBean> search(String location) {
        return weatherApiInstance.search(location);
    }

    @Override
    public Observable<List<ProvincesBean>> getProvinces() {
        return weatherApiInstance.getProvinces();
    }

    @Override
    public Observable<List<CitiesBean>> getCities(String province) {
        return weatherApiInstance.getCities(province);
    }

    @Override
    public Observable<List<CountiesBean>> getCounties(String province, String city) {
        return weatherApiInstance.getCounties(province, city);
    }
}
