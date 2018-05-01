package com.xily.weather.utils;

import android.app.Activity;
import android.util.TypedValue;


public class ColorUtil {
    public static int getAttrColor(Activity activity, int resId) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(resId, typedValue, true);
        return typedValue.data;
    }
}
