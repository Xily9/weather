package com.xily.weather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private Context mContext;
    private List<WeatherInfo.ValueBean.WeathersBean> weathersBeanList;
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

    public ForecastAdapter(Context mContext, List<WeatherInfo.ValueBean.WeathersBean> weathersBeanList) {
        this.mContext = mContext;
        this.weathersBeanList = weathersBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_forecast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherInfo.ValueBean.WeathersBean weathersBean = weathersBeanList.get(position);
        holder.temperature.setText(weathersBean.getTemp_day_c() + "/" + weathersBean.getTemp_night_c() + "°C");
        holder.weather.setText(weathersBean.getWeather());
        if (position == 0) {
            holder.day.setText("今天");
        } else {
            holder.day.setText(weathersBean.getWeek());
        }
        if (map.containsKey(weathersBean.getImg())) {
            holder.icon.setImageResource(map.get(weathersBean.getImg()));
        } else {
            holder.icon.setImageResource(R.drawable.weather_na);
            LogUtil.d("unknown", weathersBean.getWeather() + weathersBean.getImg());
        }
    }

    @Override
    public int getItemCount() {
        return weathersBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
            ButterKnife.bind(this, itemView);
        }
    }

}

