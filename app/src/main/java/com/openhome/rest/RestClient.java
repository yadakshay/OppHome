package com.openhome.rest;

import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.ShPrefManager;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Bhargav
 */
public class RestClient {
    private static WebAPI webAPI;

    static {
        setupRestClient();
    }

    //
    private RestClient() {
    }

    /**
     * Method to create and return web api
     *
     * @return
     */
    public static WebAPI getAPI() {
        return webAPI;
    }

    // Define the interceptor to add headers
    RequestInterceptor interceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Content-Type", "application/json");
            request.addHeader("User-Agent", "Open-Home-Android");
        }
    };

    /*
    creating the rest adapter with required configuration
     */
    private static void setupRestClient() {
        //OkHttpClient configuration
        // OkHttpClient okHttpClient= App.getInstance().getMyHttpClient();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(ApplicationConstants.CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(ApplicationConstants.WRITE_TIME_OUT, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(ApplicationConstants.READ_TIME_OUT, TimeUnit.MILLISECONDS);

        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(ApplicationConstants.BASE_URL);
        builder.setLogLevel(RestAdapter.LogLevel.FULL);
        builder.setClient(new OkClient(okHttpClient));

        RestAdapter restAdapter = builder.build();
        webAPI = restAdapter.create(WebAPI.class);
    }
}
