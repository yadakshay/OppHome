package com.openhome.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.fragment.CustomDialogFragment;
import com.openhome.fragment.OpenHomeVisitorDialogFragment;
import com.openhome.model.Hazards;
import com.openhome.model.HazardsResponse;
import com.openhome.model.PropertyInfo;
import com.openhome.model.ResponseData;
import com.openhome.model.ScheduleEventRequest;
import com.openhome.model.WatchlistRequest;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.CalendarUtils;
import com.openhome.utils.ErrorLogUtils;
import com.openhome.utils.Logger;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/3/2015.
 */
public class PropertyDetailsActivity extends AppCompatActivity implements CustomDialogFragment.DialogEventListener, OpenHomeVisitorDialogFragment.DialogEventListener, View.OnClickListener {

    ImageView propertyImage, detailsPropertyWatchlistImage;
    TextView shortDesc, location, price, listedOn, bedrooms, bathrooms, floorArea, landArea, propertyType, parking, description;
    TextView detailsViewingDate0, detailsViewingDate1, detailsViewingDate2;
    TextView detailsViewingTime0, detailsViewingTime1, detailsViewingTime2;
    Button addToWatchlistButton, reqMoreInfoButton, moveToARchiveButton, deleteButton, visitOpenHome;

    ImageView addToCalendar3, addToCalendar2, addToCalendar1;

    LinearLayout locationHolderLayout, openHomeTimingsLayout, viewingLinearLayout0, viewingLinearLayout1, viewingLinearLayout2, hazardsDisplayLayout;
    private TextView tvToolbarTitle;

    private ProgressDialog progressDialog;

    private  List<Hazards> hazardsList;

    private ScheduleEventRequest scheduleEventRequest = new ScheduleEventRequest();

    private String propertyId = "", userId = "", userRole = "";
    private int archiveStatus = 0;
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_detail_layout);
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof ErrorLogUtils)) {
            Log.d("ACTIVITY", "This is instance of ErrorLogUtils");
            Thread.setDefaultUncaughtExceptionHandler(new ErrorLogUtils(getApplication()));
        }
        userId = ShPrefManager.with(this).getUserId();
        userRole = ShPrefManager.with(this).getUserType();

        propertyImage = (ImageView) findViewById(R.id.detailsPropertyImage);
        shortDesc = (TextView) findViewById(R.id.detailsShortDesc);
        price = (TextView) findViewById(R.id.detailsPrice);
        listedOn = (TextView) findViewById(R.id.detailsListed);
        location = (TextView) findViewById(R.id.detailsLocation);
        bedrooms = (TextView) findViewById(R.id.detailsBedroom);
        bathrooms = (TextView) findViewById(R.id.detailsBathroom);
        propertyType = (TextView) findViewById(R.id.detailsPropType);
        floorArea = (TextView) findViewById(R.id.detailsFloorArea);
        landArea = (TextView) findViewById(R.id.detailsLandArea);
        parking = (TextView) findViewById(R.id.detailsParking);
        description = (TextView) findViewById(R.id.detailsDescription);

        locationHolderLayout = (LinearLayout) findViewById(R.id.locationHolderLayout);

        addToCalendar3 = (ImageView) findViewById(R.id.addToCalendar3);
        addToCalendar2 = (ImageView) findViewById(R.id.addToCalendar2);
        addToCalendar1 = (ImageView) findViewById(R.id.addToCalendar1);
        addToCalendar1.setOnClickListener(this);
        addToCalendar2.setOnClickListener(this);
        addToCalendar3.setOnClickListener(this);

        detailsPropertyWatchlistImage = (ImageView) findViewById(R.id.detailsPropertyWatchlistImage);
        Intent i = getIntent();
        Serializable serializable = i.getSerializableExtra("PropertyDetails");
        PropertyInfo propertyInfo = (PropertyInfo) serializable;

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (propertyInfo != null) {
            propertyId = propertyInfo.getPropertyId();

            Picasso.with(this).load(ApplicationConstants.PROPERTY_IMAGE_BASE_URL + propertyId + ApplicationConstants.IMAGE_PNG_EXTENSION).placeholder(R.drawable.home_default).resize(dm.widthPixels - 2, dm.widthPixels - 2).centerCrop().into(propertyImage);
            shortDesc.setText(propertyInfo.getShortDescription());

            if (propertyInfo.getMethodOfSale() != null && propertyInfo.getMethodOfSaleValue() != null) {
                price.setText(propertyInfo.getMethodOfSale() + " " + propertyInfo.getMethodOfSaleValue());
            } else if (propertyInfo.getPrice() != null) {
                price.setText("Asking Price: " + propertyInfo.getPrice());
            } else {
                price.setVisibility(View.GONE);
            }

            listedOn.setText("Listed on: " + propertyInfo.getCreatedOn());
            location.setText(propertyInfo.getAddress());
            bedrooms.setText(propertyInfo.getBedrooms());
            bathrooms.setText(propertyInfo.getBathrooms());
            propertyType.setText(propertyInfo.getType());
            floorArea.setText(propertyInfo.getFloorArea());
            landArea.setText(propertyInfo.getLandArea());
            parking.setText(propertyInfo.getParking());
            description.setText(propertyInfo.getDescription());
            archiveStatus = propertyInfo.getIsArchived();
            addToWatchlistButton = (Button) findViewById(R.id.add_to_watchlist_button);
            reqMoreInfoButton = (Button) findViewById(R.id.req_more_info_button);
            moveToARchiveButton = (Button) findViewById(R.id.move_to_archive_button);
            deleteButton = (Button) findViewById(R.id.delete_button);
            visitOpenHome = (Button) findViewById(R.id.visit_openhome);

            if (userRole.equals("A")) {
                // Agent
                reqMoreInfoButton.setVisibility(View.GONE);
                addToWatchlistButton.setVisibility(View.GONE);
                if (propertyInfo.getIsArchived() == ApplicationConstants.ARCHIVED_STATUS) {
                    moveToARchiveButton.setText("Move to Current");
                } else {
                    moveToARchiveButton.setText("Move to Archive");
                }
                visitOpenHome.setVisibility(View.GONE);
            } else {
                // Public
                moveToARchiveButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                if (propertyInfo.isWatching()) {
                    addToWatchlistButton.setText("Remove from Watchlist");
                    //   detailsPropertyWatchlistImage.setImageResource(R.drawable.icon_watchlist_selected);

                } else {
                    addToWatchlistButton.setText("Add to Watchlist");
                    //  detailsPropertyWatchlistImage.setImageResource(R.drawable.icon_watchlist_white);
                }

            }

            detailsViewingTime0 = (TextView) findViewById(R.id.detailsViewingTime0);
            detailsViewingTime1 = (TextView) findViewById(R.id.detailsViewingTime1);
            detailsViewingTime2 = (TextView) findViewById(R.id.detailsViewingTime2);

            detailsViewingDate0 = (TextView) findViewById(R.id.detailsViewingDate0);
            detailsViewingDate1 = (TextView) findViewById(R.id.detailsViewingDate1);
            detailsViewingDate2 = (TextView) findViewById(R.id.detailsViewingDate2);

            viewingLinearLayout0 = (LinearLayout) findViewById(R.id.viewingLayout0);
            viewingLinearLayout1 = (LinearLayout) findViewById(R.id.viewingLayout1);
            viewingLinearLayout2 = (LinearLayout) findViewById(R.id.viewingLayout2);

            String viewingTimes[] = propertyInfo.getViewingTime().split("::::");
            if (viewingTimes.length > 0) {
                int count = 0;
                for (String viewingTime : viewingTimes) {
                    if (!viewingTime.trim().isEmpty() && viewingTime.trim().length() > 5) {
                        addViewingTimeToLayout(viewingTime, count);
                    } else {
                        hideViewingTimeLayout(count);
                    }
                    count++;
                }
            }
            setupListeners();

            scheduleEventRequest.setAgentUserId(propertyInfo.getAgentId());
            scheduleEventRequest.setPropertyId(propertyInfo.getPropertyId());
            scheduleEventRequest.setStatus("BOOKED");
            scheduleEventRequest.setUserId(userId);

            hazardsDisplayLayout = (LinearLayout) findViewById(R.id.hazardsDisplayLayout);

            getPropertyHazards();
        }
        setupToolbar();
    }

    /**
     * Get Property Hazards for the property.
     */
    public void getPropertyHazards() {
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        showProgress("Open Home", "Please wait while we retrieve property details from server.");

        RestClient.getAPI().getPropertyHazards(authToken, propertyId, userId, new RestCallback<HazardsResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
            }

            @Override
            public void success(HazardsResponse response, Response response2) {
                hideProgress();
                hazardsList = response.getMessage().getHazards();
                int moreInfoRequested = response.getMessage().getIsMoreInfoRequested();
                if (moreInfoRequested == ApplicationConstants.MORE_INFO_REQUESTED)
                    reqMoreInfoButton.setVisibility(View.GONE);
                if (hazardsList != null) {
                    if (hazardsList.size() == 0)
                        hazardsDisplayLayout.setVisibility(View.GONE);
                    else
                        for (Hazards hazards : hazardsList) {
                            hazardsDisplayLayout.addView(getTextView(hazards));
                        }
                } else {
                    hazardsDisplayLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * This method creates hazard text view with hazard text.
     *
     * @param hazards
     * @return
     */
    public TextView getTextView(Hazards hazards) {
        String text = "<b>" + hazards.getHazardScenario() + "(" + hazards.getHazardType() + ") - </b>" + hazards.getHazardDescription();
        ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(this);
        tv.setLayoutParams(lparams);
        tv.setTextSize(12f);
        tv.setPadding(5, 5, 5, 3);

        tv.setText(Html.fromHtml(text));
        return tv;
    }

    /**
     * Returns the linear layour by adding properties ( date and time )
     *
     * @param viewingTime
     * @return
     */
    private void addViewingTimeToLayout(String viewingTime, int count) {

        String date = viewingTime.split(" ")[0];
        String time = viewingTime.replace(date, "");
        if (count == 0) {
            detailsViewingDate0.setText(date);
            detailsViewingTime0.setText(time);
            scheduleEventRequest.setScheduledDateTime(date + "::::" + time);
            //addToCalendar1.setTag(scheduleEventRequest);
        } else if (count == 1) {
            detailsViewingDate1.setText(date);
            detailsViewingTime1.setText(time);
            scheduleEventRequest.setScheduledDateTime(date + "::::" + time);
            //addToCalendar2.setTag(scheduleEventRequest);
        } else if (count == 2) {
            detailsViewingDate2.setText(date);
            detailsViewingTime2.setText(time);
            scheduleEventRequest.setScheduledDateTime(date + "::::" + time);
            //addToCalendar3.setTag(scheduleEventRequest);
        }
    }

    /**
     * This method hides the viewing time layout for the given row number.
     *
     * @param count
     */
    private void hideViewingTimeLayout(int count) {
        if (count == 0) {
            viewingLinearLayout0.setVisibility(View.GONE);
        } else if (count == 1) {
            viewingLinearLayout0.setVisibility(View.GONE);
        } else if (count == 2) {
            viewingLinearLayout0.setVisibility(View.GONE);
        }
    }

    /**
     * On click listener
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        addScheduledEventToServer();
    }

    /**
     * Adds shceduled event to server.
     */
    public void addScheduledEventToServer() {

        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait While we schedule this Open Home for you.");
        RestClient.getAPI().addScheduleEvent(authToken, scheduleEventRequest, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                //hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                hideProgress();
                String message = "Oops ! There was an error scheduling Open Home. Please try again later.";
                CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.error,
                        message, ApplicationConstants.BUTTON_OK, 0);
                regSuccessDialogFragment.show(getFragmentManager(), "AddToCalendarFragment");
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                boolean calendarError = addScheduleEventToCalendar();

                // If error
                if (calendarError) {
                    String message = "Open Home scheduled successfully, but could not add the event to your calendar.";
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.error,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getFragmentManager(), "AddToCalendarFragment");
                } else {
                    String message = "Open Home scheduled successfully and the event is added your calendar.";
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getFragmentManager(), "AddToCalendarFragment");
                }
            }
        });
    }

    /**
     * Adds the event to calendar.
     *
     * @return
     */
    private boolean addScheduleEventToCalendar() {
        boolean calendarError = false;
        try {
            String dateTime = scheduleEventRequest.getScheduledDateTime();
            Logger.logMessage("Calendar ID", "Calendar id is " + CalendarUtils.getCalendar(getApplicationContext()));
            //17-3-2016::::09:00 AM - 09:00 AM
            if (!dateTime.trim().isEmpty()) {
                Calendar beginTime = CalendarUtils.getCalendarFromViewingTime(dateTime);
                //startActivity(CalendarUtils.addEventToCalendar(beginTime, beginTime, location.getText().toString()));
                String calID = CalendarUtils.getCalendar(getApplicationContext());
                // CalendarUtils.addCalendarEvent(getApplicationContext(), calID, location.getText().toString(), "Open Home", beginTime.getTimeInMillis(), beginTime.getTimeInMillis(), 0);

                CalendarUtils.addCalendarContractEvents("Open Home", "You've booked to view Open Home.", calID,
                        location.getText().toString(), beginTime.getTimeInMillis(), beginTime.getTimeInMillis(), getContentResolver());
            } else {
                // Error adding to calendar
                calendarError = true;
            }
        } catch (Exception e) {
            // Error adding to calendar
            e.printStackTrace();
            calendarError = true;
        }
        return calendarError;
    }

    /**
     * Gets the Calendar (By default the first calendar)
     *
     * @return
     */
    private String getCalanderSelected() {
        String calName = null;
        String calId = null;
        String[] projection = new String[]{"_id", "name"};
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor =
                managedQuery(calendars, projection, "selected=1", null, null);
        if (managedCursor != null)
            if (managedCursor.moveToFirst()) {

                int nameColumn = managedCursor.getColumnIndex("name");
                int idColumn = managedCursor.getColumnIndex("_id");
                //  do {
                calName = managedCursor.getString(nameColumn);
                calId = managedCursor.getString(idColumn);
                // } while (managedCursor.moveToNext());
            }
        return calId;
    }

    /**
     * To set up tool bar
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.title_property_details).toUpperCase());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setHomeButtonEnabled(true); // disable the button
        actionBar.setDisplayHomeAsUpEnabled(true); // remove the left caret
        actionBar.setDisplayShowHomeEnabled(true); // remove the icon

    }

    /**
     * This method sets up all the required listeners.
     */
    public void setupListeners() {
        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonLabel = addToWatchlistButton.getText().toString();
                boolean add = false;
                if (buttonLabel.equalsIgnoreCase("add to watchlist"))
                    add = true;
                addOrRemoveToWatchlist(add);
            }
        });

        moveToARchiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String buttonLabel = moveToARchiveButton.getText().toString();

                changeArchiveStatus((archiveStatus == ApplicationConstants.CURRENT_STATUS) ? ApplicationConstants.ARCHIVED_STATUS : ApplicationConstants.CURRENT_STATUS);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String buttonLabel = moveToARchiveButton.getText().toString();

                deleteProperty();
            }
        });

        detailsPropertyWatchlistImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonLabel = addToWatchlistButton.getText().toString();
                boolean add = false;
                if (buttonLabel.equalsIgnoreCase("add to watchlist"))
                    add = true;
                addOrRemoveToWatchlist(add);
            }
        });

        reqMoreInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMoreInformation();
            }
        });

        locationHolderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPropertyInMap();
            }
        });

        visitOpenHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateVisitorDialog();
            }
        });


    }

    /**
     * Adds or Removes a property to/from watchlist
     */
    public void addOrRemoveToWatchlist(boolean add) {

        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        if (add) {
            WatchlistRequest watchlistRequest = new WatchlistRequest(userId, propertyId);
            RestClient.getAPI().addToWatchlist(authToken, watchlistRequest, new RestCallback<ResponseData>() {
                @Override
                public void failure(RestError restError) {
                    hideProgress();
                    OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                }

                @Override
                public void success(ResponseData responseData, Response response) {
                    hideProgress();
                    addToWatchlistButton.setText("Remove from Watchlist");
                    //  detailsPropertyWatchlistImage.setImageResource(R.drawable.icon_watchlist_selected);
                    String message = "Property added to watchlist successfully. Please go to watchlist screen to see all the properties in the watchlist.";
                    //    showCustomToast("Property added to watchlist");
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getFragmentManager(), "AddToWatchlistFragment");
                }
            });
        } else {

            RestClient.getAPI().removeFromWatchlist(authToken, propertyId, userId, new RestCallback<ResponseData>() {
                @Override
                public void failure(RestError restError) {
                    hideProgress();
                    OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                }

                @Override
                public void success(ResponseData responseData, Response response) {
                    hideProgress();
                    addToWatchlistButton.setText("Add to Watchlist");
                    // detailsPropertyWatchlistImage.setImageResource(R.drawable.icon_watchlist_white);
                    String message = "Property removed from watchlist successfully. Please go to watchlist screen to see all the properties in the watchlist.";
                    //showCustomToast("Property removed from watchlist");
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getFragmentManager(), "RemovedFromWatchlistFragment");
                }
            });
        }
    }

    /**
     * Request more information on this property.
     */
    public void requestMoreInformation() {

        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        RestClient.getAPI().updatePropertyInfo(authToken, "updateMoreInfo", propertyId, userId, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                String message = "More information requested successfully. Property agent will get in touch with you soon.";
                CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                        message, ApplicationConstants.BUTTON_OK, 0);
                regSuccessDialogFragment.show(getFragmentManager(), "MoreInfoFragment");
            }
        });
    }

    /**
     * Request more information on this property.
     */
    public void changeArchiveStatus(final int status) {

        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        RestClient.getAPI().updateArchiveStatus(authToken, userId, propertyId, status, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                        responseData.getMessage(), ApplicationConstants.BUTTON_OK, 0);
                regSuccessDialogFragment.show(getFragmentManager(), "MoreInfoFragment");
            }
        });
    }

    public void deleteProperty() {
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        RestClient.getAPI().deleteProperty(authToken, propertyId, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                Toast.makeText(getApplicationContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                finishMe();
               /* CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                        responseData.getMessage(), ApplicationConstants.BUTTON_OK, 0);
                regSuccessDialogFragment.show(getFragmentManager(), "MoreInfoFragment");*/
            }
        });
    }


    /**
     * This method updates the visitor count for the property.
     */
    public void updateVisitorCount() {

    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(this, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onButtonClick(DialogFragment dialog, int action) {
        dialog.dismiss();
        setResult(RESULT_OK);
    }

    /**
     * Displays the selected property in Map Intent
     */
    public void showPropertyInMap() {
        String locationStr = location.getText().toString();
        Uri geoLocation = Uri.parse("geo:0,0?q=" + locationStr);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.error,
                    "Please install Google Maps application to use location feature.", ApplicationConstants.BUTTON_OK, 0);
            regSuccessDialogFragment.show(getFragmentManager(), "GoogleMapsIntentFragment");
        }
    }

    public void finishMe() {
        this.finish();
    }

    public void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void showUpdateVisitorDialog() {
        OpenHomeVisitorDialogFragment regSuccessDialogFragment = OpenHomeVisitorDialogFragment.newInstance(R.string.thank_you,
                "Thank you for your interest in this open home. Please click 'OK' to confirm your visit otherwise click cancel.", ApplicationConstants.BUTTON_OK, ApplicationConstants.BUTTON_CANCEL);
        regSuccessDialogFragment.setHazardsList(hazardsList);
        regSuccessDialogFragment.show(getFragmentManager(), "UpdateVisitorFragment");
    }

    private void visitOpenHome() {
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        RestClient.getAPI().updatePropertyInfo(authToken, "updateVisitor", propertyId, userId, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();

            }
        });
    }

    @Override
    public void onUpdateVisitorCount(DialogFragment dialog, int action) {
        if(action == ApplicationConstants.BUTTON_OK){
            visitOpenHome();
        }
        dialog.dismiss();
    }
}
