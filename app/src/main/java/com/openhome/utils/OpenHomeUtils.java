package com.openhome.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;

/**
 * Created by Bhargav on 10/30/2015.
 */
public class OpenHomeUtils {

    /**
     * For email validation.
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        //TODO: Implement email validation
        return true;
    }

    /**
     * Method to show toast message
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void showToast(Context context, String message, int duration) {
        Toast.makeText(context.getApplicationContext(), message, duration).show();
    }



    /**
     * This method formats the hour and minute to form a TIME string
     *
     * @param hour
     * @param minute
     * @return
     */
    public static String getFormattedTimeString(int hour, int minute) {
        String formattedTime = "";

        String sHour = "00";
        if (hour < 10) {
            sHour = "0" + hour;
        } else {
            sHour = String.valueOf(hour);
        }


        String sMinute = "00";
        if (minute < 10) {
            sMinute = "0" + minute;
        } else {
            sMinute = String.valueOf(minute);
        }

        return sHour + ApplicationConstants.TIME_SEPERATOR + sMinute;
    }
}
