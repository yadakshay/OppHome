package com.openhome.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.openhome.model.DashboardResponse;
import com.openhome.model.ErrorLog;
import com.openhome.model.ResponseData;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import retrofit.client.Response;

/**
 * Created by Bhargav on 3/23/2017.
 */

public class ErrorLogUtils implements Thread.UncaughtExceptionHandler {


    private Thread.UncaughtExceptionHandler defaultUEH;

    private Application application;

    public ErrorLogUtils(Application application) {
        Log.d("ErrorLogUtils", "This is instance of ErrorLogUtils constructor");
        this.application = application;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable e) {
        Log.d("ErrorLogUtils", "This is instance of ErrorLogUtils uncaughtException(Thread thread, Throwable e)");
        handleUncaughtException(thread, e);
        defaultUEH.uncaughtException(thread, e);
    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        Log.d("ErrorLogUtils", "This is instance of ErrorLogUtils handleUncaughtException(Thread thread, Throwable e)");
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        String message = e.getMessage();
        String exceptionType = e.getClass().getName();
        ErrorLog errorLog = extractLogs();
        errorLog.setErrorMessage(message);
        errorLog.setExceptionType(exceptionType);
        Log.d("ErrorLogUtils", "getAndroidSdkInt " + errorLog.getAndroidSdkInt() );
        Log.d("ErrorLogUtils", "exceptionType " + exceptionType);
        Log.d("ErrorLogUtils", "errorLog.getErrorMessage()  " + errorLog.getErrorMessage() );
        Log.d("ErrorLogUtils", "getDeviceModel " + errorLog.getDeviceModel() );
        Log.d("ErrorLogUtils", "getExtractedLog " + errorLog.getExtractedLog() );


        RestClient.getAPI().reportErrorToServer(errorLog, new RestCallback<String>() {
            @Override
            public void failure(RestError restError) {

            }

            @Override
            public void success(String responseData, Response response) {

            }
        });
    }

    public ErrorLog extractLogs() {
        Log.d("ErrorLogUtils", "This is instance of ErrorLogUtils extractLogs()");
        ErrorLog errorLog = new ErrorLog();
        PackageManager manager = application.getPackageManager();
        PackageInfo info = null;
        int appVersionCode = 0;
        String appVersionName = "";
        try {
            info = manager.getPackageInfo(application.getPackageName(), 0);
            appVersionCode = info.versionCode;
            appVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e2) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;
        int androidVersion = Build.VERSION.SDK_INT;
        InputStreamReader reader = null;
        try {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
                    "logcat -d -v time";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            String log = convertStreamToString(process.getInputStream());

            errorLog.setAppVersionCode(appVersionCode);
            errorLog.setAndroidSdkInt(androidVersion);
            errorLog.setAppVersionName(appVersionName);
            errorLog.setExtractedLog(log);
        } catch (Exception e) {
        }
        return errorLog;
    }


    private String convertStreamToString(InputStream is) {
        Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private class ReportErrorAsyncTask extends AsyncTask<ErrorLog, Void, String> {

        @Override
        protected String doInBackground(ErrorLog... params) {
            Log.d("ReportErrorAsyncTask", "ErrorLogErrorLogErrorLog");
            RestClient.getAPI().reportErrorToServer(params[0], new RestCallback<String>() {
                @Override
                public void failure(RestError restError) {

                }

                @Override
                public void success(String responseData, Response response) {

                }
            });
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

//    String timestamp = TimestampFormatter.getInstance().getTimestamp();
//    final Writer result = new StringWriter();
//    final PrintWriter printWriter = new PrintWriter(result);
//    e.printStackTrace(printWriter);
//    String stacktrace = result.toString();
//    printWriter.close();

}
