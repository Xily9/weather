package com.xily.weather.utils;

import android.content.Context;
import android.widget.Toast;

import com.xily.weather.app.App;


public class ToastUtil {
    public static void showShort(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void LongToast(final String text) {
        Toast.makeText(App.getInstance(), text, Toast.LENGTH_LONG).show();
    }

    public static void LongToast(final int stringId) {
        Toast.makeText(App.getInstance(), stringId, Toast.LENGTH_LONG).show();
    }

    public static void ShortToast(final String text) {
        Toast.makeText(App.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

    public static void ShortToast(final int stringId) {
        Toast.makeText(App.getInstance(), stringId, Toast.LENGTH_SHORT).show();
    }
}
