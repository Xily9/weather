package com.xily.weather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xily.weather.R;
import com.xily.weather.db.CityList;
import com.xily.weather.entity.WeatherInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    private Context mContext;
    private List<CityList> cityList;
    private onClickListener OnClicklistener;
    private onLongClickListener OnLongClickListener;

    public CityAdapter(Context mContext, List<CityList> cityList) {
        this.mContext = mContext;
        this.cityList = cityList;
    }

    public void setOnClicklistener(onClickListener onClicklistener) {
        this.OnClicklistener = onClicklistener;
    }

    public void setOnLongClickListener(onLongClickListener onLongClickListener) {
        this.OnLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_city, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> OnClicklistener.onclick(position));
        holder.itemView.setOnLongClickListener(view -> OnLongClickListener.onLongClick(position));
        holder.cityName.setText(cityList.get(position).getCityName());
        String data = cityList.get(position).getWeatherData();
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
            holder.wind.setText(valueBean.getRealtime().getWD() + valueBean.getRealtime().getWS());
            holder.todayTemp.setText(valueBean.getWeathers().get(0).getTemp_day_c() + " / " + valueBean.getWeathers().get(0).getTemp_night_c() + "°");
            holder.wet.setText("湿度" + valueBean.getRealtime().getSD() + "%");
        }
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public void setCityList(List<CityList> cityList) {
        this.cityList = cityList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
            ButterKnife.bind(this, itemView);
        }
    }

    public interface onClickListener {
        void onclick(int position);
    }

    public interface onLongClickListener {
        boolean onLongClick(int position);
    }
}
