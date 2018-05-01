package com.xily.weather.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackbarUtil {
    public static void showMessage(View view, String text) {
        new Handler(Looper.getMainLooper()).post(() -> Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show());
    }
}
