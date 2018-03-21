package com.openhome.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.adapter.AgentHomeAdapter;
import com.openhome.model.PropertyInfo;
import com.openhome.model.StandartPropertyListResponse;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.ui.SpaceItemDecoration;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.RecyclerViewItemClickListener;
import com.openhome.utils.ShPrefManager;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/3/2015.
 */
public class AgentListingsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private AgentHomeAdapter agentHomeAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progressDialog;

    private List<PropertyInfo> propertyList;

    private TextView tvToolbarTitle;

    private int listingsType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_listings);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
            listingsType = bundle.getInt(ApplicationConstants.LISTINGS_TYPE);

        mRecyclerView = (RecyclerView) findViewById(R.id.lvAgentPropertiesView);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(3));

        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this, new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (propertyList.size() > 0) {
                    if (propertyList.get(position) != null) {
                        PropertyInfo propertyInfo = propertyList.get(position);
                        Intent intent = new Intent(getApplicationContext(), PropertyDetailsActivity.class);
                        intent.putExtra("PropertyDetails", propertyInfo);
                        startActivity(intent);
                    }
                }
            }
        }));


        setupToolbar();

        loadHomeContent();
    }


    /**
     * Loads the home page content.
     */
    private void loadHomeContent() {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we retrieve current listings.");
        String userId = ShPrefManager.with(this).getUserId();
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        String listingStr = "";
        if (listingsType == ApplicationConstants.ARCHIVED_STATUS)
            listingStr = ApplicationConstants.LISTINGS_TYPE_ARCHIVED;
        else if (listingsType == ApplicationConstants.ALL_STATUSES)
            listingStr = ApplicationConstants.LISTINGS_TYPE_ALL;
        else
            listingStr = ApplicationConstants.LISTINGS_TYPE_CURRENT;
        RestClient.getAPI().getPropertiesByAgent(authToken, userId, listingStr, new RestCallback<StandartPropertyListResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(StandartPropertyListResponse propertyListResponse, Response response) {
                hideProgress();
                if (propertyListResponse.getStatus().equalsIgnoreCase("success")) {

                    propertyList = propertyListResponse.getPropertyInfoList();
                    String userId = ShPrefManager.with(getApplicationContext()).getUserId();
                    agentHomeAdapter = new AgentHomeAdapter(getApplicationContext(), propertyList, userId, 'S');
                    mRecyclerView.setAdapter(agentHomeAdapter);
                } else {
                    OpenHomeUtils.showToast(getApplicationContext(), "No results to display. Please modify the search criteria and try again.", Toast.LENGTH_LONG);
                }
            }
        });
    }

    /**
     * Showing the progress dialog
     */
    public void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(this, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /**
     * To set up tool bar
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        if (listingsType == ApplicationConstants.ALL_STATUSES)
            tvToolbarTitle.setText(getResources().getString(R.string.title_total_listings).toUpperCase());
        else if (listingsType == ApplicationConstants.ARCHIVED_STATUS)
            tvToolbarTitle.setText(getResources().getString(R.string.title_archived_listings).toUpperCase());
        else
            tvToolbarTitle.setText(getResources().getString(R.string.title_current_listings).toUpperCase());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setHomeButtonEnabled(true); // disable the button
        actionBar.setDisplayHomeAsUpEnabled(true); // remove the left caret
        actionBar.setDisplayShowHomeEnabled(true); // remove the icon

    }
}
