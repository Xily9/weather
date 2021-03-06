package com.xily.weather.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.base.BaseAdapter;
import com.xily.weather.model.bean.WeatherBean;

import java.util.List;

import butterknife.BindView;

public class SuggestAdapter extends BaseAdapter<SuggestAdapter.ViewHolder, WeatherBean.ValueBean.IndexesBean> {

    public SuggestAdapter(List<WeatherBean.ValueBean.IndexesBean> mList) {
        super(mList);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_item_suggest;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, WeatherBean.ValueBean.IndexesBean value) {
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

