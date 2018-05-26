package com.xily.weather.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.base.BaseAdapter;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.utils.LogUtil;
import com.xily.weather.utils.WeatherUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class ForecastAdapter extends BaseAdapter<ForecastAdapter.ViewHolder, WeatherInfo.ValueBean.WeathersBean> {

    private Map<String, Integer> map = WeatherUtil.getWeatherIcons();

    public ForecastAdapter(List<WeatherInfo.ValueBean.WeathersBean> mList) {
        super(mList);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, WeatherInfo.ValueBean.WeathersBean value) {
        holder.temperature.setText(value.getTemp_day_c() + "/" + value.getTemp_night_c() + "°C");
        holder.weather.setText(value.getWeather());
        if (position == 0) {
            holder.day.setText("今天");
        } else {
            holder.day.setText(value.getWeek());
        }
        if (map.containsKey(value.getImg())) {
            holder.icon.setImageResource(map.get(value.getImg()));
        } else {
            holder.icon.setImageResource(R.drawable.weather_na);
            LogUtil.d("unknown", value.getWeather() + value.getImg());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_item_forecast;
    }

    class ViewHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.weather)
        TextView weather;
        @BindView(R.id.temperature)
        TextView temperature;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

}

