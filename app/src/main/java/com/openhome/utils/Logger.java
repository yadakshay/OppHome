package com.openhome.utils;

import android.util.Log;

/**
 * This class handles the logging in the application
 * Created by Bhargav on 10/31/2015.
 */
public class Logger {

    private static final boolean LOG_ENABLED = true;

    /**
     * This static method logs the message[For debugging purpose].
     * @param tag
     * @param message
     */
    public static void logMessage(String tag, String message){
        if (LOG_ENABLED)
            Log.d(tag, message);
    }
}
