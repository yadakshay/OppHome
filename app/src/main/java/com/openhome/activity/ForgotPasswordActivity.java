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
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.openhome.R;
import com.openhome.fragment.CustomDialogFragment;
import com.openhome.model.ChangePasswordRequest;
import com.openhome.model.ForgotPasswordRequest;
import com.openhome.model.ResponseData;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import java.util.List;

import retrofit.client.Response;

/**
 * Forgot Password
 */
public class ForgotPasswordActivity extends AppCompatActivity implements CustomDialogFragment.DialogEventListener, Validator.ValidationListener {

    private ProgressDialog progressDialog;

    Validator validator;

    // UI references.
    @NotEmpty(message = "Please enter email address.")
    @Email(message = "Please enter a valid email address.")
    private EditText emailAddressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Set up tool bar
        setupToolbar();

        validator = new Validator(this);
        validator.setValidationListener(this);

        // Set up the login form.
        emailAddressView = (EditText) findViewById(R.id.forgot_password_email);


        Button forgotPasswordButton = (Button) findViewById(R.id.forgot_password_submit_button);
        forgotPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                validator.validate();
            }
        });
    }

    /**
     * Validation Succeeded
     */
    @Override
    public void onValidationSucceeded() {
        forgotPassword();
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
        tvToolbarTitle.setText(getResources().getString(R.string.title_forgot_password).toUpperCase());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        // for Back button
        actionBar.setDisplayHomeAsUpEnabled(true);


        //toolbar.setNavigationIcon(R.drawable.back);
    }

    /**
     * forgot password req to server
     */
    private void forgotPassword() {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we process your forgot password request.");
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();

        String emailAddress = emailAddressView.getText().toString();
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setUserEmailAddress(emailAddress);
        RestClient.getAPI().forgotPassword(forgotPasswordRequest, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                CustomDialogFragment cus = CustomDialogFragment.newInstance(R.string.error,
                        restError.getErrorMessage(), ApplicationConstants.BUTTON_OK, 0);
                cus.show(getFragmentManager(), "ForgptPassFragment");
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                CustomDialogFragment cus = CustomDialogFragment.newInstance(R.string.success,
                        responseData.getMessage(), ApplicationConstants.BUTTON_OK, 0);
                cus.show(getFragmentManager(), "ForgptPassFragment");
            }
        });
    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(ForgotPasswordActivity.this, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
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
    }
}

