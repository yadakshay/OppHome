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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.openhome.model.VisitorRegistrationRequest;
import com.openhome.model.VisitorResponse;
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


public class VisitorRegisterFormActivity extends AppCompatActivity implements CustomDialogFragment.DialogEventListener, Validator.ValidationListener {

    private static final String TAG = VisitorRegisterFormActivity.class.getSimpleName();

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

    @NotEmpty(message = "Please enter Phone No.")
    private EditText phoneNoView;

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
            password, confirmPassword, selectedDate, phoneNumber, visitorType;
    private boolean isLicensed;
    private CheckBox newHome, firstHome, investor;
    String from;

    private ProgressDialog progressDialog;
    private VisitorRegistrationRequest vrRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_register_form);
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
        phoneNoView = (EditText) findViewById(R.id.phoneNo);
        newHome = (CheckBox) findViewById(R.id.newHomeB);
        investor = (CheckBox) findViewById(R.id.investor);
        firstHome = (CheckBox) findViewById(R.id.firstHome);
        visitorType = ""; // default value
        // Get edit text height and set the same to spinner
        float height = selectedDateView.getTextSize();
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        float heightSP = height / scaledDensity;
        Logger.logMessage(TAG, "Text Size of the selected date view is " + height);


        TextView welcomeNote = (TextView) findViewById(R.id.welcomeNote);
        welcomeNote.setText("Register Visitor");
        // If not licensed hide license related fields.
        if (!isLicensed) {
            licenseNumberView.setVisibility(View.GONE);
            licenseClassView.setVisibility(View.GONE);
            licenseTypeView.setVisibility(View.GONE);
            agencyView.setVisibility(View.GONE);
            branchView.setVisibility(View.GONE);
            expiryDateLayout.setVisibility(View.GONE);
            usernameView.setVisibility(View.GONE);
            passwordView.setVisibility(View.GONE);
            confirmPasswordView.setVisibility(View.GONE);
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
        if (NetworkUtils.isNetworkConnected(VisitorRegisterFormActivity.this))
            attemptRegistration();
        else
            OpenHomeUtils.showToast(VisitorRegisterFormActivity.this, "Active internet connection is required.", Toast.LENGTH_LONG);
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
            tvToolbarTitle.setText(getResources().getString(R.string.title_visitor_registration).toUpperCase());
        else
            tvToolbarTitle.setText(getResources().getString(R.string.title_visitor_registration).toUpperCase());
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
        phoneNoView.setError(null);
     //   usernameView.setError(null);
      //  passwordView.setError(null);
      //  confirmPasswordView.setError(null);

        // Store values at the time of the registration attempt.
        firstName = firstNameView.getText().toString();
        lastName = lastNameView.getText().toString();
        email = emailView.getText().toString();
        phoneNumber = phoneNoView.getText().toString();

        if(newHome.isChecked()){
            visitorType = "NEW_HOME_BUYER";
        }
        if(investor.isChecked()){
            if(visitorType == "") {
                visitorType = "INVESTOR";
            }else{
                visitorType = visitorType + ", INVESTOR";
            }
        }
        if(firstHome.isChecked()){
            if(visitorType == "") {
                visitorType = "FIRST_HOME_BUYER";
            }else{
                visitorType = visitorType + ", FIRST_HOME_BUYER";
            }
        }
      //  username = usernameView.getText().toString();
      //  password = passwordView.getText().toString();
       // confirmPassword = confirmPasswordView.getText().toString();

        // For Licensed Sales Person
    /*    if (isLicensed) {
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
        }*/
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
        }// else if (TextUtils.isEmpty(username)) {
          //  usernameView.setError(getString(R.string.error_field_required));
          //  focusView = usernameView;
          //  cancel = true;
       // } else if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
        //    passwordView.setError(getString(R.string.error_invalid_password));
       //     focusView = passwordView;
        //    cancel = true;
      //  } else if (TextUtils.isEmpty(confirmPassword) && !isPasswordValid(password)) {
     //       confirmPasswordView.setError(getString(R.string.error_invalid_password));
      //      focusView = confirmPasswordView;
      //      cancel = true;
     //   } else if (!password.equals(confirmPassword)) {
      //      confirmPasswordView.setError(getString(R.string.error_field_cfmpwd));
      //      focusView = confirmPasswordView;
     //       cancel = true;
     //   }

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
            /*VISITOR*/
            vrRequest = new VisitorRegistrationRequest();
            vrRequest.setFirstName(firstName);
            vrRequest.setLastName(lastName);
            vrRequest.setEmailAddress(email);
            vrRequest.setPhoneNumber(phoneNumber);
            /*********************/
            vrRequest.setVisitorType(visitorType);
          //  registerVisitor(vrRequest);
            String message = "Thank you for your visit. Please Acknowledge the Hazards list below." +
                    "\n\n - Outdoor stairs can be slippery when wet" +
                    "\n\n - Outdoor balcony at height" +
                    "\n\n - Outdoor shared driveway" +
                    "\n\n - Indoor raised floor kitchen";

            CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                    message, ApplicationConstants.BUTTON_ACCEPT, ApplicationConstants.BUTTON_REJECT);
            regSuccessDialogFragment.show(getFragmentManager(), "RegSuccessDialogFragment");
         /**      String message = "Thank you for your visit. Please Acknowledge the Hazards list below." +
                       "\n\n - Outdoor stairs can be slippery when wet" +
                       "\n\n - Outdoor balcony at height" +
                       "\n\n - Outdoor shared driveway" +
                       "\n\n - Indoor raised floor kitchen";

            CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                    message, ApplicationConstants.BUTTON_ACCEPT, ApplicationConstants.BUTTON_REJECT);
            regSuccessDialogFragment.show(getFragmentManager(), "RegSuccessDialogFragment");**/
           /* RegistrationRequest registrationRequest = new RegistrationRequest();
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
          //  registerUser(registrationRequest);
            Intent i = new Intent(this, ActivePropertyHazardActivity.class);
            startActivity(i);
            finish();*/
        }
    }

    /**
     * For registration action.
     */
    public void registrationCompleteShowPop(boolean success, String message) {
        //OpenHomeUtils.showToast(getApplicationContext(), "Registration feature is yet to be implemented.", Toast.LENGTH_LONG);
//        AlertDialog.Builder builder = new AlertDialog.Builder(VisitorRegisterFormActivity.this);
//        builder.setMessage(message).setPositiveButton("OK", dialogClickListener)
//                .show();
        if(success) {
            CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                    message, ApplicationConstants.BUTTON_PROCEED, 0);
            regSuccessDialogFragment.show(getFragmentManager(), "RegSuccessDialogFragment");
        }else{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }    }

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
        progressDialog = ProgressDialog.show(VisitorRegisterFormActivity.this, title, message, false);
        progressDialog.show();
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void registerVisitor(VisitorRegistrationRequest registrationRequest) {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we process your registration request.");
        RestClient.getAPI().registerVisitorUser(registrationRequest, new RestCallback<VisitorResponse>() {
            @Override
            public void failure(RestError restError) {
                //hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                hideProgress();
                registrationCompleteShowPop(false, restError.getErrorMessage());
                Logger.logMessage("failure()::::restError.getErrorMessage():: ", restError.getErrorMessage());
            }

            @Override
            public void success(VisitorResponse responseData, Response response) {
                hideProgress();
                Logger.logMessage("success()::::responseData.getStatus():: ", responseData.getStatus());
              //  Logger.logMessage("success()::::responseData.getMessage():: ", responseData.getMessage());
                if (responseData.getStatus().equalsIgnoreCase(getString(R.string.success))){ //&& responseData.getMessage().length() > 0) {
                  //  if (!from.equals("scanner"))
                //        ShPrefManager.with(VisitorRegisterFormActivity.this).putUserDetails(responseData.getUserID(), responseData.getUserType(), responseData.getToken());
                   // registrationCompleteShowPop(true, "");
                    Toast.makeText(VisitorRegisterFormActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(VisitorRegisterFormActivity.this, ScannerActivity.class);
                        startActivity(i);
                        finish();
                } else {
                    Toast.makeText(VisitorRegisterFormActivity.this, "Failed to update visitor", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(VisitorRegisterFormActivity.this, ScannerActivity.class);
                            startActivity(i);
                            finish();
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
        if(action == ApplicationConstants.BUTTON_ACCEPT){
            vrRequest.setHazardsAccepted("true");
         //   Toast.makeText(this, "hit accept", Toast.LENGTH_SHORT).show();
        }
        if(action == ApplicationConstants.BUTTON_REJECT){
            vrRequest.setHazardsAccepted("false");
        //    Toast.makeText(this, "hit decline", Toast.LENGTH_SHORT).show();
        }
        registerVisitor(vrRequest);
        dialog.dismiss();
        setResult(RESULT_OK);
    }
}