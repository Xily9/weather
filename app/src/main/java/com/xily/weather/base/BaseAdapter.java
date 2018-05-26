package com.xily.weather.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

public abstract class BaseAdapter<T extends BaseAdapter.BaseViewHolder, U> extends RecyclerView.Adapter<T> {
    private Context mContext;
    private List<U> mList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public Context getContext() {
        return mContext;
    }

    public List<U> getList() {
        return mList;
    }

    public void setList(List<U> mList) {
        this.mList = mList;
    }

    public BaseAdapter(List<U> mList) {
        this.mList = mList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    protected abstract
    @LayoutRes
    int getLayoutId();

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        T holder = createViewHolder(LayoutInflater.from(mContext).inflate(getLayoutId(), parent, false));
        if (onItemClickListener != null)
            holder.itemView.setOnClickListener(view -> {
                int position = holder.getAdapterPosition();
                onItemClickListener.onItemClick(position);
            });
        if (onItemLongClickListener != null)
            holder.itemView.setOnLongClickListener(view -> {
                int position = holder.getAdapterPosition();
                return onItemLongClickListener.onItemLongClick(position);
            });
        return holder;
    }

    /**
     * 利用反射获取子类的ViewHolder实例
     */
    @SuppressWarnings("unchecked")
    private T createViewHolder(View view) {
        Type type = getClass().getGenericSuperclass();//获取父类Type
        if (type instanceof ParameterizedType) {//判断是不是泛型参数列表
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();//获取泛型参数列表
            Class clazz = (Class) types[0];//获取第一个Class
            try {
                Constructor cons;
                if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {//是内部类且不是静态类
                    cons = clazz.getDeclaredConstructor(getClass(), View.class);//获取内部类的构造函数,要注意必须要传入外部类的Class
                    cons.setAccessible(true);//设置为可访问
                    return (T) cons.newInstance(this, view);//实例化
                } else {
                    cons = clazz.getDeclaredConstructor(View.class);
                    cons.setAccessible(true);
                    return (T) cons.newInstance(view);
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return (T) new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        onBindViewHolder(holder, position, mList.get(position));
    }

    protected abstract void onBindViewHolder(@NonNull T holder, int position, U value);

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position);
    }
}
