package com.xily.weather.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xily.weather.adapter.CityAdapter;

import java.util.List;

import butterknife.ButterKnife;

/**
 * RecycleView通用基类
 * 极度简化了RecycleView的使用
 *
 * @param <T> 子类的ViewHolder
 * @param <U> List容器的类型
 * @author Xily
 */

public abstract class BaseAdapter<T extends BaseAdapter.ViewHolder, U> extends RecyclerView.Adapter<T> {
    protected Context mContext;
    protected List<U> mList;
    private CityAdapter.onClickListener onClickListener;
    private CityAdapter.onLongClickListener onLongClickListener;

    public BaseAdapter(Context mContext, List<U> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setOnClickListener(CityAdapter.onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(CityAdapter.onLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    protected abstract
    @LayoutRes
    int getLayoutId();

    protected abstract T getViewHolder(View view);

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getViewHolder(LayoutInflater.from(mContext).inflate(getLayoutId(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        if (onClickListener != null)
            holder.itemView.setOnClickListener(view -> onClickListener.onclick(position));
        if (onLongClickListener != null)
            holder.itemView.setOnLongClickListener(view -> onLongClickListener.onLongClick(position));
        onBindViewHolder(holder, position, mList.get(position));
    }

    protected abstract void onBindViewHolder(@NonNull T holder, int position, U value);

    public void setList(List<U> mList) {
        this.mList = mList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
