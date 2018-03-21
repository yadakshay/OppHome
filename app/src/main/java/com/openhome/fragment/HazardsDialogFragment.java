package com.openhome.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.adapter.HazardsAdapter;
import com.openhome.adapter.ParentLevelAdapter;
import com.openhome.model.AddPropertyRequest;
import com.openhome.model.Hazards;
import com.openhome.model.ResponseData;
import com.openhome.model.SelectedHazards;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.ui.SpaceItemDecoration;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.CalendarUtils;
import com.openhome.utils.HazardsHolder;
import com.openhome.utils.Logger;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.client.Response;

/**
 * Created by Bhargav
 */
public class HazardsDialogFragment extends DialogFragment implements View.OnClickListener {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button submitButton;

    public static List<Hazards> hazardsList;
    private Set<String> level1List;
    private Map<String, Set<String>> level2Map, level3Map;

    public static AddPropertyRequest addPropertyRequest;

    private ProgressDialog progressDialog;
    HazardsDialogFragment dialogFragment;

    LinearLayout level1HazardsLayout, level3HazardsLayout, level2HazardsLayout;
    TextView indoor, outdoor, other;

    HazardsHolder holder;

    LayoutInflater inflater;

    List<Hazards> selectedHazardsList;

    public HazardsDialogFragment() {
        //Empty public constructor
    }

    /* The activity that creates an instance of this dialog fragment must
        * implement this interface in order to receive event callbacks.
        * Each method passes the DialogFragment in case the host needs to query it. */
    public interface HazardDialogEventListener {
        public void onButtonClick(DialogFragment dialog, AddPropertyRequest addPropertyRequest);
    }

    // Use this instance of the interface to deliver action events
    HazardDialogEventListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (HazardDialogEventListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public static HazardsDialogFragment newInstance(List<Hazards> hazardsList, AddPropertyRequest addPropertyRequest) {
        HazardsDialogFragment dialog = new HazardsDialogFragment();
        Bundle args = new Bundle();
        HazardsDialogFragment.hazardsList = hazardsList;
        HazardsDialogFragment.addPropertyRequest = addPropertyRequest;

        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.dialog_hazards, container, false);

        List<String> listDataHeader = new ArrayList<>();
        selectedHazardsList = new ArrayList<>();

        String[] mItemHeaders = getResources().getStringArray(R.array.items_array_expandable_level_one);
        Collections.addAll(listDataHeader, mItemHeaders);
        final ExpandableListView mExpandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView_Parent);
        if (mExpandableListView != null) {
            ParentLevelAdapter parentLevelAdapter = new ParentLevelAdapter(getActivity(), listDataHeader, hazardsList);
            mExpandableListView.setAdapter(parentLevelAdapter);
            // display only one expand item
//            mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//                int previousGroup = -1;
//                @Override
//                public void onGroupExpand(int groupPosition) {
//                    if (groupPosition != previousGroup)
//                        mExpandableListView.collapseGroup(previousGroup);
//                    previousGroup = groupPosition;
//                }
//            });
        }

//        initializeElements(view, inflater);
        submitButton = (Button) view.findViewById(R.id.property_submit_after_hazards);
        submitButton.setOnClickListener(this);

        Bundle args = getArguments();

        setDialogProperties();
        return view;
    }

    private void initializeElements(View parentView, LayoutInflater inflater) {

       /* level1List = new LinkedHashSet<>();
        level2Map = new HashMap<String, Set<String>>();
        level3Map = new HashMap<String, Set<String>>();

        level1HazardsLayout = (LinearLayout) parentView.findViewById(R.id.hazardsLevel1Layout);
        level2HazardsLayout = (LinearLayout) parentView.findViewById(R.id.hazardsLevel2Layout);
        level3HazardsLayout = (LinearLayout) parentView.findViewById(R.id.hazardsLevel3Layout);

        indoor = (TextView) parentView.findViewById(R.id.indoorHazards);
        outdoor = (TextView) parentView.findViewById(R.id.outdoorHazards);
        other = (TextView) parentView.findViewById(R.id.otherHazards);

        indoor.setOnClickListener(this);
        outdoor.setOnClickListener(this);
        other.setOnClickListener(this);

        holder = new HazardsHolder(hazardsList);

        submitButton = (Button) parentView.findViewById(R.id.property_submit_after_hazards);*/
/*
        HazardsHolder holder = new HazardsHolder(hazardsList);
        HashSet<String> level1 = holder.getHazardTypeList();
        Iterator i = level1.iterator();
        while(i.hasNext()){
            TextView tv = n
            level1HazardsLayout.addView((String)i.next());
        }*/

    }

    /*private void populateHazardMaps() {
        Iterator i = hazardsList.iterator();
        Set indoorSet = new HashSet();
        Set outdoorSet = new HashSet();
        while (i.hasNext()) {
            Hazards hazard = (Hazards) i.next();
            level1List.add(hazard.getHazardType());
            if (hazard.getHazardCode().contains("IN_"))
                indoorSet.add(hazard.getHazardScenario());
            else if (hazard.getHazardCode().contains("OUT_"))
                outdoorSet.add(hazard.getHazardScenario());
        }
        level2Map.put("Indoor Hazards", indoorSet);
        level2Map.put("Outdoor Hazards", outdoorSet);
    }*/


    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.dimAmount = 0.3f;
        window.setAttributes(params);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * Method to display dialog properties like position,cancel
     */
    private void setDialogProperties() {
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
    }

    /**
     * Method to return spannable string for the given string
     *
     * @param message
     * @return
     */
    private SpannableStringBuilder getSpannableString(String message) {
        int startIndex = message.indexOf("\"");
        int lastIndex = message.lastIndexOf("\"") + 1;
        final SpannableStringBuilder sb = new SpannableStringBuilder(message);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.BLACK);
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        if (startIndex != -1 && lastIndex != -1) {
            sb.setSpan(fcs, startIndex, lastIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            sb.setSpan(bss, startIndex, lastIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        return sb;
    }


    /**
     * Onclick listener for Submit Button
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
       if (v == submitButton) {

            for(Hazards h : hazardsList){
                if(h.getDisplay() == 0)
                    selectedHazardsList.add(h);
            }

           int count = selectedHazardsList.size();
           Log.d("Sel " , count + "");
    /*        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                View view = mRecyclerView.getChildAt(i);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.expListItemChkBox);
                TextView hazardIdTextView = (TextView) view.findViewById(R.id.expListItemId);
                TextView hazardDescTextView = (TextView) view.findViewById(R.id.expListItemDesc);
                TextView hazardTypeTextView = (TextView) view.findViewById(R.id.expListItemType);
                if (checkBox.isChecked()) {
                    int id = Integer.parseInt(hazardIdTextView.getText().toString());
                    String desc = hazardDescTextView.getText().toString();
                    String type = hazardTypeTextView.getText().toString();

                    Hazards hazards = new Hazards();
                    hazards.setHazardId(id);
                    hazards.setHazardDescription(desc);
                    hazards.setHazardType(type);
                    selectedHazards.add(hazards);
                }
            }
*/
            addPropertyRequest.setPropertyHazards(selectedHazardsList);

            if (mListener != null) {
                mListener.onButtonClick(HazardsDialogFragment.this, addPropertyRequest);
            } else {
                HazardsDialogFragment.this.getDialog().cancel();
            }

        }
        // addPropertyAfterHazardsSelection();
    }

    private void displayChildHazardViews() {

    }

    private TextView getTextViewWithStyle(LayoutInflater inflater, String textToDisplay) {
        TextView tv = (TextView) inflater.inflate(R.layout.hazard_textview_layout, null);
        tv.setText(textToDisplay);
        //tv.setOnClickListener(this);
        return tv;
    }

    /**
     * Add property after selection of hazards.
     */
    public void addPropertyAfterHazardsSelection() {
        showProgress(null, "Connecting to server...");
        String authToken = ShPrefManager.with(getActivity()).getToken();

        RestClient.getAPI().addProperty(authToken, addPropertyRequest, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                //hideProgress();
                OpenHomeUtils.showToast(getActivity(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                hideProgress();
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                //hideProgress();
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
                            String calID = CalendarUtils.getCalendar(getActivity().getApplicationContext());
                            // CalendarUtils.addCalendarEvent(getApplicationContext(), calID, location.getText().toString(), "Open Home", beginTime.getTimeInMillis(), beginTime.getTimeInMillis(), 0);

                            CalendarUtils.addCalendarContractEvents("Open Home", "Open Home viewing time for the property added by you.",
                                    calID, address.toString(), beginTime.getTimeInMillis(), beginTime.getTimeInMillis(), getActivity().getContentResolver());

                        }
                    } catch (Exception e) {
                        message = "Property added successfully. Open Home timings could not be added to your calendar. Please contact administrator if this problem persists.";
                    }
                } else {
                    message = "Error adding property. Please try again after some time.";
                    dialogFragment = CustomDialogFragment.newInstance(heading,
                            message, ApplicationConstants.BUTTON_OK, 0);
                }
                dialogFragment.show(getActivity().getFragmentManager(), "RegSuccessDialogFragment");
            }
        });

    }

    private void displayScenariosLayout(String text) {
        level1HazardsLayout.setVisibility(View.GONE);
        level2HazardsLayout.setVisibility(View.VISIBLE);
        level3HazardsLayout.setVisibility(View.GONE);

        HashSet<Hazards> set = holder.getScenarioList(text);
        Iterator<Hazards> i = set.iterator();
        level2HazardsLayout.removeAllViews();
        while(i.hasNext()){
            Hazards h = i.next();
            TextView textView = getTextViewWithStyle(inflater, h.getHazardScenario());
            level2HazardsLayout.addView(textView);
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
        // if (progressDialog != null && progressDialog.isShowing())
        try {
            progressDialog.dismiss();
        } catch (Exception e) {

        }
    }


}
