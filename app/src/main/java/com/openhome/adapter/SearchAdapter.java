package com.openhome.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.model.PropertyInfo;
import com.openhome.model.ResponseData;
import com.openhome.model.WatchlistRequest;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.Logger;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 12/7/2015.
 */
public class SearchAdapter extends
        RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<PropertyInfo> propertyList;
    private String userId;
    private ProgressDialog progressDialog;
    private char from;

    public SearchAdapter(Context context, List<PropertyInfo> propertyList, String userId, char from) {
        this.context = context;
        this.propertyList = propertyList;
        this.userId = userId;
        this.from = from;
    }

    // Create new views
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.search_list_item, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final int pos = position;
        final PropertyInfo propertyInfo = propertyList.get(position);

        viewHolder.propertyShortDescription.setText((propertyInfo.getShortDescription() != null) ? propertyInfo.getShortDescription().toUpperCase() : "");
        viewHolder.propertyAddress.setText(propertyInfo.getAddress() != null ? propertyInfo.getAddress() : "");

        if(propertyInfo.getMethodOfSale() != null && propertyInfo.getMethodOfSaleValue() != null){
            viewHolder.propertyAskingPrice.setText(propertyInfo.getMethodOfSale() + " " + propertyInfo.getMethodOfSaleValue());
        }else if(propertyInfo.getPrice() != null){
            viewHolder.propertyAskingPrice.setText("Asking Price: " + propertyInfo.getPrice());
        }else{
            viewHolder.propertyAskingPrice.setText("");
        }

        if (from == 'S')
            viewHolder.propertyShortDescription.setId(Integer.parseInt(propertyInfo.getPropertyId()));
        else
            viewHolder.propertyShortDescription.setId(Integer.parseInt(propertyInfo.getWatchlistId()));

        viewHolder.propertyBedrooms.setText(propertyInfo.getBedrooms() != null ? propertyInfo.getBedrooms() + " Bedrooms" : "");
        viewHolder.propertyBathrooms.setText(propertyInfo.getBathrooms() != null ? propertyInfo.getBathrooms() + " Bathrooms" : "");
        viewHolder.propertyFloorArea.setText(propertyInfo.getFloorArea() != null ? "Floor Area - " + propertyInfo.getFloorArea() : "");
//        if (propertyInfo.isWatching())
//            viewHolder.tvPropertyType.setImageResource(R.drawable.watchlist_selected);
//        else
//            viewHolder.tvPropertyType.setImageResource(R.drawable.watchlist_unselected);
        Picasso.with(context).load(ApplicationConstants.PROPERTY_IMAGE_BASE_URL + propertyInfo.getPropertyId() + ".jpg").placeholder(R.drawable.home_default).fit().centerCrop().into(viewHolder.propertyImage);

        viewHolder.propertyShortDescription.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Logger.logMessage("SearchAdapter", "OnClick()::Image:: " + v.getId());

                        if (propertyInfo.isWatching()) {
                            if (from == 'S') {
                                propertyList.remove(pos);
                                propertyInfo.setWatching(false);
                            } else {
                                propertyList.remove(pos);
                                showProgress("Watchlist", "Removing property to watchlist.");
                                removeFromWatchlist(v.getId() + "");
                            }

                            //OpenHomeUtils.showToast(context.getApplicationContext(), "Property removed from watchlist.", Toast.LENGTH_LONG);
                        } else {
                            showProgress("Watchlist", "Adding property to watchlist.");
                            propertyInfo.setWatching(true);
                            // propertyList.remove(pos);
                            addPropertyToWatchlist(v.getId() + "");
                        }
                        hideProgress();
                        notifyDataSetChanged();
                    }
                });
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView propertyImage;
        public TextView propertyShortDescription, propertyAddress, propertyAskingPrice, propertyFloorArea, propertyBathrooms, propertyBedrooms;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            propertyImage = (ImageView) itemLayoutView.findViewById(R.id.propertyImage);

            propertyShortDescription = (TextView) itemLayoutView.findViewById(R.id.propertyShortDescription);
            propertyAddress = (TextView) itemLayoutView.findViewById(R.id.propertyAddress);
            propertyAskingPrice = (TextView) itemLayoutView.findViewById(R.id.propertyAskingPrice);

            propertyFloorArea = (TextView) itemLayoutView.findViewById(R.id.propertyFloorArea);
            propertyBathrooms = (TextView) itemLayoutView.findViewById(R.id.propertyBathrooms);
            propertyBedrooms = (TextView) itemLayoutView.findViewById(R.id.propertyBedrooms);

            // tvMessagesCount = (TextView) itemLayoutView.findViewById(R.id.tvMessageCount);
        }

    }

    // method to access in activity after updating selection
    public List<PropertyInfo> getPropertyList() {
        return propertyList;
    }


    private void addPropertyToWatchlist(String propertyId) {
        WatchlistRequest watchlistRequest = new WatchlistRequest(userId, propertyId);
        String authToken = ShPrefManager.with(context).getToken();
        RestClient.getAPI().addToWatchlist(authToken, watchlistRequest, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(context.getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                OpenHomeUtils.showToast(context.getApplicationContext(), "Property added to watchlist successfully.", Toast.LENGTH_LONG);

            }
        });
    }

    private void removeFromWatchlist(String propertyId) {
        WatchlistRequest watchlistRequest = new WatchlistRequest(userId, propertyId);
        String authToken = ShPrefManager.with(context).getToken();
        RestClient.getAPI().removeFromWatchlist(authToken, propertyId, userId, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(context.getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                OpenHomeUtils.showToast(context.getApplicationContext(), "Property removed from watchlist successfully.", Toast.LENGTH_LONG);

            }
        });
    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(context, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

}
