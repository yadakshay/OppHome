package com.openhome.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.openhome.R;
import com.openhome.fragment.CustomDialogFragment;
import com.openhome.model.ChangePasswordRequest;
import com.openhome.model.LoginRequest;
import com.openhome.model.LoginResponse;
import com.openhome.model.ResponseData;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.Logger;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import java.util.List;

import retrofit.client.Response;

/**
 * A login screen that offers login via email/password.
 */
public class ChangePasswordActivity extends AppCompatActivity implements CustomDialogFragment.DialogEventListener, Validator.ValidationListener {

    private ProgressDialog progressDialog;

    Validator validator;

    // UI references.
    @NotEmpty(message = "Please old enter Password.")
    private EditText oldPasswordView;

    @NotEmpty(message = "Please enter Password.")
    @Password(min = 6, scheme = Password.Scheme.ANY, message = "Please enter minimum of 6 characters")
    private EditText mPasswordView;

    @NotEmpty(message = "Please enter Confirm Password.")
    @ConfirmPassword(message = "Password and Confirm Password mismatch.")
    private EditText confirmPasswordView;

    private View changePasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Set up tool bar
        setupToolbar();

        validator = new Validator(this);
        validator.setValidationListener(this);

        // Set up the login form.
        oldPasswordView = (EditText) findViewById(R.id.oldPassword);
        mPasswordView = (EditText) findViewById(R.id.newPassword);
        confirmPasswordView = (EditText) findViewById(R.id.confirmNewPassword);

        Button changePasswordButton = (Button) findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                validator.validate();
            }
        });

        changePasswordView = findViewById(R.id.change_password_form);
    }

    /**
     * Validation Succeeded
     */
    @Override
    public void onValidationSucceeded() {
        attempChangePassword();
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

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);

        ImageView newsImageView = (ImageView) toolbar.findViewById(R.id.newsImageView);
        newsImageView.setVisibility(View.GONE);
        tvToolbarTitle.setText(getResources().getString(R.string.title_change_password).toUpperCase());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        // for Back button
        actionBar.setDisplayHomeAsUpEnabled(true);


        //toolbar.setNavigationIcon(R.drawable.back);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attempChangePassword() {
        // Reset errors.
        oldPasswordView.setError(null);
        mPasswordView.setError(null);
        confirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String oldPassword = oldPasswordView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!password.equals(confirmPassword)) {
            mPasswordView.setError(getString(R.string.error_field_cfmpwd));
            focusView = mPasswordView;
            cancel = true;
        }

        if(oldPassword.equals(password)){
            mPasswordView.setError(getString(R.string.error_field_newpwd));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            changePassword(oldPassword, password);
        }
    }

    /**
     * change password req to server
     *
     * @param oldPassword
     * @param newPassword
     */
    private void changePassword(String oldPassword, String newPassword) {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we update your password.");

        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        String userIdStr = ShPrefManager.with(getApplicationContext()).getUserId();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword, Integer.parseInt(userIdStr));

        RestClient.getAPI().changePassword(authToken, changePasswordRequest, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                CustomDialogFragment cus = CustomDialogFragment.newInstance(R.string.error,
                        restError.getErrorMessage(), ApplicationConstants.BUTTON_OK, 0);
                cus.show(getFragmentManager(), "ChangePassFragment");
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                CustomDialogFragment cus = CustomDialogFragment.newInstance(R.string.success,
                        responseData.getMessage(), ApplicationConstants.BUTTON_OK, 0);
                cus.show(getFragmentManager(), "ChangePassFragment");
            }
        });
    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(ChangePasswordActivity.this, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /**
     * For registration action.
     */
    public void register() {
        //OpenHomeUtils.showToast(getApplicationContext(), "Registration feature is yet to be implemented.", Toast.LENGTH_LONG);
//        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//        builder.setTitle("Registration Type").setMessage(getString(R.string.licensed_check)).setPositiveButton("Yes", dialogClickListener)
//                .setNegativeButton("No", dialogClickListener).show();

        CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.registration_type,
                getString(R.string.licensed_check), ApplicationConstants.BUTTON_NO, ApplicationConstants.BUTTON_YES);
        regSuccessDialogFragment.show(getFragmentManager(), "RegistrationSelectDialogFragment");
    }

    /**
     * Forgot password implementation.
     */
    public void forgotPassword() {
        OpenHomeUtils.showToast(getApplicationContext(), "This feature is on the way.", Toast.LENGTH_SHORT);
    }

    /**
     * To display a dialog on click of Register button
     */
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(ChangePasswordActivity.this, RegistrationActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    intent.putExtra("licensed", true);
                    startActivity(intent);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    intent.putExtra("licensed", false);
                    startActivity(intent);
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
        Intent intent = new Intent(ChangePasswordActivity.this, RegistrationActivity.class);
        switch (action) {
            case ApplicationConstants.BUTTON_YES:
                intent.putExtra("licensed", true);
                startActivity(intent);
                break;
            case ApplicationConstants.BUTTON_NO:
                intent.putExtra("licensed", false);
                startActivity(intent);
                break;
        }
    }
}

