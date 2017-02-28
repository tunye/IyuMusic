package com.iyuba.music.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.iyuba.music.listener.ILocationListener;
import com.iyuba.music.manager.RuntimeManager;

import java.util.Timer;
import java.util.TimerTask;

public class LocationUtil {

    public final static int GPSTIMEOUT = 0;
    public final static int STATUS_CHANGED = 1;
    public final static int SELECT_LOCATION = 2;
    public final static int DEFAULT_LOCATION_COMPLETED = 3;
    public final static int GET_LOCATIONBUILDINGLIST_FAILED = 4;
    public final static int CANCELGPS_COMPLETED = 5;
    public final static int SELECT_LOCATION_COMPLETED = 6;
    public final static int REFRESHGPS_COMPLETED = 7;
    public final static int REFRESHGPS_NOPROVIDER = 8;
    public final static int GETLOCATION_FAILED = 9;
    private static LocationUtil instanceGetLocation;
    private ILocationListener iLocationListener = null;
    /**
     * location services
     */
    private LocationManager locationManager = null;
    /**
     * location services
     */
    private MyLocationListener mLocationListener = null;

    private double latitude = 39.9;
    private double longitude = 116.3;
    private Timer mGpsTimer = new Timer();
    private Handler mGpsTimerHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (locationManager == null) {
                return;
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                } catch (SecurityException e) {
                    latitude = 39.9;
                    longitude = 116.3;
                }
            } else {
                try {
                    locationManager.removeUpdates(mLocationListener);
                } catch (SecurityException e) {
                    latitude = 39.9;
                    longitude = 116.3;
                }
                if (iLocationListener != null) {
                    iLocationListener.notifyChange(GPSTIMEOUT, null);
                }
            }
        }
    };

    public static LocationUtil getInstance() {
        if (instanceGetLocation == null) {
            instanceGetLocation = new LocationUtil();
        }
        return instanceGetLocation;
    }

    public void initLocationUtil() {
        locationManager = (LocationManager) RuntimeManager.getContext().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();
    }

    public void refreshGPS(ILocationListener iLocationListener) {
        this.iLocationListener = iLocationListener;
        try {
            locationManager.removeUpdates(mLocationListener);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                mGpsTimer = new Timer();
                GpsTimeOutTask mGpsTimeOutTask = new GpsTimeOutTask();
                mGpsTimer.schedule(mGpsTimeOutTask, 300000);
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            }
            if (iLocationListener != null) {
                iLocationListener.notifyChange(REFRESHGPS_COMPLETED, null);
            }
        } catch (SecurityException e) {
            latitude = 39.9;
            longitude = 116.3;
        }
    }

    /**
     * cancel operations of refreshing GPS
     */
    public void cancelRefreshGPS() {
        try {
            if (locationManager != null) {
                locationManager.removeUpdates(mLocationListener);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (iLocationListener != null) {
            iLocationListener.notifyChange(CANCELGPS_COMPLETED, null);
        }
    }

    public void destroy() {
        if (mGpsTimer != null) {
            mGpsTimer.cancel();
        }
        cancelRefreshGPS();
        iLocationListener = null;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private class MyLocationListener implements LocationListener {
        private boolean mLocationReceived = false;

        @Override
        public void onLocationChanged(Location location) {
            if (location != null && !mLocationReceived) {
                mLocationReceived = true;
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                if (iLocationListener != null) {
                    iLocationListener.notifyChange(DEFAULT_LOCATION_COMPLETED, "position:" + latitude + "," + longitude);
                }
            } else if (location == null) {
                if (iLocationListener != null) {
                    iLocationListener.notifyChange(GETLOCATION_FAILED, null);
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            try {
                if (provider.equals(LocationManager.GPS_PROVIDER) && status != LocationProvider.AVAILABLE) {
                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                    } else {
                        locationManager.removeUpdates(mLocationListener);
                        if (iLocationListener != null) {
                            iLocationListener.notifyChange(STATUS_CHANGED, null);
                        }
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * this class is TimerTask for time out of GPS location update
     */
    private class GpsTimeOutTask extends TimerTask {
        public void run() {
            Message message = new Message();
            message.what = 1;
            mGpsTimerHandler.sendMessage(message);
        }
    }

}