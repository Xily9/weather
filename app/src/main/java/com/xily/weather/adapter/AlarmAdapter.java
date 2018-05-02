package com.xily.weather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.entity.WeatherInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private Context mContext;
    private List<WeatherInfo.ValueBean.AlarmsBean> alarmsBeanList;

    public AlarmAdapter(Context mContext, List<WeatherInfo.ValueBean.AlarmsBean> alarmsBeanList) {
        this.mContext = mContext;
        this.alarmsBeanList = alarmsBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_alarm, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherInfo.ValueBean.AlarmsBean alarmsBean = alarmsBeanList.get(position);
        holder.alarm.setText(alarmsBean.getAlarmTypeDesc() + "预警");
        holder.updateTime.setText(alarmsBean.getPublishTime());
        holder.content.setText(alarmsBean.getAlarmContent());
    }

    @Override
    public int getItemCount() {
        return alarmsBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.alarm)
        TextView alarm;
        @BindView(R.id.updateTime)
        TextView updateTime;
        @BindView(R.id.content)
        TextView content;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

