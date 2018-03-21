package com.openhome.fragment;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
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
import com.openhome.activity.PropertyDetailsActivity;
import com.openhome.model.PropertyInfo;
import com.openhome.model.SearchResponse;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 8/1/2016.
 */

public class NearByPropertiesMapFragment extends Fragment implements OnMapReadyCallback, CustomDialogFragment.DialogEventListener {

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

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.activity_maps, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //setupToolbar();
        return view;
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
            OpenHomeUtils.showToast(getActivity().getApplicationContext(), "Unable to fetch current location. Defaulting location to Auckland.", Toast.LENGTH_LONG);
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
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

    }

    /**
     * Fetches near by available open homes from the server.
     */
    private void getNearByOpenHomes(double lattitude, double longitude) {
        String authToken = ShPrefManager.with(getActivity()).getToken();
        String userId = ShPrefManager.with(getActivity()).getUserId();
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
                        addToDatabaseAndStartService(responseData);
                        plotNearByLocations(responseData);
                    }
                });
    }
    /*****function added by Akshay****/
    private void addToDatabaseAndStartService(SearchResponse responseData){
      //  Toast.makeText(getContext(), "started adding to db!", Toast.LENGTH_SHORT).show();
        locationDbHelper mDbHelper = new locationDbHelper(getContext());
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
         //   Toast.makeText(getContext(), "added!", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(getContext(), LocationUpdateService.class);
            long insertId = db.insert(locationsContract.locationsEntry.TABLE_NAME, null, cv);
            getContext().startService(serviceIntent);
        }
    }
    /**
     * PropertyInfo Details Screen
     *
     * @param propertyInfo
     */
    private void navigateToPropertyDetailsScreen(PropertyInfo propertyInfo) {
        Intent intent = new Intent(getActivity(), PropertyDetailsActivity.class);
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
                        addMarker(latLng, propertyInfo.getPropertyId() + "-----" + propertyInfo.getShortDescription(), propertyInfo.getAddress(), false);
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
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
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
            String propertyId = marker.getTitle().split("-----")[0];
            for (PropertyInfo property : propertiesList) {
                if (property.getPropertyId().equalsIgnoreCase(propertyId)) {
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
                .zoom(13f)
                .bearing(0.0f)
                .tilt(1.0f)
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
        options.fillColor(getResources()
                .getColor(R.color.colorPrimary_15_Opaque));
        options.strokeColor(getResources()
                .getColor(R.color.colorPrimary));
        options.strokeWidth(5);
        mMap.addCircle(options);
    }

    /**
     * GETs latest available location.
     *
     * @return
     */
    private Double[] getLatestAvailableLocation() {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat, lon;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
            //OpenHomeUtils.showToast(getApplicationContext(), lat + " " + lon, Toast.LENGTH_LONG);
            return new Double[]{lat, lon};
        } catch (NullPointerException e) {
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
        nearByLocationsFragment.show(getActivity().getFragmentManager(), "NearByLocationsFragment");
    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(getActivity(), title, message, false);
    }

    @Override
    public void onButtonClick(DialogFragment dialog, int action) {
        dialog.dismiss();
    }


    /**
     * To display custom info window for markers
     */
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.marker_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            String propertyId = marker.getTitle().split("-----")[0];
            String shortDesc = marker.getTitle().split("-----")[1];
            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            tvTitle.setText(shortDesc);
            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());
            ImageView imageView = (ImageView) myContentsView.findViewById(R.id.infoWindowImage);
            Picasso.with(getActivity()).load(ApplicationConstants.PROPERTY_IMAGE_BASE_URL + propertyId + ".jpg").placeholder(R.drawable.home_default).resize(50, 50).centerCrop().into(imageView);

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
