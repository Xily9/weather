package com.xily.weather.utils;

import android.app.Activity;

import com.xily.weather.R;


/**
 * Created by Xily on 2017/10/24.
 */

public class ThemeUtil {
    private static final int[] colorList = {
            R.color.red,
            R.color.orange,
            R.color.pink,
            R.color.green,
            R.color.blue,
            R.color.purple,
            R.color.teal,
            R.color.brown,
            R.color.dark_blue,
            R.color.dark_purple,
    };

    public static void setTheme(Activity act) {
        int[] styleList = {
                R.style.AppThemeRed,
                R.style.AppThemeOrange,
                R.style.AppThemePink,
                R.style.AppThemeGreen,
                R.style.AppThemeBlue,
                R.style.AppThemePurple,
                R.style.AppThemeTeal,
                R.style.AppThemeBrown,
                R.style.AppThemeDarkBlue,
                R.style.AppThemeDarkPurple
        };
        act.setTheme(styleList[getTheme()]);
    }

    public static int[] getColorList() {
        return colorList;
    }

    public static int getTheme() {
        PreferenceUtil settingsData = new PreferenceUtil(PreferenceUtil.FILE_SETTING);
        return settingsData.get("theme", 4);
    }
}
