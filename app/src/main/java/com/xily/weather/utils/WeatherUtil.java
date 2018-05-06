package com.xily.weather.utils;

import com.xily.weather.R;

import java.util.HashMap;
import java.util.Map;

public class WeatherUtil {
    private static Map<String, Integer> map = new HashMap<String, Integer>() {{
        put("0", R.drawable.weather_0);
        put("1", R.drawable.weather_1);
        put("2", R.drawable.weather_2);
        put("3", R.drawable.weather_3);
        put("4", R.drawable.weather_4);
        put("7", R.drawable.weather_7);
        put("8", R.drawable.weather_8);
        put("9", R.drawable.weather_9);
        put("10", R.drawable.weather_10);
        put("29", R.drawable.weather_29);
    }};

    public static Map<String, Integer> getWeatherIcons() {
        return map;
    }
}
