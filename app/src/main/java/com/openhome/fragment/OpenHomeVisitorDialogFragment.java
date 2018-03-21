package com.openhome.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.openhome.R;
import com.openhome.model.Hazards;
import com.openhome.utils.ApplicationConstants;

import java.util.List;

/**
 * Created by Bhargav
 */
public class OpenHomeVisitorDialogFragment extends DialogFragment {

    private List<Hazards> hazardsList;

    public OpenHomeVisitorDialogFragment() {
        //Empty public constructor
    }

    /* The activity that creates an instance of this dialog fragment must
        * implement this interface in order to receive event callbacks.
        * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DialogEventListener {
        public void onUpdateVisitorCount(DialogFragment dialog, int action);
    }

    // Use this instance of the interface to deliver action events
    DialogEventListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogEventListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public static OpenHomeVisitorDialogFragment newInstance(int title, String message, int action, int action2) {
        OpenHomeVisitorDialogFragment dialog = new OpenHomeVisitorDialogFragment();
        Bundle args = new Bundle();
        args.putInt("dialog_title", title);
        args.putString("dialog_message", message);
        args.putInt("dialog_action", action);
        args.putInt("dialog_action2", action2);
        dialog.setArguments(args);

        return dialog;
    }

    public void setHazardsList(List<Hazards> hazardsList) {
        this.hazardsList = hazardsList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.openhome_visitor_dialog, container, false);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
        TextView dialogMessage = (TextView) view.findViewById(R.id.dialogMessage);
        TextView dialogAction = (TextView) view.findViewById(R.id.dialogAction);
        TextView dialogAction2 = (TextView) view.findViewById(R.id.dialogAction2);

        TextView hazardsMessage = (TextView) view.findViewById(R.id.hazardsMessage);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.oh_visitors_hazards);

        Bundle args = getArguments();
        final int action = args.getInt("dialog_action");
        final int action2 = args.getInt("dialog_action2");
        int title = args.getInt("dialog_title");
        String message = args.getString("dialog_message");

        dialogTitle.setText(title);
        //int titleIcon = getString(title).equalsIgnoreCase("SORRY") ? R.drawable.sorry : R.drawable.thankyou_title_icon;
        //dialogTitle.setCompoundDrawablesWithIntrinsicBounds(titleIcon, 0, 0, 0);
        dialogMessage.setText(getSpannableString(message));
        dialogAction.setText(ApplicationConstants.getActionTitle(action));

        if (hazardsList != null) {
            if (hazardsList.size() == 0) {
                linearLayout.setVisibility(View.GONE);
                hazardsMessage.setVisibility(View.GONE);
            } else {
                for (Hazards hazards : hazardsList) {
                    linearLayout.addView(getTextView(hazards));
                }
            }
        } else {
            linearLayout.setVisibility(View.GONE);
            hazardsMessage.setVisibility(View.GONE);
        }


        dialogAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onUpdateVisitorCount(OpenHomeVisitorDialogFragment.this, action);
                } else {
                    OpenHomeVisitorDialogFragment.this.getDialog().cancel();
                }
            }
        });
        if (action2 != 0) {
            dialogAction2.setText(ApplicationConstants.getActionTitle(action2));
            dialogAction2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onUpdateVisitorCount(OpenHomeVisitorDialogFragment.this, action2);
                    } else {
                        OpenHomeVisitorDialogFragment.this.getDialog().cancel();
                    }
                }
            });
        } else {
            dialogAction2.setVisibility(View.GONE);
        }


        //setting dialog properties
        setDialogProperties();

        return view;
    }

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
     * This method creates hazard text view with hazard text.
     *
     * @param hazards
     * @return
     */
    public TextView getTextView(Hazards hazards) {
        String text = "&#8226;  <b>" + hazards.getHazardScenario() + "(" + hazards.getHazardType() + ") - </b>" + hazards.getHazardDescription();
        ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView tv = new TextView(getActivity());
        tv.setLayoutParams(lparams);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(15f);
        tv.setPadding(5, 5, 5, 3);

        tv.setText(Html.fromHtml(text));
        return tv;
    }
}
