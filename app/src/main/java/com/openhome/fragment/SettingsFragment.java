package com.openhome.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.activity.ChangePasswordActivity;
import com.openhome.activity.LoginActivity;
import com.openhome.activity.MapsActivity;
import com.openhome.activity.PrivacyPolicyActivity;
import com.openhome.activity.PropertyDetailsActivity;
import com.openhome.model.ChangePasswordRequest;
import com.openhome.model.HomeContentRequest;
import com.openhome.model.HomeContentResponse;
import com.openhome.model.PropertyInfo;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.Logger;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/5/2015.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    LinearLayout hotPropertiesLayout, savedSearchLayout;
    TextView hotTextView, savedTextView, ohSwitchUserMenu, ohNearByMenu, ohLogoutMenu, ohChangePwdMenu, ohPrivacyPolicyMenu;
    private ProgressDialog progressDialog;
    private List<PropertyInfo> hotPropertiesList, savedPropertiesList;
    String userType, userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        ohSwitchUserMenu = (TextView) v.findViewById(R.id.ohSwitchUser_Menu);
        ohNearByMenu = (TextView) v.findViewById(R.id.ohNearBy_Menu);
        ohLogoutMenu = (TextView) v.findViewById(R.id.ohLogout_Menu);
        ohChangePwdMenu = (TextView) v.findViewById(R.id.ohChangePassword_Menu);
        ohPrivacyPolicyMenu = (TextView) v.findViewById(R.id.ohPrivacyPolicy_Menu);

        userType = ShPrefManager.with(getActivity()).getUserType() == null ? "A" : ShPrefManager.with(getActivity()).getUserType();
        userId = ShPrefManager.with(getActivity()).getUserId();
        String userOriginalType = ShPrefManager.with(getActivity()).getUserOriginalType();
        if (!userOriginalType.equals("A")) {
            ohSwitchUserMenu.setVisibility(View.GONE);
        }

        ohSwitchUserMenu.setOnClickListener(this);
        ohNearByMenu.setOnClickListener(this);
        ohLogoutMenu.setOnClickListener(this);
        ohChangePwdMenu.setOnClickListener(this);
        ohPrivacyPolicyMenu.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v == ohSwitchUserMenu) {
            if (userType.equals("A")) {
                ShPrefManager.with(getActivity()).putUserDetails(userId, "P", ShPrefManager.with(getActivity()).getToken());
            } else if (userType.equals("P")) {
                ShPrefManager.with(getActivity()).putUserDetails(userId, "A", ShPrefManager.with(getActivity()).getToken());
            }
            getActivity().finish();
            getActivity().startActivity(getActivity().getIntent());
        } else if (v == ohNearByMenu) {
            Intent i = new Intent(getActivity(), MapsActivity.class);
            startActivity(i);
        } else if (v == ohLogoutMenu) {
            ShPrefManager.with(getActivity()).invalidateToken();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }else if (v == ohChangePwdMenu) {
            Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(i);
        }else if (v == ohPrivacyPolicyMenu) {
            Intent i = new Intent(getActivity(), PrivacyPolicyActivity.class);
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