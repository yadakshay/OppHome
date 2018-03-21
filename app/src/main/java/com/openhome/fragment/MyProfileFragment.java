package com.openhome.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.openhome.R;
import com.openhome.activity.ProfileImageUploaderActivity;
import com.openhome.model.LoginResponse;
import com.openhome.model.ProfileResponse;
import com.openhome.model.RegistrationRequest;
import com.openhome.model.MyProfileRequest;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.Logger;
import com.openhome.utils.NetworkUtils;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/5/2015.
 */
public class MyProfileFragment extends Fragment {

    ImageView qrCodeImageView;
    // UI references.
    private EditText firstNameView, lastNameView, emailView, licenseNumberView,
            agencyView, branchView, usernameView, selectedDateView, passwordView;

    private TextView qrImageText, myProfileImageText;
    private Spinner licenseTypeView, licenseClassView;
    private LinearLayout expiryDateLayout, cancelSaveLayout;
    private View registrationView;
    private View registrationProgressView;
    private String firstName, lastName, email, licenseNumber, licenseType, licenseClass, agency, branch, username, password;
    private boolean isLicensed;

    private TextView hintLicClass, hintLicType, hintAgency, hintBranch, hintLicNum, hintExpDate;
    Button editProfileButton;
    Button saveProfileButton;
    Button cancelSaveProfileButton;

    String userId;

    private ProgressDialog progressDialog;
    RelativeLayout myProfileImageLayout;
    CircularImageView thumbnailImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        qrImageText = (TextView) v.findViewById(R.id.qrImageText);

        thumbnailImage = (CircularImageView) v.findViewById(R.id.profileThumbnail);

        userId = ShPrefManager.with(getActivity()).getUserId();
        Toast.makeText(getActivity(), userId, Toast.LENGTH_SHORT).show();
        firstNameView = (EditText) v.findViewById(R.id.firstName);
        lastNameView = (EditText) v.findViewById(R.id.lastName);
        emailView = (EditText) v.findViewById(R.id.regemail);
        licenseNumberView = (EditText) v.findViewById(R.id.liscenseNumber);
        licenseClassView = (Spinner) v.findViewById(R.id.license_class_spinner);
        licenseTypeView = (Spinner) v.findViewById(R.id.license_type_spinner);
        agencyView = (EditText) v.findViewById(R.id.agency);
        branchView = (EditText) v.findViewById(R.id.branch);
        usernameView = (EditText) v.findViewById(R.id.username);
        expiryDateLayout = (LinearLayout) v.findViewById(R.id.expiry_date_layout);
        selectedDateView = (EditText) v.findViewById(R.id.selected_date);

        qrCodeImageView = (ImageView) v.findViewById(R.id.qrCode);

        hintAgency = (TextView) v.findViewById(R.id.hintAgency);
        hintBranch = (TextView) v.findViewById(R.id.hintBranch);
        hintExpDate = (TextView) v.findViewById(R.id.hintExpDate);
        hintLicClass = (TextView) v.findViewById(R.id.hintLicClass);
        hintLicNum = (TextView) v.findViewById(R.id.hintLicNum);
        hintLicType = (TextView) v.findViewById(R.id.hintLicTyp);

        loadMyProfile();
        setFieldStatus(false);

        editProfileButton = (Button) v.findViewById(R.id.edit_profile_button);
        saveProfileButton = (Button) v.findViewById(R.id.save_profile_button);
        cancelSaveProfileButton = (Button) v.findViewById(R.id.cancel_save_profile_button);
        cancelSaveLayout = (LinearLayout) v.findViewById(R.id.cancel_save_layout);

        qrCodeImageView.setVisibility(View.GONE);

        setupListeners();
        return v;
    }

    /**
     * Set up all listeners for this VIEW
     */
    private void setupListeners() {
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFieldStatus(true);
                editProfileButton.setVisibility(View.GONE);
                cancelSaveLayout.setVisibility(View.VISIBLE);
            }
        });

        cancelSaveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFieldStatus(false);
                editProfileButton.setVisibility(View.VISIBLE);
                cancelSaveLayout.setVisibility(View.GONE);
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isNetworkConnected(getActivity()))
                    attempEditProfile();
                else
                    OpenHomeUtils.showToast(getActivity(), "Active internet connection is required.", Toast.LENGTH_LONG);
                editProfileButton.setVisibility(View.VISIBLE);
                cancelSaveLayout.setVisibility(View.GONE);
            }
        });

        qrImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDialogFragment imageDialogFragment = ImageDialogFragment.newInstance(userId, "QR Code");
                imageDialogFragment.show(getActivity().getFragmentManager(), "ImageFullViewFragment");
            }
        });

        thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileImageUploaderActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Sets either enabled to true or false
     *
     * @param status
     */
    private void setFieldStatus(boolean status) {
        firstNameView.setEnabled(status);
        lastNameView.setEnabled(status);
        emailView.setEnabled(status);
        licenseNumberView.setEnabled(status);
        licenseClassView.setEnabled(status);
        licenseTypeView.setEnabled(status);
        agencyView.setEnabled(status);
        branchView.setEnabled(status);
        usernameView.setEnabled(status);
        //passwordView = (EditText)  v.findViewById(R.id.password);
        // confirmPasswordView = (EditText)  v.findViewById(R.id.confirmPassword);
        expiryDateLayout.setEnabled(status);
    }

    /**
     * Method to load Profile details
     */
    private void loadMyProfile() {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we load your profile details.");
        final String userId = ShPrefManager.with(getActivity()).getUserId();
        String authToken = ShPrefManager.with(getActivity()).getToken();
        MyProfileRequest searchRequest = new MyProfileRequest();
        searchRequest.setUserId(userId);
        RestClient.getAPI().getUserProfile(authToken, userId, new RestCallback<ProfileResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                showDialogOnResult(R.string.error, "It seems there is some problem in retrieving your profile details. Please try again.");
            }

            @Override
            public void success(ProfileResponse profileResponse, Response response) {
                ProfileResponse.UserDetails userDetails = profileResponse.getMessage();
                if (userDetails == null) {
                    showDialogOnResult(R.string.error, "It seems there is some problem in retrieving your profile details. Please try again.");
                } else {
                    Logger.logMessage("Myprofilefragment ", ApplicationConstants.QRCODE_IMAGE_BASE_URL + userDetails.getUserId() + "_qrcode.png");
                   // Picasso.with(getActivity()).load(ApplicationConstants.QRCODE_IMAGE_BASE_URL + userDetails.getUserId() + "_qrcode.png").placeholder(R.drawable.default_profile).fit().centerCrop().into(qrCodeImageView);
                    Picasso.with(getActivity()).load(ApplicationConstants.QRCODE_IMAGE_BASE_URL + userDetails.getUserId() + ".png").placeholder(R.drawable.default_profile).fit().centerCrop().into(qrCodeImageView);

                    String profileImageFileName = userId;// + "_profile" + ".png";
                    Picasso.with(getActivity()).load(ApplicationConstants.PROFILE_IMAGE_BASE_URL + profileImageFileName).placeholder(R.drawable.default_profile).fit().centerCrop().into(thumbnailImage);

                    if (userDetails.getUserType().equalsIgnoreCase("P")) {
                        hintExpDate.setVisibility(View.GONE);
                        hintBranch.setVisibility(View.GONE);
                        hintLicNum.setVisibility(View.GONE);
                        hintLicClass.setVisibility(View.GONE);
                        hintAgency.setVisibility(View.GONE);
                        hintLicType.setVisibility(View.GONE);
                        
                        licenseNumberView.setVisibility(View.GONE);
                        licenseClassView.setVisibility(View.GONE);
                        licenseTypeView.setVisibility(View.GONE);
                        agencyView.setVisibility(View.GONE);
                        branchView.setVisibility(View.GONE);
                        expiryDateLayout.setVisibility(View.GONE);
                    }

                    firstNameView.setText(userDetails.getFirstName());
                    lastNameView.setText(userDetails.getLastName());
                    emailView.setText(userDetails.getEmailAddress());
                    usernameView.setText(userDetails.getUsername());
                    //passwordView.setText(userDetails.getPassword());
                    agencyView.setText(userDetails.getAgency());
                    branchView.setText(userDetails.getBranch());
                    setSpinnerSelection(licenseClassView, userDetails.getLiscClass());
                    setSpinnerSelection(licenseTypeView, userDetails.getLiscType());
                    licenseNumberView.setText(userDetails.getFirstName());
                    selectedDateView.setText(userDetails.getExpiryDate());
                    hideProgress();
                }
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }

    /**
     * Attempts to register the account specified by the registration form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void attempEditProfile() {

        resetFormErrorsAndGetValues();

        boolean cancel = false;
        View focusView = null;

        // Form Validations
        if (TextUtils.isEmpty(firstName)) {
            firstNameView.setError(getString(R.string.error_field_required));
            focusView = firstNameView;
            cancel = true;
        } else if (TextUtils.isEmpty(lastName)) {
            lastNameView.setError(getString(R.string.error_field_required));
            focusView = lastNameView;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!OpenHomeUtils.isValidEmail(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        } else if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        /*else if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }*/
        // Additional validations for licensed sales person
        if (isLicensed) {

        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setUserId(userId);

            registrationRequest.setFirstName(firstName);
            registrationRequest.setLastName(lastName);
            registrationRequest.setUsername(username);
            //registrationRequest.setPassword(password);
            registrationRequest.setEmailAddress(email);
            registrationRequest.setLicenceClass(licenseClass);
            registrationRequest.setLicenceNumber(licenseNumber);
            registrationRequest.setLicenceType(licenseType);
            registrationRequest.setAgency(agency);
            registrationRequest.setBranch(branch);
            if (isLicensed)
                registrationRequest.setRegType("A");
            else
                registrationRequest.setRegType("P");
            editProfile(registrationRequest);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (userId != null) {
            //thumbnailImage.setImageResource(0);
            String profileImageFileName = userId + "_profile" + ".png";
            Picasso.with(getActivity()).load(ApplicationConstants.PROFILE_IMAGE_BASE_URL + profileImageFileName).placeholder(R.drawable.default_profile).fit().centerCrop().into(thumbnailImage);

        }

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    /**
     * This method resets form errors and retrieves the values from fields and stores globally.
     */
    private void resetFormErrorsAndGetValues() {
        // Reset errors.
        firstNameView.setError(null);
        lastNameView.setError(null);
        emailView.setError(null);
        usernameView.setError(null);
//        passwordView.setError(null);


        // Store values at the time of the registration attempt.
        firstName = firstNameView.getText().toString();
        lastName = lastNameView.getText().toString();
        email = emailView.getText().toString();
        //password = passwordView.getText().toString();
        username = usernameView.getText().toString();
        // For Licensed Sales Person
        if (isLicensed) {
            licenseNumberView.setError(null);
            //licenseTypeView.set
            //licenseClassView.setError(null);
            agencyView.setError(null);
            branchView.setError(null);

            licenseNumber = licenseNumberView.getText().toString();
            licenseType = licenseTypeView.getSelectedItem().toString();
            licenseClass = licenseClassView.getSelectedItem().toString();
            agency = agencyView.getText().toString();
            branch = branchView.getText().toString();
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

    private void editProfile(RegistrationRequest registrationRequest) {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we update your profile details.");
        final String authToken = ShPrefManager.with(getActivity()).getToken();
        RestClient.getAPI().editProfile(authToken, registrationRequest, new RestCallback<LoginResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                showDialogOnResult(R.string.error, "Profile could not be updated - " + restError.getErrorMessage());
                Logger.logMessage("failure()::::restError.getErrorMessage():: ", restError.getErrorMessage());
            }

            @Override
            public void success(LoginResponse responseData, Response response) {
                Logger.logMessage("success()::::responseData.getStatus():: ", responseData.getStatus());
                Logger.logMessage("success()::::responseData.getMessage():: ", responseData.getMessage());
                hideProgress();
                if (responseData.getStatus().equalsIgnoreCase(getString(R.string.success)) && responseData.getMessage().length() > 0) {

                    ShPrefManager.with(getActivity()).putUserDetails(responseData.getUserID(), userId, authToken);
                    showDialogOnResult(R.string.thank_you, "Profile updated successfully.");
                } else {
                    //OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                }
            }
        });
    }

    /**
     * For registration action.
     */
    public void showDialogOnResult(int heading, String message) {
        CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(heading,
                message, ApplicationConstants.BUTTON_OK, 0);
        regSuccessDialogFragment.show(getActivity().getFragmentManager(), "MyProfileFragment");
    }

    /**
     * To display a dialog on click of Register button
     */
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    loadMyProfile();
                    break;
            }
        }
    };

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                //urldisplay = URLEncoder.encode(urldisplay, "UTF-8");
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getActivity().getCurrentFocus();
        boolean ret = getActivity().dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getActivity().getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

}