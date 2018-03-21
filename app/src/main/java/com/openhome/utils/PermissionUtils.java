package com.openhome.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Bhargav on 7/17/2016.
 */
public class PermissionUtils {

    public static int MY_PERMISSIONS_ALL = 100;
    public static int MY_PERMISSIONS_READ_CALENDAR = 200;
    public static int MY_PERMISSIONS_WRITE_CALENDAR = 300;
    public static int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 400;
    public static int MY_PERMISSIONS_REQUEST_CAMERA = 500;

    public static void checkPermissions(Context context, Activity activity) {
        int CAMERA = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        int READ_CALENDAR = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR);
        int WRITE_CALENDAR = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR);
        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (CAMERA != PackageManager.PERMISSION_GRANTED || READ_CALENDAR != PackageManager.PERMISSION_GRANTED
                || WRITE_CALENDAR != PackageManager.PERMISSION_GRANTED || WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED
                || ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED || ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_CALENDAR,
                            Manifest.permission.WRITE_CALENDAR, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ALL);
        }
    }


}
