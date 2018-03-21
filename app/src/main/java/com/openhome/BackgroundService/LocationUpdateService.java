package com.openhome.BackgroundService;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.openhome.R;
import com.openhome.activity.MapsActivity;
import com.openhome.activity.SplashActivity;

public class LocationUpdateService extends Service {
    private static final String TAG = "LocationUpdateService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private locationDbHelper mDbHelper;
    private Context mContext;
    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            checkLocations(location.getLatitude(), location.getLongitude());
        }
        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private void checkLocations(Double lat, Double longi){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(locationsContract.locationsEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        if(cursor != null){
            if (cursor.moveToFirst()){
                do{
                    double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(locationsContract.locationsEntry.COLUMN_LATITUDE)));
                    double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(locationsContract.locationsEntry.COLUMN_LONGITUDE)));
                    double distance = calculateDistance(latitude, longitude, lat, longi);
                    if((distance*1000) < 500){
                        showNotification(distance, latitude, longitude);
                    }
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
    }

    private void showNotification(double dist, double lat, double longi){
        Intent notificationIntent = new Intent(mContext, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Open Home: Proximity Alert!")
                        .setContentText("You are "+ String.valueOf(dist*1000).substring(0,5) +" meters near property")//+String.valueOf(lat)+", "+String.valueOf(longi))
                        .addAction(navigateAction(mContext, String.valueOf(lat), String.valueOf(longi)))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX);
        int mNotificationId = 005;
        NotificationManager mNotifyMgr =
                (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
    private NotificationCompat.Action navigateAction(Context context, String lat, String longi) {
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + lat + "," + longi));
        PendingIntent navigationPendingIntent = PendingIntent.getActivity(
                context,
                110,
                navigationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action navAction = new NotificationCompat.Action(R.drawable.icon_near_by,
                "navigate me!",
                navigationPendingIntent);
        return navAction;
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        mDbHelper = new locationDbHelper(getApplicationContext());
        mContext = getApplicationContext();
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //calculate distance in Km
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
