package com.xily.weather.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.base.BaseAdapter;
import com.xily.weather.entity.WeatherInfo;

import java.util.List;

import butterknife.BindView;

public class AlarmAdapter extends BaseAdapter<AlarmAdapter.ViewHolder, WeatherInfo.ValueBean.AlarmsBean> {

    public AlarmAdapter(List<WeatherInfo.ValueBean.AlarmsBean> mList) {
        super(mList);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_item_alarm;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, WeatherInfo.ValueBean.AlarmsBean alarmsBean) {
        holder.alarm.setText(alarmsBean.getAlarmTypeDesc() + "预警");
        holder.updateTime.setText(alarmsBean.getPublishTime());
        holder.content.setText(alarmsBean.getAlarmContent());
    }

    class ViewHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.alarm)
        TextView alarm;
        @BindView(R.id.updateTime)
        TextView updateTime;
        @BindView(R.id.content)
        TextView content;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

}

