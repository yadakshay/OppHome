package com.openhome.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.openhome.R;
import com.openhome.utils.ApplicationConstants;
import com.squareup.picasso.Picasso;

/**
 * Created by Bhargav
 */
public class ImageDialogFragment extends DialogFragment {

    private static String userId;
    public ImageDialogFragment() {
        //Empty public constructor
    }

    /* The activity that creates an instance of this dialog fragment must
        * implement this interface in order to receive event callbacks.
        * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DialogEventListener {
        public void onButtonClick(DialogFragment dialog, int action);
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

    public static ImageDialogFragment newInstance(String userIds, String heading) {
        ImageDialogFragment dialog = new ImageDialogFragment();
        Bundle args = new Bundle();
        ImageDialogFragment.userId = userIds;
        args.putString("title", heading);
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
        View view = inflater.inflate(R.layout.image_dialog, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.qrCodeLarge);

        Bundle args = getArguments();
        Picasso.with(getActivity()).load(ApplicationConstants.QRCODE_IMAGE_BASE_URL + userId+ ".png").placeholder(R.drawable.default_profile).fit().centerCrop().into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDialogFragment.this.getDialog().cancel();
            }
        });

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
        window.setGravity(Gravity.CENTER);
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
}
