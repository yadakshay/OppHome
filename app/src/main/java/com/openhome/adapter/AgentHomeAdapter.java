package com.openhome.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openhome.R;
import com.openhome.model.PropertyInfo;
import com.openhome.utils.ApplicationConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Bhargav on 12/7/2015.
 */
public class AgentHomeAdapter extends
        RecyclerView.Adapter<AgentHomeAdapter.ViewHolder> {

    private Context context;
    private List<PropertyInfo> propertyList;
    private String userId;
    private ProgressDialog progressDialog;
    private char from;

    public AgentHomeAdapter(Context context, List<PropertyInfo> propertyList, String userId, char from) {
        this.context = context;
        this.propertyList = propertyList;
        this.userId = userId;
        this.from = from;
    }

    // Create new views
    @Override
    public AgentHomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.agent_home_list_item, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final int pos = position;
        final PropertyInfo propertyInfo = propertyList.get(position);

        viewHolder.propertyShortDescription.setText(propertyInfo.getShortDescription());
        Picasso.with(context).load(ApplicationConstants.PROPERTY_IMAGE_BASE_URL + propertyInfo.getPropertyId() + ApplicationConstants.IMAGE_PNG_EXTENSION).placeholder(R.drawable.home_default).fit().centerCrop().into(viewHolder.propertyImage);

//        if (from == 'S')
//            viewHolder.propertyShortDescription.setId(Integer.parseInt(propertyInfo.getPropertyId()));
//        else
//            viewHolder.propertyShortDescription.setId(Integer.parseInt(propertyInfo.getWatchlistId()));
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView propertyImage;
        public TextView propertyShortDescription;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            propertyImage = (ImageView) itemLayoutView.findViewById(R.id.propertyImage);
            propertyShortDescription = (TextView) itemLayoutView.findViewById(R.id.propertyShortDescription);
        }

    }

    // method to access in activity after updating selection
    public List<PropertyInfo> getPropertyList() {
        return propertyList;
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
