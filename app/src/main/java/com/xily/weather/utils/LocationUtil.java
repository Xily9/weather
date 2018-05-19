package com.xily.weather.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.xily.weather.MyApplication;

public class LocationUtil {
    private static LocationListener listener = new MyLocationListener();
    private static ILocationListener iLocationListener;
    private static LocationManager locationManager;

    @SuppressLint("MissingPermission")
    public static void getLocation(ILocationListener locationListener) {
        iLocationListener = locationListener;
        locationManager = (LocationManager) MyApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        Location location;
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            locationListener.listener(location);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                locationListener.listener(location);
            } else {
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    LogUtil.d("test", "getLocationNetwork");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                }
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    LogUtil.d("test", "getLocationGPS");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                }
            }
        }
    }

    public static void unRegisterListener() {
        if (listener != null && iLocationListener != null) {
            locationManager.removeUpdates(listener);
        }
    }

    private static class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            LogUtil.d("test", "LocationUpdate");
            if (iLocationListener != null) {
                iLocationListener.listener(location);
            }
            unRegisterListener();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    public interface ILocationListener {
        void listener(Location location);
    }
}
