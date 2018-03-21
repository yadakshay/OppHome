package com.openhome.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.fragment.CustomDialogFragmentWithIntent;
import com.openhome.model.ResponseData;
import com.openhome.model.ScannedCodesRequest;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.ErrorLogUtils;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import java.util.List;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.camera.CameraController;
import eu.livotov.labs.android.camview.camera.CameraInfo;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;
import retrofit.client.Response;

import static eu.livotov.labs.android.camview.camera.CameraManager.getAvailableCameras;

/**
 * Created by Bhargav on 11/3/2015.
 */
public class ScannerActivity extends AppCompatActivity implements ScannerLiveView.ScannerViewEventListener, CustomDialogFragmentWithIntent.DialogEventListener {

    private ScannerLiveView camera;
    private CameraController controller;
    private boolean flashStatus;
    private Button scannerRegisterButton;
    private List<CameraInfo> camInfo;
    private ProgressDialog progressDialog;
    private TextView tvToolbarTitle;
    private int cameraSwitch = 0;

    CustomDialogFragmentWithIntent regSuccessDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scanner_view);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ErrorLogUtils)) {
            Log.d("ACTIVITY", "This is instance of ErrorLogUtils");
            Thread.setDefaultUncaughtExceptionHandler(new ErrorLogUtils(getApplication()));
        }
        camera = (ScannerLiveView) findViewById(R.id.scannerCameraView);
        // camera.setHudImageResource(R.drawable.scanner_hud);
        camera.setScannerViewEventListener(this);

        scannerRegisterButton = (Button) findViewById(R.id.scannerRegisterButton);


        camInfo =(List) getAvailableCameras(this);
       // Log.i("CAMERA INFO", camInfo.toString());

        // Register Button onclick
        scannerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ScannerActivity.this, RegistrationActivity.class);
                i.putExtra("licensed", false);
                i.putExtra("from", "scanner");
                startActivity(i);
                finish();
            }
        });
        setupToolbar();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    /**
     * To set up tool bar
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.title_oh_scanner).toUpperCase());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setHomeButtonEnabled(true); // disable the button
        actionBar.setDisplayHomeAsUpEnabled(true); // remove the left caret
        actionBar.setDisplayShowHomeEnabled(true); // remove the icon

    }

    @Override
    public void onScannerStarted(ScannerLiveView scanner) {
        //OpenHomeUtils.showToast(ScannerActivity.this, "Open Home Scanner Started.", Toast.LENGTH_SHORT);
        //uploadScannedCodeToServer("testing API call");
    }

    @Override
    public void onScannerStopped(ScannerLiveView scanner) {
        //OpenHomeUtils.showToast(ScannerActivity.this, "Open Home Scanner Stopped.", Toast.LENGTH_SHORT);
    }

    @Override
    public void onScannerError(Throwable err) {
        // OpenHomeUtils.showToast(ScannerActivity.this, "Error in Open Home Scanner.", Toast.LENGTH_SHORT);
    }

    @Override
    public void onCodeScanned(String data) {
        //OpenHomeUtils.showToast(ScannerActivity.this, "Code scanned successfully - " + data, Toast.LENGTH_SHORT);
        uploadScannedCodeToServer(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ZXDecoder decoder = new ZXDecoder();
            decoder.setScanAreaPercent(1.0);

            camera.setDecoder(decoder);
         //   Collection<CameraInfo> camInfo = getAvailableCameras(this);
         //   Log.i("CAMERA INFO", camInfo.toString());
            camera.startScanner(camInfo.get(1));
        }catch(Exception e){
            OpenHomeUtils.showToast(ScannerActivity.this, "Unable to open the scanner.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onPause() {
        camera.stopScanner();
        super.onPause();
    }

    public void toggleFlash() {
        flashStatus = !flashStatus;
        camera.getCamera().getController().switchFlashlight(flashStatus);
    }

    @Override
    public void onButtonClick(DialogFragment dialog, int action) {
        dialog.dismiss();
        setResult(RESULT_OK);
        Intent i = new Intent(this, ActivePropertyHazardActivity.class);
        startActivity(i);
        //finish();
    }

    /**
     * To updload scanned code to server.
     */
    public void uploadScannedCodeToServer(String data) {

        String userId = ShPrefManager.with(ScannerActivity.this).getUserId();
        ScannedCodesRequest scannedCodesRequest = new ScannedCodesRequest(userId, data);
        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        RestClient.getAPI().updateScannedCodes(authToken, scannedCodesRequest, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                String message = "Scanned code updated to server successfully.";
                if (!responseData.getStatus().equalsIgnoreCase("success")) {
                    message = "Error updating to server. Please try again. If this problem persists, please contact administrator.";
                }

              //  CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,

                CustomDialogFragmentWithIntent regSuccessDialogFragment = CustomDialogFragmentWithIntent.newInstance(R.string.thank_you,
                        message, ApplicationConstants.BUTTON_OK, 0);
                regSuccessDialogFragment.show(getFragmentManager(), "ScannedCodesFragment");

            }
        });
    }

    /**
     * Showing the progress dialog
     */
    public void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(this, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void visitorRegister(View view) {
        Intent i = new Intent(this, VisitorRegisterFormActivity.class);
        i.putExtra("licensed", false);
        i.putExtra("from", "scanner");
        startActivity(i);
    }

    public void switchCamera(View view) {
        camera.stopScanner();
        if (cameraSwitch == 0){
            camera.startScanner(camInfo.get(cameraSwitch));
            cameraSwitch = 1;
        }else if(cameraSwitch == 1){
            camera.startScanner(camInfo.get(cameraSwitch));
            cameraSwitch = 0;
        }
    }
}
