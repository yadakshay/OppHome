package com.openhome.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.mobsandgeeks.saripaar.annotation.ValidateUsing;
import com.openhome.R;
import com.openhome.fragment.CustomDialogFragment;
import com.openhome.fragment.DatePickerFragment;
import com.openhome.model.LoginResponse;
import com.openhome.model.RegistrationRequest;
import com.openhome.model.ResponseData;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.ErrorLogUtils;
import com.openhome.utils.Logger;
import com.openhome.utils.NetworkUtils;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;


public class RegistrationActivity extends AppCompatActivity implements CustomDialogFragment.DialogEventListener, Validator.ValidationListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    Validator validator;
    // UI references.
    @NotEmpty(message = "Please enter First Name.")
    private EditText firstNameView;

    @NotEmpty(message = "Please enter Last Name")
    private EditText lastNameView;

    @NotEmpty(message = "Please enter Email.")
    @Email(message = "Please enter a valid Email Address.")
    private EditText emailView;

    @NotEmpty(message = "Please enter Licence Number.")
    private EditText licenseNumberView;

    @NotEmpty(message = "Please enter Agency.")
    private EditText agencyView;

    @NotEmpty(message = "Please enter Branch.")
    private EditText branchView;

    @NotEmpty(message = "Please enter Username.")
    private EditText usernameView;

    @NotEmpty(message = "Please enter Password.")
    @Password(min = 6, scheme = Password.Scheme.ANY, message = "Please enter minimum of 6 characters")
    private EditText passwordView;

    @NotEmpty(message = "Please enter Confirm Password.")
    @ConfirmPassword(message = "Password and Confirm Password mismatch.")
    private EditText confirmPasswordView;

    @NotEmpty(message = "Please select Expiry Date.")
    private EditText selectedDateView;

    private Spinner licenseTypeView;

    private Spinner licenseClassView;

    private LinearLayout expiryDateLayout;

    private View registrationView;
    private View registrationProgressView;

    private String firstName, lastName, email, licenseNumber, licenseType, licenseClass, agency, branch, username,
            password, confirmPassword, selectedDate;
    private boolean isLicensed;
    String from;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ErrorLogUtils)) {
            Log.d("ACTIVITY", "This is instance of ErrorLogUtils");
            Thread.setDefaultUncaughtExceptionHandler(new ErrorLogUtils(getApplication()));
        }
        from = "login";
        validator = new Validator(this);
        validator.setValidationListener(this);

        Intent intent = getIntent();

        from = (intent.getStringExtra("from") == null) ? "login" : intent.getStringExtra("from");
        isLicensed = intent.getBooleanExtra("licensed", false);

        // Set up tool bar
        setupToolbar();

        // Set up the registration form.
        firstNameView = (EditText) findViewById(R.id.firstName);
        lastNameView = (EditText) findViewById(R.id.lastName);
        emailView = (EditText) findViewById(R.id.regemail);
        licenseNumberView = (EditText) findViewById(R.id.liscenseNumber);
        licenseClassView = (Spinner) findViewById(R.id.license_class_spinner);
        licenseTypeView = (Spinner) findViewById(R.id.license_type_spinner);
        agencyView = (EditText) findViewById(R.id.agency);
        branchView = (EditText) findViewById(R.id.branch);
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        confirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
        expiryDateLayout = (LinearLayout) findViewById(R.id.expiry_date_layout);
        selectedDateView = (EditText) findViewById(R.id.selected_date);

        // Get edit text height and set the same to spinner
        float height = selectedDateView.getTextSize();
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        float heightSP = height / scaledDensity;
        Logger.logMessage(TAG, "Text Size of the selected date view is " + height);


        TextView welcomeNote = (TextView) findViewById(R.id.welcomeNote);
        welcomeNote.setText("Register your Open Home Account");
        // If not licensed hide license related fields.
        if (!isLicensed) {
            licenseNumberView.setVisibility(View.GONE);
            licenseClassView.setVisibility(View.GONE);
            licenseTypeView.setVisibility(View.GONE);
            agencyView.setVisibility(View.GONE);
            branchView.setVisibility(View.GONE);
            expiryDateLayout.setVisibility(View.GONE);

        }

        Button mEmailSignInButton = (Button) findViewById(R.id.registration_lisc_register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });

        registrationView = findViewById(R.id.registration_lisc_form);
        registrationProgressView = findViewById(R.id.registration_lisc_progress);
    }

    /**
     * Validation Succeeded
     */
    @Override
    public void onValidationSucceeded() {
        if (NetworkUtils.isNetworkConnected(RegistrationActivity.this))
            attemptRegistration();
        else
            OpenHomeUtils.showToast(RegistrationActivity.this, "Active internet connection is required.", Toast.LENGTH_LONG);
    }

    /**
     * Validation Failed
     *
     * @param errors
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Set up tool bar.
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView newsImageView = (ImageView) toolbar.findViewById(R.id.newsImageView);
        newsImageView.setVisibility(View.GONE);

        TextView tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        if (isLicensed)
            tvToolbarTitle.setText(getResources().getString(R.string.title_registration_agent).toUpperCase());
        else
            tvToolbarTitle.setText(getResources().getString(R.string.title_registration_public).toUpperCase());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.back);
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
        passwordView.setError(null);
        confirmPasswordView.setError(null);

        // Store values at the time of the registration attempt.
        firstName = firstNameView.getText().toString();
        lastName = lastNameView.getText().toString();
        email = emailView.getText().toString();
        username = usernameView.getText().toString();
        password = passwordView.getText().toString();
        confirmPassword = confirmPasswordView.getText().toString();

        // For Licensed Sales Person
        if (isLicensed) {
            licenseNumberView.setError(null);
            selectedDateView.setError(null);
            //licenseTypeView.set
            //licenseClassView.setError(null);
            agencyView.setError(null);
            branchView.setError(null);
            selectedDate = selectedDateView.getText().toString();
            licenseNumber = licenseNumberView.getText().toString();
            licenseType = licenseTypeView.getSelectedItem().toString();
            licenseClass = licenseClassView.getSelectedItem().toString();
            agency = agencyView.getText().toString();
            branch = branchView.getText().toString();
        }
    }

    /**
     * To Show date picker dialog (for expiry date)
     *
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = DatePickerFragment.newInstance(selectedDateView);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * Attempts to register the account specified by the registration form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void attemptRegistration() {

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
        } else if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        } else if (TextUtils.isEmpty(confirmPassword) && !isPasswordValid(password)) {
            confirmPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = confirmPasswordView;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordView.setError(getString(R.string.error_field_cfmpwd));
            focusView = confirmPasswordView;
            cancel = true;
        }

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
            registrationRequest.setFirstName(firstName);
            registrationRequest.setLastName(lastName);
            registrationRequest.setUsername(username);
            registrationRequest.setPassword(password);
            registrationRequest.setEmailAddress(email);
            registrationRequest.setLicenceClass(licenseClass);
            registrationRequest.setLicenceNumber(licenseNumber);
            registrationRequest.setLicenceType(licenseType);
            registrationRequest.setAgency(agency);
            registrationRequest.setBranch(branch);
            registrationRequest.setExpiryDate(selectedDate);
            if (isLicensed)
                registrationRequest.setRegType("A");
            else
                registrationRequest.setRegType("P");
            registerUser(registrationRequest);
        }
    }

    /**
     * For registration action.
     */
    public void registrationCompleteShowPop(boolean success, String message) {
        //OpenHomeUtils.showToast(getApplicationContext(), "Registration feature is yet to be implemented.", Toast.LENGTH_LONG);
//        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
//        builder.setMessage(message).setPositiveButton("OK", dialogClickListener)
//                .show();
        if (success)
            message = getString(R.string.registrationSuccessMsg);
        CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                message, ApplicationConstants.BUTTON_PROCEED, 0);
        regSuccessDialogFragment.show(getFragmentManager(), "RegSuccessDialogFragment");
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
                    finish();
                    break;
            }
        }
    };

   /* private boolean isEmailValid(String email) {
        return email.contains("@");
    }*/

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(RegistrationActivity.this, title, message, false);
        progressDialog.show();
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void registerUser(RegistrationRequest registrationRequest) {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we process your registration request.");
        RestClient.getAPI().registerUser(registrationRequest, new RestCallback<LoginResponse>() {
            @Override
            public void failure(RestError restError) {
                //hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                hideProgress();
                registrationCompleteShowPop(false, restError.getErrorMessage());
                Logger.logMessage("failure()::::restError.getErrorMessage():: ", restError.getErrorMessage());
            }

            @Override
            public void success(LoginResponse responseData, Response response) {
                hideProgress();
                Logger.logMessage("success()::::responseData.getStatus():: ", responseData.getStatus());
                Logger.logMessage("success()::::responseData.getMessage():: ", responseData.getMessage());
                if (responseData.getStatus().equalsIgnoreCase(getString(R.string.success)) && responseData.getMessage().length() > 0) {
                    if (!from.equals("scanner"))
                        ShPrefManager.with(RegistrationActivity.this).putUserDetails(responseData.getUserID(), responseData.getUserType(), responseData.getToken());
                    registrationCompleteShowPop(true, "");
                } else {
                    //OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public void onButtonClick(DialogFragment dialog, int action) {
        dialog.dismiss();
        setResult(RESULT_OK);

        finish();
        if (from.equals("scanner")) {
            Intent i = new Intent(RegistrationActivity.this, ScannerActivity.class);
            startActivity(i);
        }
    }
}