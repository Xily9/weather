package com.xily.weather.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.xily.weather.R;
import com.xily.weather.entity.WeatherInfo;
import com.xily.weather.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Weather3View extends View {
    private Context context;
    private Paint paint = new Paint();
    private int width = 60, oldX, oldY, w, h, paddingTop = 10;
    private DisplayMetrics dm = getResources().getDisplayMetrics();
    private Map<String, Integer> map = new HashMap<String, Integer>() {{
        put("0", R.drawable.weather_0);
        put("1", R.drawable.weather_1);
        put("2", R.drawable.weather_2);
        put("3", R.drawable.weather_3);
        put("4", R.drawable.weather_4);
        put("7", R.drawable.weather_7);
        put("8", R.drawable.weather_8);
        put("9", R.drawable.weather_9);
        put("29", R.drawable.weather_29);
    }};
    public List<WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean> weather3HoursDetailsInfosBeans = new ArrayList<>();

    public Weather3View(Context context) {
        super(context);
        this.context = context;
    }

    public Weather3View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setData(List<WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean> weather3HoursDetailsInfosBeans) {
        this.weather3HoursDetailsInfosBeans.clear();
        this.weather3HoursDetailsInfosBeans.addAll(weather3HoursDetailsInfosBeans);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        w = dp2px(width * weather3HoursDetailsInfosBeans.size());
        h = dp2px(200);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (weather3HoursDetailsInfosBeans.isEmpty()) return;
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(dp2px(13));
        paint.setTextAlign(Paint.Align.CENTER);
        int x = width / 2;
        int max, min;
        max = min = Integer.valueOf(weather3HoursDetailsInfosBeans.get(0).getHighestTemperature());
        for (int i = 1; i < weather3HoursDetailsInfosBeans.size(); i++) {
            int temp = Integer.valueOf(weather3HoursDetailsInfosBeans.get(i).getHighestTemperature());
            if (temp > max) max = temp;
            if (temp < min) min = temp;
        }
        int h1 = 70 / (max - min);
        int h2 = 100;
        for (int i = 0; i < weather3HoursDetailsInfosBeans.size(); i++) {
            WeatherInfo.ValueBean.WeatherDetailsInfoBean.Weather3HoursDetailsInfosBean value = weather3HoursDetailsInfosBeans.get(i);
            int temp = Integer.valueOf(value.getHighestTemperature());
            paint.setStrokeWidth(dp2px(4));
            canvas.drawCircle(dp2px(x), dp2px(h1 * (max - temp) + paddingTop + 10), dp2px(2), paint);
            if (i < weather3HoursDetailsInfosBeans.size() - 1) {
                int nextTemp = Integer.valueOf(weather3HoursDetailsInfosBeans.get(i + 1).getHighestTemperature());
                paint.setStrokeWidth(dp2px(2));
                canvas.drawLine(dp2px(x), dp2px(h1 * (max - temp) + paddingTop + 10), dp2px(x + width), dp2px(h1 * (max - nextTemp) + paddingTop + 10), paint);
            }
            paint.setStrokeWidth(0);
            canvas.drawText(temp + "°C", dp2px(x), dp2px(h1 * (max - temp) + paddingTop), paint);
            canvas.drawText(value.getStartTime().substring(11, 16), dp2px(x), dp2px(h2 + 25), paint);
            Bitmap bitmap;
            if (map.containsKey(value.getImg())) {
                bitmap = BitmapFactory.decodeResource(getResources(),
                        map.get(value.getImg()));
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.weather_na);
                LogUtil.d("unknown", value.getWeather() + value.getImg());
            }
            RectF rectF = new RectF(dp2px(x - 20), dp2px(h2 + 35), dp2px(x + 20), dp2px(h2 + 75));
            canvas.drawBitmap(bitmap, null, rectF, paint);
            canvas.drawText(value.getWeather(), dp2px(x), dp2px(h2 + 95), paint);
            x += width;
        }
    }

    /*
    暂时还搞不定滑动冲突等各种奇葩问题,先套一个HorizontalScrollView凑活着用
        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldX = x;
                    oldY = y;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    LogUtil.d("down","down");
                    break;
                case MotionEvent.ACTION_MOVE:
                    LogUtil.d("move","move");
                    if (Math.abs(y - oldY) < 10 && (oldX > x && getScrollX() + dm.widthPixels <= w) || (oldX < x && getScrollX() >= 0)) {
                        scrollBy(oldX - x, 0);
                        LogUtil.d("move",String.valueOf(oldX-x));
                    }else{
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    oldX = x;
                    oldY = y;
                    break;
            }
            return true;
        }
    */
    public int dp2px(float dpValue) {
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, dm));
    }
}
