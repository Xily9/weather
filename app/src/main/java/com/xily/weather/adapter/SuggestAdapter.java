package com.xily.weather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.base.BaseAdapter;
import com.xily.weather.entity.WeatherInfo;

import java.util.List;

import butterknife.BindView;

public class SuggestAdapter extends BaseAdapter<SuggestAdapter.ViewHolder, WeatherInfo.ValueBean.IndexesBean> {

    public SuggestAdapter(Context mContext, List<WeatherInfo.ValueBean.IndexesBean> mList) {
        super(mContext, mList);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_item_suggest;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, WeatherInfo.ValueBean.IndexesBean value) {
        holder.name.setText(value.getName());
        holder.value.setText(value.getLevel());
    }

    class ViewHolder extends BaseAdapter.BaseViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.value)
        TextView value;

        ViewHolder(View itemView) {
            super(itemView);
        }
    }

}

