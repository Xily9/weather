package com.xily.weather.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xily.weather.R;
import com.xily.weather.db.CityList;

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

        public ViewHolder(View itemView) {
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
