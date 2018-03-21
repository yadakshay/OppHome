package com.openhome.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bhargav on 12/8/2015.
 */
public class ShPrefManager {

    private static final String TAG = ShPrefManager.class.getSimpleName();
    private static final String PREFS_NAME = "OHPrefs";
    private static ShPrefManager instance;
    private SharedPreferences sharedPreferences;

    private ShPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static ShPrefManager with(Context context) {
        if (instance == null) {
            instance = new ShPrefManager(context.getApplicationContext());
        }

        return instance;
    }

    public SharedPreferences.Editor edit() {
        return sharedPreferences.edit();
    }

    public String getUserId() {
        return sharedPreferences.getString("userID", null);
    }

    public String getUserType() {
        return sharedPreferences.getString("userType", null);
    }

    public String getToken() {
        return sharedPreferences.getString("authToken", null);
    }

    public String getUserOriginalType() {
        return sharedPreferences.getString("userOriginalType", null);
    }

    public String getPassword() {
        return sharedPreferences.getString("password", null);
    }

    public void putUserDetails(String userID, String userType, String authToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", userID);
        editor.putString("userType", userType);
        editor.putString("authToken", authToken);
        editor.apply();
    }

    public void putUserOriginalType(String userOriginalType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userOriginalType", userOriginalType);
        editor.apply();
    }

    public void invalidateToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("authToken", null);
        editor.apply();
    }

}
