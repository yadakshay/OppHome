package com.openhome.fragment.agent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.activity.ActivePropertyHazardActivity;
import com.openhome.activity.AgentListingsActivity;
import com.openhome.adapter.AgentHomeAdapter;
import com.openhome.model.DashboardResponse;
import com.openhome.model.HomeContentRequest;
import com.openhome.model.PropertyInfo;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/5/2015.
 */
public class AgentHomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AgentHomeFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private AgentHomeAdapter agentHomeAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progressDialog;

    private TextView myListingsView, totalListingsView, watchlistedCountView, visitorsCount, archivedListingsCount, scannedCodesCountView;

    private List<PropertyInfo> propertyList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_agent_dashboard, container, false);

        myListingsView = (TextView) v.findViewById(R.id.myListingsCount);
        totalListingsView = (TextView) v.findViewById(R.id.totalListingsCount);
        watchlistedCountView = (TextView) v.findViewById(R.id.watchlistAddedCount);
        visitorsCount = (TextView) v.findViewById(R.id.visitorsCount);
        archivedListingsCount = (TextView) v.findViewById(R.id.archivedListingsCount);
        scannedCodesCountView = (TextView) v.findViewById(R.id.scannedQRCount);

        myListingsView.setOnClickListener(this);
        totalListingsView.setOnClickListener(this);
        visitorsCount.setOnClickListener(this);
        archivedListingsCount.setOnClickListener(this);

        loadDashboard();

        return v;
    }


    /**
     * Loads the home page content.
     */
    private void loadDashboard() {
        showProgress(null, "Please wait while we load the dashboard.");
        String userId = ShPrefManager.with(getActivity()).getUserId();
        String authToken = ShPrefManager.with(getActivity()).getToken();
        final HomeContentRequest homeContentRequest = new HomeContentRequest(userId);
        RestClient.getAPI().getDashboardContent(authToken, userId, new RestCallback<DashboardResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getActivity().getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(DashboardResponse dashboardResponse, Response response) {
                hideProgress();
                if (dashboardResponse.getMessage().equalsIgnoreCase("success")) {
                    myListingsView.setText(dashboardResponse.getMyListingsCount() == null ? "0" : dashboardResponse.getMyListingsCount());
                    totalListingsView.setText(dashboardResponse.getTotalListingsCount() == null ? "0" : dashboardResponse.getTotalListingsCount());
                    archivedListingsCount.setText(dashboardResponse.getVisitorsCount() == null ? "0" : dashboardResponse.getArchivedListingsCount());
                    watchlistedCountView.setText(dashboardResponse.getWatchlistCount() == null ? "0" : dashboardResponse.getWatchlistCount());
                    scannedCodesCountView.setText(dashboardResponse.getScannedQRCodesCount() == null ? "0" : dashboardResponse.getScannedQRCodesCount());
                    visitorsCount.setText(dashboardResponse.getMoreInfoRequestedCount() == null ? "0" : dashboardResponse.getVisitorsCount());

                } else {
                    OpenHomeUtils.showToast(getActivity().getApplicationContext(), "Error loading dashboard. Please try again later.", Toast.LENGTH_LONG);
                    myListingsView.setText("-");
                    totalListingsView.setText("-");
                    archivedListingsCount.setText("-");
                    watchlistedCountView.setText("-");
                    scannedCodesCountView.setText("-");
                    visitorsCount.setText("-");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        Bundle bundle = new Bundle();
        if (v == archivedListingsCount) {
            i = new Intent(getActivity(), AgentListingsActivity.class);
            bundle.putInt(ApplicationConstants.LISTINGS_TYPE, ApplicationConstants.ARCHIVED_STATUS);
            i.putExtras(bundle);
            startActivity(i);
        } else if (v == myListingsView) {
            i = new Intent(getActivity(), AgentListingsActivity.class);
          //  i = new Intent(getActivity(), ActivePropertyHazardActivity.class);
            bundle.putInt(ApplicationConstants.LISTINGS_TYPE, ApplicationConstants.CURRENT_STATUS);
            i.putExtras(bundle);
            startActivity(i);
        } else if (v == visitorsCount) {

        } else if (v == totalListingsView) {
            i = new Intent(getActivity(), AgentListingsActivity.class);
            bundle.putInt(ApplicationConstants.LISTINGS_TYPE, ApplicationConstants.ALL_STATUSES);
            i.putExtras(bundle);
            startActivity(i);
        }
    }

    /**
     * Showing the progress dialog
     */
    public void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(getActivity(), title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


}