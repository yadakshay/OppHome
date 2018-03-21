package com.openhome;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.openhome.model.ErrorLog;
import com.openhome.utils.ErrorLogUtils;
import com.openhome.utils.TypefaceUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by Bhargav on 11/5/2015.
 */
public class OpenHomeApplication extends MultiDexApplication {
    private static final String TAG = OpenHomeApplication.class.getSimpleName();

    private static OpenHomeApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "MONOSPACE", "fonts/arial.ttf"); // font from assets: "assets/fonts/Roboto-Light.ttf
        instance = this;
        setupUncaughtErrorHandler(this);
    }

    /**
     * Method that returns app instance
     *
     * @return
     */
    public static OpenHomeApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Set up uncaught exception handler
     */
    public void setupUncaughtErrorHandler(final Application application) {
        // Setup handler for uncaught exceptions.
       /* Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
               new ErrorLogUtils(this).handleUncaughtException(thread, e, application);
            }
        });*/
    }


}
