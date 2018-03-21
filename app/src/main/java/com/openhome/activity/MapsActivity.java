package com.openhome.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openhome.BackgroundService.LocationUpdateService;
import com.openhome.BackgroundService.locationDbHelper;
import com.openhome.BackgroundService.locationsContract;
import com.openhome.R;
import com.openhome.fragment.CustomDialogFragment;
import com.openhome.model.PropertyInfo;
import com.openhome.model.ResponseData;
import com.openhome.model.SearchResponse;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.ErrorLogUtils;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit.client.Response;

/**
 * Created by Bhargav on 7/17/2016.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, CustomDialogFragment.DialogEventListener {

    private GoogleMap mMap;
    private Double[] userLatLong;
    private final int[] MAP_TYPES = {GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    private int curMapTypeIndex = 3;

    // Fixed for now.
    private int proximityRadius = 2;

    List<PropertyInfo> propertiesList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ErrorLogUtils)) {
            Log.d("ACTIVITY", "This is instance of ErrorLogUtils");
            Thread.setDefaultUncaughtExceptionHandler(new ErrorLogUtils(getApplication()));
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupToolbar();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //OpenHomeUtils.showToast(getApplicationContext(), "Maps Ready to use !!", Toast.LENGTH_LONG);
        userLatLong = getLatestAvailableLocation();
        // mMap.setOnInfoWindowClickListener(this);
        if (userLatLong != null) {
            LatLng latLng = new LatLng(userLatLong[0], userLatLong[1]);
            //addMarker(location, "Current Location", true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            drawCircle(latLng);
            initCamera(latLng);
            getNearByOpenHomes(userLatLong[0], userLatLong[1]);
        } else {
            OpenHomeUtils.showToast(getApplicationContext(), "Unable to fetch current location. Defaulting your location to Auckland.", Toast.LENGTH_LONG);
            double defaultLat = -36.847152;
            double defaultLng = 174.766023;
            LatLng latLng = new LatLng(defaultLat, defaultLng);
            //addMarker(location, "Current Location", true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            drawCircle(latLng);
            initCamera(latLng);
            getNearByOpenHomes(defaultLat, defaultLng);
        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                infoWindowClick(marker);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);

        ImageView newsImageView = (ImageView) toolbar.findViewById(R.id.newsImageView);
        newsImageView.setVisibility(View.GONE);
        tvToolbarTitle.setText(getResources().getString(R.string.title_nearby_openhomes).toUpperCase());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        // for Back button
        actionBar.setHomeButtonEnabled(true); // disable the button
        actionBar.setDisplayHomeAsUpEnabled(true); // remove the left caret
        actionBar.setDisplayShowHomeEnabled(true); // remove the icon

        //toolbar.setNavigationIcon(R.drawable.back);
    }

    /**
     * Fetches near by available open homes from the server.
     */
    private void getNearByOpenHomes(double lattitude, double longitude) {
        Log.i("xxxxxxxxxxxxxxxx", "get nearby homes run");
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        String userId = ShPrefManager.with(getApplicationContext()).getUserId();
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we retrieve near by Open Homes...");

        RestClient.getAPI().getNearByProperties(authToken, userId, String.valueOf(lattitude),
                String.valueOf(longitude), String.valueOf(proximityRadius), new RestCallback<SearchResponse>() {
                    @Override
                    public void failure(RestError restError) {
                        hideProgress();
                        showErrorDialog(R.string.error, "There seems to be problem in retrieving near by open homes. Please try after some time.");
                    }

                    @Override
                    public void success(SearchResponse responseData, Response response) {
                        hideProgress();
                        //added by Akshay 26-10-2017
                        addToDatabaseAndStartService(responseData);
                        plotNearByLocations(responseData);
                    }
                });
    }
    /*****function added by Akshay****/
    private void addToDatabaseAndStartService(SearchResponse responseData){
        Toast.makeText(this, "started adding to db!", Toast.LENGTH_SHORT).show();
        Log.i("xxxxxxxxxxxxx", "addingggg to dbbbbbbbbbb");
        locationDbHelper mDbHelper = new locationDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        propertiesList = responseData.getMessage();
        if (propertiesList.size() != 0) {
            for (PropertyInfo propertyInfo : propertiesList) {
                if (propertyInfo.getLongitude() != null && propertyInfo.getLattitude() != null) {
                    String latitude = propertyInfo.getLattitude();
                    String longitude = propertyInfo.getLongitude();
                    ContentValues cv = new ContentValues();
                    cv.put(locationsContract.locationsEntry.COLUMN_LATITUDE, latitude);
                    cv.put(locationsContract.locationsEntry.COLUMN_LONGITUDE, longitude);
                    long insertId = db.insert(locationsContract.locationsEntry.TABLE_NAME, null, cv);
                }
            }
            ContentValues cv = new ContentValues();
            cv.put(locationsContract.locationsEntry.COLUMN_LATITUDE, "19.871828");
            cv.put(locationsContract.locationsEntry.COLUMN_LONGITUDE, "75.352848");
            Toast.makeText(this, "added!", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(this, LocationUpdateService.class);
            long insertId = db.insert(locationsContract.locationsEntry.TABLE_NAME, null, cv);
            startService(serviceIntent);
        }
    }
    /**
     * PropertyInfo Details Screen
     *
     * @param propertyInfo
     */
    private void navigateToPropertyDetailsScreen(PropertyInfo propertyInfo) {
        Intent intent = new Intent(getApplicationContext(), PropertyDetailsActivity.class);
        intent.putExtra("PropertyDetails", propertyInfo);
        startActivity(intent);
    }

    /**
     * Plot all the locations retrieved from server.
     *
     * @param responseData
     */
    private void plotNearByLocations(SearchResponse responseData) {
        try {
            propertiesList = responseData.getMessage();
            if (propertiesList.size() != 0) {
                for (PropertyInfo propertyInfo : propertiesList) {
                    if (propertyInfo.getLongitude() != null && propertyInfo.getLattitude() != null) {
                        LatLng latLng = new LatLng(Double.parseDouble(propertyInfo.getLattitude()), Double.parseDouble(propertyInfo.getLongitude()));
                        addMarker(latLng, propertyInfo.getShortDescription(), propertyInfo.getAddress(), false);
                    }
                }
            } else {
                showErrorDialog(R.string.thank_you, "There are no near by open homes. Thank you.");
            }
        } catch (Exception e) {
            showErrorDialog(R.string.error, "There seems to be problem in retrieving near by open homes. Please try after some time.");
        }
    }

    /**
     * Adds marker to MAP
     *
     * @param latLng
     * @param title
     */
    private void addMarker(LatLng latLng, String title, String snippet, boolean isUserLocation) {

        if (!isUserLocation) {
            MarkerOptions marker = new MarkerOptions().position(latLng).title(title).snippet(snippet);
            //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_pin));
            mMap.addMarker(marker);
        }
    }

    /**
     * Info Window Click listener
     *
     * @param marker
     */
    public void infoWindowClick(Marker marker) {
        String title = marker.getTitle(); // Short Description
        String snippet = marker.getSnippet(); // Address
        try {
            PropertyInfo propertyInfo = null;
            for (PropertyInfo property : propertiesList) {
                if (property.getShortDescription().equalsIgnoreCase(title) && property.getAddress().equalsIgnoreCase(snippet)) {
                    propertyInfo = property;
                    break;
                }
            }
            if (propertyInfo != null) {
                navigateToPropertyDetailsScreen(propertyInfo);
            } else {
                showErrorDialog(R.string.error, "Unable to navigate to property details. Please try after some time.");
            }
        } catch (Exception e) {
            showErrorDialog(R.string.error, "Unable to navigate to property details. Please try after some time.");
        }
    }

    private String getDistanceWithMetrics(String distanceStr) {
        try {
            double distance = Double.parseDouble(distanceStr);
            BigDecimal bd = new BigDecimal(distance);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            if (distance > 1)
                return bd.doubleValue() + " Kms Approx.";
            else {
                distance = Double.parseDouble(distanceStr) * 100;
                bd = new BigDecimal(distance);
                bd = bd.setScale(0, RoundingMode.HALF_UP);
                return bd.doubleValue() + " Meters Approx.";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    /**
     * Sets MAP camera position to user current location.
     *
     * @param latLng
     */
    private void initCamera(LatLng latLng) {
        CameraPosition position = CameraPosition.builder()
                .target(latLng)
                .zoom(14f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);

        mMap.setMapType(MAP_TYPES[curMapTypeIndex]);
        // mMap.setTrafficEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    /**
     * To Draw 2 KM radius circle around user location.
     *
     * @param location
     */
    private void drawCircle(LatLng location) {
        CircleOptions options = new CircleOptions();
        options.center(location);
        //Radius in meters
        options.radius(2000);
//        options.fillColor( getResources()
//                .getColor( R.color.fill_color ) );
//        options.strokeColor( getResources()
//                .getColor( R.color.stroke_color ) );
        options.strokeWidth(10);
        mMap.addCircle(options);
    }

    /**
     * GETs latest available location.
     *
     * @return
     */
    private Double[] getLatestAvailableLocation() {
        // Get the location manager
        try {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat, lon;

            lat = location.getLatitude();
            lon = location.getLongitude();
            //OpenHomeUtils.showToast(getApplicationContext(), lat + " " + lon, Toast.LENGTH_LONG);
            return new Double[]{lat, lon};
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /**
     * Display error dialog
     */
    private void showErrorDialog(int heading, String message) {
        CustomDialogFragment nearByLocationsFragment = CustomDialogFragment.newInstance(heading,
                message, ApplicationConstants.BUTTON_OK, 0);
        nearByLocationsFragment.show(getFragmentManager(), "NearByLocationsFragment");
    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(this, title, message, false);
    }

    @Override
    public void onButtonClick(DialogFragment dialog, int action) {
        dialog.dismiss();
    }
}