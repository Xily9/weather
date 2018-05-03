package com.xily.weather.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.xily.weather.MyApplication;

import static android.content.Context.ACTIVITY_SERVICE;


public class DeviceUtil {

    private static final DisplayMetrics outMetrics = MyApplication.getInstance().getResources().getDisplayMetrics();
    private static Location location;

    /**
     * 获取设备宽度
     *
     * @return
     */
    public static int getWidth() {
        return outMetrics.widthPixels;
    }

    /**
     * 获取设备高度
     *
     * @return
     */
    public static int getHeight() {
        return outMetrics.heightPixels;
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return
     */
    public static int dp2px(float dpValue) {
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, outMetrics));
    }

    /**
     * 显示或隐藏StatusBar
     *
     * @param enable false 显示，true 隐藏
     */
    public static void hideStatusBar(Window window, boolean enable) {
        WindowManager.LayoutParams p = window.getAttributes();
        if (enable) {
            p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        window.setAttributes(p);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void hideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) MyApplication.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static String getCacheDir() {
        return MyApplication.getInstance().getExternalCacheDir().getPath();
    }

    @SuppressLint("MissingPermission")
    public static Location getLocation() {
        LocationManager locationManager = (LocationManager) MyApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }

    public static void setStatusBarUpper(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static boolean isServiceRunning(String ServicePackageName) {
        ActivityManager manager = (ActivityManager) MyApplication.getInstance().getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ServicePackageName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
