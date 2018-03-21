package com.openhome.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.model.Hazards;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 25/9/2015.
 */
public class HazardsAdapter extends
        RecyclerView.Adapter<HazardsAdapter.ViewHolder> {

    private Context context;
    private List<Hazards> hazardsList;
    private ProgressDialog progressDialog;

    public HazardsAdapter(Context context, List<Hazards> hazardsList) {
        this.context = context;
        this.hazardsList = hazardsList;
    }

    // Create new views
    @Override
    public HazardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.expandable_list_item, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final int pos = position;
        final Hazards hazards = hazardsList.get(position);
        // Using display field to control the checkbox checked status as recycler view recycles the views.
        // By default server returns display value as 1
        if (hazards.getDisplay() != 1)
            viewHolder.chkBox.setChecked(true);
        else
            viewHolder.chkBox.setChecked(false);
        viewHolder.chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hazardsList.get(viewHolder.getAdapterPosition()).setDisplay(isChecked ? 0 : 1);
            }
        });

        viewHolder.id.setText(hazards.getHazardId() + "");
        viewHolder.type.setText(hazards.getHazardScenario() + " (" + hazards.getHazardType() + ")");
        viewHolder.description.setText(hazards.getHazardDescription());
        viewHolder.id.setVisibility(View.GONE);

    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return hazardsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox chkBox;
        public TextView id, type, description;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            chkBox = (CheckBox) itemLayoutView.findViewById(R.id.expListItemChkBox);
            description = (TextView) itemLayoutView.findViewById(R.id.expListItemDesc);
            id = (TextView) itemLayoutView.findViewById(R.id.expListItemId);
            type = (TextView) itemLayoutView.findViewById(R.id.expListItemType);

            // tvMessagesCount = (TextView) itemLayoutView.findViewById(R.id.tvMessageCount);
        }

    }

    // method to access in activity after updating selection
    public List<Hazards> getHazardsList() {
        return hazardsList;
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
