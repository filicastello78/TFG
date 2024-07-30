package com.systemsandcloud.tfg;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class GpsSenderService extends Service {
    private static final String TAG="GpsSenderService";
    public static final String ACTION_LOCATION_BROADCAST = GpsSenderService.class.getName() + "LocationBroadcast";
    public static final String MSG_LATITUDE = "msg_latitude";
    public static final String MSG_LONGITUDE = "msg_longitude";
    private static final long MIN_TIME = 2000;
    private static final float MIN_DISTANCE = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sendBroadcastMessage(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE,
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        sendBroadcastMessage(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                }
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    private void sendBroadcastMessage(Location location) {
        if (location != null) {
            Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
            intent.setAction(ACTION_LOCATION_BROADCAST);
           // Log.v(TAG,"enviando location desde gpssenderservice:");
            intent.putExtra(MSG_LATITUDE, location.getLatitude());
            intent.putExtra(MSG_LONGITUDE, location.getLongitude());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
