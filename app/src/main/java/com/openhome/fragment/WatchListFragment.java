package com.openhome.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openhome.R;
import com.openhome.activity.PropertyDetailsActivity;
import com.openhome.adapter.SearchAdapter;
import com.openhome.model.PropertyInfo;
import com.openhome.model.SearchResponse;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.ui.SpaceItemDecoration;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.Logger;
import com.openhome.utils.RecyclerViewItemClickListener;
import com.openhome.utils.ShPrefManager;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/5/2015.
 */
public class WatchListFragment extends Fragment {

    private static final String TAG = WatchListFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PropertyInfo> propertyList;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_watchlist, container, false);
        //TextView tv = (TextView) v.findViewById(R.id.watchlist_text);
        //tv.setText("Nothing to show in watch list.");
        mRecyclerView = (RecyclerView) v.findViewById(R.id.lvWatchlistView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10));
        //tv.setTextColor(Color.parseColor("#FFFFFF"));

        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PropertyInfo propertyInfo = propertyList.get(position);

                Intent intent = new Intent(getActivity(), PropertyDetailsActivity.class);
                intent.putExtra("PropertyDetails", propertyInfo);
                startActivity(intent);
                Logger.logMessage(TAG, "addOnItemTouchListener()::propertyInfo.getPropertyId():: " + propertyInfo.getPropertyId());
            }
        }));
        loadWatchlistItems();
        //tv.setTextColor(Color.parseColor("#FFFFFF"));
        return v;
    }

    /**
     * Method to load watchlist items
     */
    private void loadWatchlistItems() {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we retrieve your watchlisted properties.");
        String userId = ShPrefManager.with(getActivity()).getUserId();
        String authToken = ShPrefManager.with(getActivity()).getToken();
        RestClient.getAPI().getWatchlist(authToken, userId, new RestCallback<SearchResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.error,
                        "There is an error retrieving watchlisted items. Please try again after sometime.", ApplicationConstants.BUTTON_OK, 0);
                regSuccessDialogFragment.show(getActivity().getFragmentManager(), "MyProfileFragment");
            }

            @Override
            public void success(SearchResponse searchResponse, Response response) {
                hideProgress();
                if (searchResponse.getMessage().size() > 0) {
                    propertyList = searchResponse.getMessage();
                    String userId = ShPrefManager.with(getActivity()).getUserId();
                    mAdapter = new SearchAdapter(getActivity(), propertyList, userId, 'W');
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.no_results,
                            "No properties are added to watchlist yet. Thank you.", ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getActivity().getFragmentManager(), "MyProfileFragment");
                }
            }
        });
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