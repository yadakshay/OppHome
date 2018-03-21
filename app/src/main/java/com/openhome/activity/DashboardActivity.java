package com.openhome.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.fragment.HazardsDialogFragment;
import com.openhome.fragment.NearByPropertiesMapFragment;
import com.openhome.fragment.agent.AgentHomeFragment;
import com.openhome.fragment.CustomDialogFragment;
import com.openhome.fragment.ImageDialogFragment;
import com.openhome.fragment.MyProfileFragment;
import com.openhome.fragment.SearchFragment;
import com.openhome.fragment.SettingsFragment;
import com.openhome.fragment.WatchListFragment;
import com.openhome.fragment.agent.AddPropertyFragment;
import com.openhome.fragment.agent.OpenHomeFragment;
import com.openhome.model.AddPropertyRequest;
import com.openhome.model.ResponseData;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.CalendarUtils;
import com.openhome.utils.ErrorLogUtils;
import com.openhome.utils.Logger;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import java.util.Calendar;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/3/2015.
 */
public class DashboardActivity extends AppCompatActivity implements CustomDialogFragment.DialogEventListener, ImageDialogFragment.DialogEventListener, HazardsDialogFragment.HazardDialogEventListener  {
    private FragmentTabHost mTabHost;
    private TextView tvToolbarTitle;
    private EditText searchView;

    private ProgressDialog progressDialog;

    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard_tabs);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ErrorLogUtils)) {
            Log.d("ACTIVITY", "This is instance of ErrorLogUtils");
            Thread.setDefaultUncaughtExceptionHandler(new ErrorLogUtils(getApplication()));
        }
        userType = ShPrefManager.with(this).getUserType();

        setupToolbar("tab1");

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setStripEnabled(true);
        mTabHost.getTabWidget().setRightStripDrawable(R.drawable.tab_line);
        mTabHost.getTabWidget().setLeftStripDrawable(R.drawable.tab_line);
        // Set up Tabs
        setupTabsBasedOnRole();

        // Setting Tab Host Title text color white.
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FF0000")); // unselected
//            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
//            tv.setTextColor(Color.parseColor("#ffffff"));
//            tv.setAllCaps(false);
//            tv.setTextSize(12);

            View v = mTabHost.getTabWidget().getChildAt(i);
            v.setBackgroundResource(R.color.colorWhite);
        }

        //  mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFC002"));
        mTabHost.setOnTabChangedListener(tabChangeListener);

    }

    /**
     * This method will setup tabs based on logged user's role (Agent/Public)
     */
    private void setupTabsBasedOnRole() {
        userType = ShPrefManager.with(this).getUserType() == null ? "P" : ShPrefManager.with(this).getUserType();
        if (userType.equalsIgnoreCase("A")) {
            // AGENT
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab1").setIndicator(getString(R.string.tab1_title), null),
                    AgentHomeFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab2").setIndicator(getString(R.string.tab2_agent_title), null),
                    AddPropertyFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab3").setIndicator(getString(R.string.tab3_agent_title), null),
                    OpenHomeFragment.class, null);

            setTabIcon(mTabHost, 1, R.drawable.icon_add_property);
            setTabIcon(mTabHost, 2, R.drawable.icon_oh);

        } else {
            // PUBLIC
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab1").setIndicator(getString(R.string.tab1_public_title), null),
                    NearByPropertiesMapFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab2").setIndicator(getString(R.string.tab2_title), null),
                    SearchFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab3").setIndicator(getString(R.string.tab3_title), null),
                    WatchListFragment.class, null);

            setTabIcon(mTabHost, 1, R.drawable.icon_search);
            setTabIcon(mTabHost, 2, R.drawable.icon_watchlist);

        }
        mTabHost.addTab(
                mTabHost.newTabSpec("tab4").setIndicator(getString(R.string.tab4_title), null),
                MyProfileFragment.class, null);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab5").setIndicator(getString(R.string.tab5_title), null),
                SettingsFragment.class, null);

        setTabIcon(mTabHost, 0, R.drawable.icon_home_selected);
        setTabIcon(mTabHost, 3, R.drawable.icon_profile);
        setTabIcon(mTabHost, 4, R.drawable.icon_settings);
    }

    public void setTabIcon(TabHost tabHost, int tabIndex, int iconResource) {
        ImageView tabImageView = (ImageView) tabHost.getTabWidget().getChildTabViewAt(tabIndex).findViewById(android.R.id.icon);
        tabImageView.setVisibility(View.VISIBLE);
        tabImageView.setImageResource(iconResource);
    }

    /**
     * To set up tool bar
     */
    private void setupToolbar(String activeTab) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        if (userType.equalsIgnoreCase("P"))
            tvToolbarTitle.setText(getResources().getString(R.string.tab1_public_title));
        else
            tvToolbarTitle.setText(getResources().getString(R.string.title_dashboard));
        searchView = (EditText) toolbar.findViewById(R.id.searchTextView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setHomeButtonEnabled(false); // disable the button
        actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
        actionBar.setDisplayShowHomeEnabled(false); // remove the icon
        //toolbar.setNavigationIcon(R.drawable.back);
        if (activeTab.equalsIgnoreCase("tab2") && userType.equalsIgnoreCase("P")) {
            searchView.setVisibility(View.VISIBLE);
            tvToolbarTitle.setVisibility(View.GONE);
        } else {
            searchView.setVisibility(View.GONE);
            tvToolbarTitle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Tab Change listener.
     */
    TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

        @Override
        public void onTabChanged(String tabId) {
            setupToolbar(tabId);
            // Set Header
            if (tabId.equalsIgnoreCase("tab1")) {
                if (userType.equalsIgnoreCase("P"))
                    tvToolbarTitle.setText(getResources().getString(R.string.tab1_public_title));
                else
                    tvToolbarTitle.setText(getResources().getString(R.string.title_dashboard));
                setTabIcon(mTabHost, 0, R.drawable.icon_home_selected);
            } else {
                setTabIcon(mTabHost, 0, R.drawable.icon_home);
            }

            // Tab 2
            if (tabId.equalsIgnoreCase("tab2")) {
                if (userType.equalsIgnoreCase("P")) {
                    tvToolbarTitle.setText(getResources().getString(R.string.title_search));
                    setTabIcon(mTabHost, 1, R.drawable.icon_search_selected);
                }
                if (userType.equalsIgnoreCase("A")) {
                    tvToolbarTitle.setText(getResources().getString(R.string.title_add_property));
                    setTabIcon(mTabHost, 1, R.drawable.icon_add_property_selected);
                }
            } else {
                if (userType.equalsIgnoreCase("P")) {
                    setTabIcon(mTabHost, 1, R.drawable.icon_search);
                }
                if (userType.equalsIgnoreCase("A")) {
                    setTabIcon(mTabHost, 1, R.drawable.icon_add_property);
                }
            }

            // Tab 3
            if (tabId.equalsIgnoreCase("tab3")) {
                if (userType.equalsIgnoreCase("P")) {
                    tvToolbarTitle.setText(getResources().getString(R.string.title_watchlist));
                    setTabIcon(mTabHost, 2, R.drawable.icon_watchlist_selected);
                }
                if (userType.equalsIgnoreCase("A")) {
                    tvToolbarTitle.setText(getResources().getString(R.string.title_open_home));
                    setTabIcon(mTabHost, 2, R.drawable.icon_oh_selected);
                }
            } else {
                if (userType.equalsIgnoreCase("P")) {
                    setTabIcon(mTabHost, 2, R.drawable.icon_watchlist);
                }
                if (userType.equalsIgnoreCase("A")) {
                    setTabIcon(mTabHost, 2, R.drawable.icon_oh);
                }

            }

            // Tab 4
            if (tabId.equalsIgnoreCase("tab4")) {
                tvToolbarTitle.setText(getResources().getString(R.string.title_profile));
                setTabIcon(mTabHost, 3, R.drawable.icon_profile_selected);
            } else {
                setTabIcon(mTabHost, 3, R.drawable.icon_profile);
            }

            // Tab 5
            if (tabId.equalsIgnoreCase("tab5")) {
                tvToolbarTitle.setText(getResources().getString(R.string.title_settings));
                setTabIcon(mTabHost, 4, R.drawable.icon_settings_selected);
            } else {
                setTabIcon(mTabHost, 4, R.drawable.icon_settings);
            }

            // Change tab color for unselected tabs
            for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                // mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#19385F")); // unselected
                View v = mTabHost.getTabWidget().getChildAt(i);
                v.setBackgroundResource(R.color.colorWhite);
            }
            // Changing the background of selected Tab.
            //View v = mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab());
            //v.setBackgroundResource(R.drawable.orange_line);// selected
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public void onButtonClick(DialogFragment dialog, int action) {
        dialog.dismiss();
    }


    /**
     * Receive call back from hazards dialog
     * @param dialog
     * @param addPropertyRequest
     */
    @Override
    public void onButtonClick(DialogFragment dialog, final AddPropertyRequest addPropertyRequest) {
        dialog.dismiss();
        showProgress("Open Home", "Please wait while we add the property details.");
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();

        RestClient.getAPI().addProperty(authToken, addPropertyRequest, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                hideProgress();
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                Logger.logMessage("success()::::responseData.getStatus():: ", responseData.getStatus());
                int heading = R.string.error;
                String message = "";
                CustomDialogFragment dialogFragment;
                if (responseData.getStatus().equalsIgnoreCase(getString(R.string.success))) {
                    heading = R.string.thank_you;
                    message = "Property added successfully. Open Home timings will be added to your calendar";
                    dialogFragment = CustomDialogFragment.newInstance(heading,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    try {
                        String viewingTimes = addPropertyRequest.getViewingTime();
                        String address = addPropertyRequest.getAddress();
                        String splitViewingTimes[] = viewingTimes.split("::::");
                        for (String viewTime : splitViewingTimes) {
                            String date = viewTime.split(" ")[0];
                            String time = viewTime.replace(date, "");
                            Calendar beginTime = CalendarUtils.getCalendarFromViewingTime(date + "::::" + time);
                            //startActivity(CalendarUtils.addEventToCalendar(beginTime, beginTime, location.getText().toString()));
                            String calID = CalendarUtils.getCalendar(getApplicationContext());
                            // CalendarUtils.addCalendarEvent(getApplicationContext(), calID, location.getText().toString(), "Open Home", beginTime.getTimeInMillis(), beginTime.getTimeInMillis(), 0);

                            CalendarUtils.addCalendarContractEvents("Open Home", "Open Home viewing time for the property added by you.",
                                    calID, address.toString(), beginTime.getTimeInMillis(), beginTime.getTimeInMillis(),getContentResolver());

                        }
                    } catch (Exception e) {
                        message = "Property added successfully. Open Home timings could not be added to your calendar. Please contact administrator if this problem persists.";
                    }
                } else {
                    message = "Error adding property. Please try again after some time.";
                    dialogFragment = CustomDialogFragment.newInstance(heading,
                            message, ApplicationConstants.BUTTON_OK, 0);
                }
                dialogFragment.show(getFragmentManager(), "RegSuccessDialogFragment");
            }
        });
    }

    /**
     * Showing the progress dialog
     */
    public void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(DashboardActivity.this, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    public void hideProgress() {
        // if (progressDialog != null && progressDialog.isShowing())
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
}
