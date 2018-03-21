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
import com.openhome.model.RegionsResponse;
import com.openhome.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 1/13/2016.
 */
public class RegionsAdapter extends RecyclerView.Adapter<RegionsAdapter.ViewHolder> {

    private Context context;
    List<RegionsResponse.Regions> regionsList;

    private int parentIdOnDisplay;
    private int previousParentIdOnDisplay;
    private ProgressDialog progressDialog;


    public int getParentIdOnDisplay() {
        return parentIdOnDisplay;
    }

    public void setParentIdOnDisplay(int parentIdOnDisplay) {
        this.parentIdOnDisplay = parentIdOnDisplay;
    }

    public int getPreviousParentIdOnDisplay() {
        return previousParentIdOnDisplay;
    }

    public void setPreviousParentIdOnDisplay(int previousParentIdOnDisplay) {
        this.previousParentIdOnDisplay = previousParentIdOnDisplay;
    }

    public List<RegionsResponse.Regions> getRegionsList() {
        return regionsList;
    }

    public void setRegionsList(List<RegionsResponse.Regions> regionsList) {
        this.regionsList = regionsList;
    }

    public RegionsAdapter(Context context, List<RegionsResponse.Regions> regionsList, int parentIdOnDisplay) {
        this.context = context;
        this.regionsList = regionsList;
        this.parentIdOnDisplay = parentIdOnDisplay;
    }

    // Create new views
    @Override
    public RegionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.regions_list_item, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        //displayLevel = 1;
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return regionsList.size();
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final int pos = position;
        RegionsResponse.Regions region = regionsList.get(position);
        ;

        if (region != null) {
            viewHolder.tvRegionName.setText(region.getRegionName());
            viewHolder.tvRegionName.setId(region.getRegionId());

            if(region.getLevel() == 3){
                if(region.isSelected()) {
                    viewHolder.regionSelectedImageView.setVisibility(View.VISIBLE);
                    viewHolder.regionSelectedImageView.setImageResource(R.drawable.selected_icon);
                }else{
                    viewHolder.regionSelectedImageView.setVisibility(View.GONE);
                }
            }else{
                viewHolder.regionSelectedImageView.setVisibility(View.VISIBLE);
                viewHolder.regionSelectedImageView.setImageResource(R.drawable.icon_region_list_arrow);
            }
            Logger.logMessage("REgions adapter :: getisselected " , ""+region.isSelected());
//            if(region.isSelected()) {
//                viewHolder.regionSelectedImageView.setVisibility(View.VISIBLE);
//                viewHolder.regionSelectedImageView.setImageResource(R.drawable.selected_icon);
//            }
//            else {
//                viewHolder.regionSelectedImageView.setVisibility(View.GONE);
//            }

        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvRegionName;
        public ImageView regionSelectedImageView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvRegionName = (TextView) itemLayoutView.findViewById(R.id.tvRegionName);
            regionSelectedImageView = (ImageView) itemLayoutView.findViewById(R.id.regionSelectedImageView);
        }
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
