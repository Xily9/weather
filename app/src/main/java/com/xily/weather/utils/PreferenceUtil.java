package com.xily.weather.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xily.weather.app.App;

import java.util.Map;

import javax.inject.Inject;

public class PreferenceUtil {
    private static PreferenceUtil mInstance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static PreferenceUtil getInstance() {
        if (mInstance == null) {
            synchronized (PreferenceUtil.class) {
                if (mInstance == null) {
                    mInstance = new PreferenceUtil();
                }
            }
        }
        return mInstance;
    }

    @Inject
    @SuppressLint("CommitPrefEdits")
    public PreferenceUtil() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        editor = sharedPreferences.edit();
    }

    /**
     * 保存数据的方法，拿到数据保存数据的基本类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public PreferenceUtil put(String key, Object object) {

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else if (object != null) {
            editor.putString(key, object.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
        return this;
    }

    /**
     * 获取保存数据的方法，我们根据默认值的到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key           键的值
     * @param defaultObject 默认值
     * @return
     */

    private Object getValue(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }

    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultObject) {
        return (T) getValue(key, defaultObject);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public PreferenceUtil remove(String key) {
        editor.remove(key);
        editor.apply();
        return this;
    }

    /**
     * 清除所有的数据
     */
    public PreferenceUtil clear() {
        editor.clear();
        editor.apply();
        return this;
    }

    /**
     * 查询某个key是否存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }
}
