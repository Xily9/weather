package com.xily.weather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xily.weather.R;
import com.xily.weather.base.BaseAdapter;
import com.xily.weather.db.CityList;
import com.xily.weather.entity.WeatherInfo;

import java.util.List;

import butterknife.BindView;

public class CityAdapter extends BaseAdapter<CityAdapter.ViewHolder, CityList> {

    public CityAdapter(Context mContext, List<CityList> mList) {
        super(mContext, mList);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, CityList value) {
        holder.cityName.setText(value.getCityName());
        String data = value.getWeatherData();
        if (TextUtils.isEmpty(data)) {
            holder.air.setText("N/A");
            holder.temperature.setText("N/A");
            holder.todayTemp.setText("N/A");
            holder.wet.setText("N/A");
            holder.wind.setText("N/A");
            holder.weather.setText("N/A");
        } else {
            WeatherInfo weatherInfo = new Gson().fromJson(data, WeatherInfo.class);
            WeatherInfo.ValueBean valueBean = weatherInfo.getValue().get(0);
            holder.air.setText("空气质量" + valueBean.getPm25().getQuality());
            holder.weather.setText(valueBean.getRealtime().getWeather());
            holder.temperature.setText(valueBean.getRealtime().getTemp() + "°");
            holder.wind.setText(valueBean.getRealtime().getWd() + valueBean.getRealtime().getWs());
            holder.todayTemp.setText(valueBean.getWeathers().get(0).getTemp_day_c() + " / " + valueBean.getWeathers().get(0).getTemp_night_c() + "°");
            holder.wet.setText("湿度" + valueBean.getRealtime().getSd() + "%");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_item_city;
    }

    class ViewHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.cityName)
        TextView cityName;
        @BindView(R.id.weather)
        TextView weather;
        @BindView(R.id.temperature)
        TextView temperature;
        @BindView(R.id.air)
        TextView air;
        @BindView(R.id.wet)
        TextView wet;
        @BindView(R.id.wind)
        TextView wind;
        @BindView(R.id.todayTemp)
        TextView todayTemp;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
