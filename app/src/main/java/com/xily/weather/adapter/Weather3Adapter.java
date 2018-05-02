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

public class Weather3Adapter extends RecyclerView.Adapter<Weather3Adapter.ViewHolder> {

    private Context mContext;
    private List<WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean> weathersBeanList;
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

    public Weather3Adapter(Context mContext, List<WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean> weathersBeanList) {
        this.mContext = mContext;
        this.weathersBeanList = weathersBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_weather3, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean weather3HoursDetailsInfosBean = weathersBeanList.get(position);
        holder.time.setText(weather3HoursDetailsInfosBean.getStartTime().substring(11, 16));
        if (map.containsKey(weather3HoursDetailsInfosBean.getImg())) {
            holder.icon.setImageResource(map.get(weather3HoursDetailsInfosBean.getImg()));
        } else {
            holder.icon.setImageResource(R.drawable.weather_na);
            LogUtil.d("unknown", weather3HoursDetailsInfosBean.getWeather() + weather3HoursDetailsInfosBean.getImg());
        }
        holder.weather.setText(weather3HoursDetailsInfosBean.getWeather());
        holder.temperature.setText(weather3HoursDetailsInfosBean.getHighestTemperature() + "°C");
    }

    @Override
    public int getItemCount() {
        return weathersBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
            ButterKnife.bind(this, itemView);
        }
    }

}

