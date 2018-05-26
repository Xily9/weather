package com.xily.weather.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.xily.weather.R;


/**
 * Created by Xily on 2017/10/24.
 */

public class ThemeUtil {
    private static android.support.v7.app.AlertDialog dialog;
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

    private static int[] styleList = {
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

    public static void setTheme(Activity act) {
        act.setTheme(styleList[getTheme()]);
    }

    private static int[] getColorList() {
        return colorList;
    }

    public static int getTheme() {
        PreferenceUtil settingsData = PreferenceUtil.getInstance();
        return settingsData.get("theme", 4);
    }

    public static void showSwitchThemeDialog(Activity activity) {
        LinearLayout linearLayout = new LinearLayout(activity);
        int padding = DeviceUtil.dp2px(20);
        linearLayout.setPadding(padding, padding, padding, padding);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        FrameLayout frameLayout = new FrameLayout(activity);
        int[] colorList = ThemeUtil.getColorList();
        int theme = ThemeUtil.getTheme();
        for (int i = 0; i < colorList.length; i++) {
            Button button = new Button(activity);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(activity.getResources().getColor(colorList[i]));
            gradientDrawable.setShape(GradientDrawable.OVAL);
            button.setBackground(gradientDrawable);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(DeviceUtil.dp2px(40), DeviceUtil.dp2px(40));
            layoutParams.leftMargin = DeviceUtil.dp2px(50) * (i % 5) + DeviceUtil.dp2px(5);
            layoutParams.topMargin = DeviceUtil.dp2px(50) * (i / 5) + DeviceUtil.dp2px(5);
            int finalI = i;
            button.setOnClickListener(v -> {
                PreferenceUtil settingsData = PreferenceUtil.getInstance();
                settingsData.put("theme", finalI);
                dialog.dismiss();
                activity.recreate();
            });
            if (i == theme) {
                button.setText("✔");
                button.setTextColor(Color.parseColor("#ffffff"));
                button.setTextSize(15);
                button.setGravity(Gravity.CENTER);
            }
            frameLayout.addView(button, layoutParams);
        }
        linearLayout.addView(frameLayout);
        dialog = new AlertDialog.Builder(activity).setTitle("设置主题").setView(linearLayout).show();
    }
}
