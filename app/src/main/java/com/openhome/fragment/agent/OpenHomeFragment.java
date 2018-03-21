package com.openhome.fragment.agent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openhome.R;
import com.openhome.activity.AgentListingsActivity;
import com.openhome.activity.ScannerActivity;
import com.openhome.utils.ApplicationConstants;

/**
 * Created by Bhargav on 12/3/2016.
 */
public class OpenHomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = OpenHomeFragment.class.getSimpleName();

    TextView scannerTextView, currentListingsTextView, archivedListingsTextView;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_open_home, container, false);

        scannerTextView = (TextView) v.findViewById(R.id.ohScanner_Menu);

        //scannerTextView.setVisibility(View.GONE);
        currentListingsTextView = (TextView) v.findViewById(R.id.ohCurrentListings_Menu);
        archivedListingsTextView = (TextView) v.findViewById(R.id.ohArchivedListings_Menu);

        scannerTextView.setOnClickListener(this);
        currentListingsTextView.setOnClickListener(this);
        archivedListingsTextView.setOnClickListener(this);

        return v;
    }

    /**
     * Onclick of menu items.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent i = null;
        Bundle bundle = new Bundle();
        switch (v.getTag().toString()) {
            case "scan":
                i = new Intent(getActivity(), ScannerActivity.class);
                startActivity(i);
                break;
            case "current":
                i = new Intent(getActivity(), AgentListingsActivity.class);
                bundle.putInt(ApplicationConstants.LISTINGS_TYPE, ApplicationConstants.CURRENT_STATUS);
                i.putExtras(bundle);
                startActivity(i);
                break;
            case "archived":
                i = new Intent(getActivity(), AgentListingsActivity.class);
                bundle.putInt(ApplicationConstants.LISTINGS_TYPE, ApplicationConstants.ARCHIVED_STATUS);
                i.putExtras(bundle);
                startActivity(i);
            case "total":
                i = new Intent(getActivity(), AgentListingsActivity.class);
                bundle.putInt(ApplicationConstants.LISTINGS_TYPE, ApplicationConstants.ALL_STATUSES);
                i.putExtras(bundle);
                startActivity(i);
                break;

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