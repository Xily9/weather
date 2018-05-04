package com.xily.weather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.base.BaseAdapter;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class Weather3Adapter extends BaseAdapter<Weather3Adapter.ViewHolder, WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean> {

    private static Map<String, Integer> map = new HashMap<String, Integer>() {{
        put("0", R.drawable.weather_0);
        put("1", R.drawable.weather_1);
        put("2", R.drawable.weather_2);
        put("3", R.drawable.weather_3);
        put("4", R.drawable.weather_4);
        put("7", R.drawable.weather_7);
        put("8", R.drawable.weather_8);
        put("9", R.drawable.weather_9);
        put("29", R.drawable.weather_29);
    }};

    public Weather3Adapter(Context mContext, List<WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean> mList) {
        super(mContext, mList);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_item_weather3;
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean value) {
        holder.time.setText(value.getStartTime().substring(11, 16));
        if (map.containsKey(value.getImg())) {
            holder.icon.setImageResource(map.get(value.getImg()));
        } else {
            holder.icon.setImageResource(R.drawable.weather_na);
            LogUtil.d("unknown", value.getWeather() + value.getImg());
        }
        holder.weather.setText(value.getWeather());
        holder.temperature.setText(value.getHighestTemperature() + "°C");
    }

    class ViewHolder extends BaseAdapter.ViewHolder {
        @BindView(R.id.time)
        TextView time;
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

