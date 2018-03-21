package com.openhome.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import com.openhome.R;
import com.openhome.fragment.HomeFragment;
import com.openhome.fragment.MyProfileFragment;
import com.openhome.fragment.SearchFragment;
import com.openhome.fragment.WatchListFragment;
import com.openhome.utils.ErrorLogUtils;
import com.openhome.utils.PermissionUtils;
import com.openhome.utils.ShPrefManager;

/**
 * Created by Bhargav on 11/3/2015.
 */
public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ErrorLogUtils)) {
            Log.d("ACTIVITY", "This is instance of ErrorLogUtils");
            Thread.setDefaultUncaughtExceptionHandler(new ErrorLogUtils(getApplication()));
        }

        TextView splashVersionTextView = (TextView) findViewById(R.id.spashVersionTV);
        String version = null;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            splashVersionTextView.setText("www.opennhome.com " + version);
        } catch (Exception e) {
            splashVersionTextView.setText("www.opennhome.com");
        }
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                navigateToNextScreen();
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void navigateToNextScreen() {
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        if (authToken != null) {
            Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

}
