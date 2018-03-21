package com.openhome.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.openhome.R;

import java.util.List;

import com.openhome.fragment.CustomDialogFragment;
import com.openhome.model.LoginRequest;
import com.openhome.model.LoginResponse;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.Logger;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.PermissionUtils;
import com.openhome.utils.ShPrefManager;

import retrofit.client.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements CustomDialogFragment.DialogEventListener, Validator.ValidationListener {

    private ProgressDialog progressDialog;

    Validator validator;

    boolean loginStatus = false;
    // UI references.
    @NotEmpty(message = "Email is required to login.")
    @Email(message = "Please enter a valid email.")
    private EditText mEmailView;

    @NotEmpty(message = "Password is required to login.")
    private EditText mPasswordView;

    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up tool bar
        setupToolbar();

        validator = new Validator(this);
        validator.setValidationListener(this);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    // attemptLogin();
                    validator.validate();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                validator.validate();
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        Button forgotPasswordButton = (Button) findViewById(R.id.forgot_password_button);
        forgotPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);


        PermissionUtils.checkPermissions(getApplicationContext(), this);

    }

    /**
     * Validation Succeeded
     */
    @Override
    public void onValidationSucceeded() {
        attemptLogin();
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
        tvToolbarTitle.setText(getResources().getString(R.string.title_login).toUpperCase());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        // for Back button
        actionBar.setHomeButtonEnabled(false); // disable the button
        actionBar.setDisplayHomeAsUpEnabled(false); // remove the left caret
        actionBar.setDisplayShowHomeEnabled(false); // remove the icon

        //toolbar.setNavigationIcon(R.drawable.back);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we authenticate your credentials.");
        LoginRequest loginRequest = new LoginRequest(email, password);
        RestClient.getAPI().login(loginRequest, new RestCallback<LoginResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(LoginResponse loginResponse, Response response) {
                Logger.logMessage("success()::::responseData.getStatus():: ", loginResponse.getStatus());
                Logger.logMessage("success()::::responseData.getMessage():: ", loginResponse.getMessage());
                hideProgress();
                if (loginResponse.getStatus().equalsIgnoreCase(getString(R.string.success)) && loginResponse.getMessage().length() > 0) {
                    ShPrefManager.with(LoginActivity.this).putUserDetails(loginResponse.getUserID(), loginResponse.getUserType(), loginResponse.getToken());
                    ShPrefManager.with(LoginActivity.this).putUserOriginalType(loginResponse.getUserType());
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
        });
    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(LoginActivity.this, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        //if (progressDialog != null && progressDialog.isShowing())
        progressDialog.dismiss();
    }

    /**
     * For registration action.
     */
    public void register() {
        CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.registration_type,
                getString(R.string.licensed_check), ApplicationConstants.BUTTON_NO, ApplicationConstants.BUTTON_YES);
        regSuccessDialogFragment.show(getFragmentManager(), "RegistrationSelectDialogFragment");
    }

    /**
     * Forgot password implementation.
     */
    public void forgotPassword() {
        Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(i);
    }

    /**
     * To display a dialog on click of Register button
     */
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
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
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        switch (action) {
            case ApplicationConstants.BUTTON_YES:
                intent.putExtra("licensed", true);
                startActivity(intent);
                break;
            case ApplicationConstants.BUTTON_NO:
                intent.putExtra("licensed", false);
                startActivity(intent);
                break;
            case ApplicationConstants.BUTTON_OK:
                PermissionUtils.checkPermissions(getApplicationContext(), this);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                boolean showMessage = false;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED
                        || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED)) {
                    CustomDialogFragment permissionDenyFragment = CustomDialogFragment.newInstance(R.string.permissions,
                            getString(R.string.permission_deny_message), ApplicationConstants.BUTTON_OK, 0);
                    permissionDenyFragment.show(getFragmentManager(), "PermissionDenyFragment");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

