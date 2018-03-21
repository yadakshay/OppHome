package com.openhome.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.fragment.CustomDialogFragment;
import com.openhome.model.ImageUploadRequest;
import com.openhome.model.ResponseData;
import com.openhome.model.WatchlistRequest;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.ErrorLogUtils;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.camera.CameraController;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;
import retrofit.client.Response;

/**
 * Created by Bhargav on 13/3/2015.
 */
public class ProfileImageUploaderActivity extends AppCompatActivity implements CustomDialogFragment.DialogEventListener, View.OnClickListener {

    private TextView tvToolbarTitle;

    ImageButton fromCameraButton, fromGalleryButton;
    Button uploadImageButton;
    ImageView uploadedImageView;

    private ProgressDialog progressDialog;
    String loggedUserId;
    private static final int CAMERA_REQUEST = 1888;

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;

    String profileImageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upload_profile);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ErrorLogUtils)) {
            Log.d("ACTIVITY", "This is instance of ErrorLogUtils");
            Thread.setDefaultUncaughtExceptionHandler(new ErrorLogUtils(getApplication()));
        }
        loggedUserId = ShPrefManager.with(this).getUserId();

        fromCameraButton = (ImageButton) findViewById(R.id.cameraButton);
        fromGalleryButton = (ImageButton) findViewById(R.id.galleryButton);
        uploadedImageView = (ImageView) findViewById(R.id.uploadedImageView);
        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);

        //        uploadedImageView.setImageResource(0);
        profileImageFileName = loggedUserId + "_profile" + ".png";
        Picasso.with(this).load(ApplicationConstants.PROFILE_IMAGE_BASE_URL + profileImageFileName).placeholder(R.drawable.default_profile).fit().centerCrop().into(uploadedImageView);

        fromCameraButton.setOnClickListener(this);
        fromGalleryButton.setOnClickListener(this);
        uploadImageButton.setOnClickListener(this);

        setupToolbar();
    }

    /**
     * To set up tool bar
     */
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.title_profile_image_upload).toUpperCase());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setHomeButtonEnabled(true); // disable the button
        actionBar.setDisplayHomeAsUpEnabled(true); // remove the left caret
        actionBar.setDisplayShowHomeEnabled(true); // remove the icon

    }

    @Override
    public void onClick(View v) {
        if (v == fromCameraButton) {
            showCamera();
        } else if (v == fromGalleryButton) {
            showGalleryChooser();
        } else if (v == uploadImageButton) {
            uploadImageToServer();
        }
    }

    /**
     * Provides option to choose image from Gallery
     */
    private void showGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Launching camera app to capture image
     */
    private void showCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            uploadedImageView.setImageBitmap(bitmap);
        }
        // Upload the image from ImageView

    }

    /**
     * Uploads the profile image to server.
     */
    public void uploadImageToServer() {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we upload your profile image.");
        ImageUploadRequest imageUploadRequest = new ImageUploadRequest(getStringImage(), profileImageFileName);

        String authToken = ShPrefManager.with(getApplicationContext()).getToken();
        RestClient.getAPI().updateProfileImage(authToken, imageUploadRequest, new RestCallback<ResponseData>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(ResponseData responseData, Response response) {
                hideProgress();
                CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.thank_you,
                        "Profile image uploaded successfully.", ApplicationConstants.BUTTON_OK, 0);
                regSuccessDialogFragment.show(getFragmentManager(), "ProfileImageUploadFragment");
            }
        });
    }


    /**
     * Gets the Image as String from Image view
     *
     * @return
     */
    public String getStringImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void uploadImage() {
//        class UploadImage extends AsyncTask<Bitmap,Void,String>{
//
//            ProgressDialog loading;
//            RequestHandler rh = new RequestHandler();
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                loading = ProgressDialog.show(MainActivity.this, "Uploading Image", "Please wait...", true, true);
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                loading.dismiss();
//                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            protected String doInBackground(Bitmap... params) {
//                Bitmap bitmap = params[0];
//                String uploadImage = getStringImage(bitmap);
//
//                HashMap<String,String> data = new HashMap<>();
//                data.put(UPLOAD_KEY, uploadImage);
//
//                String result = rh.sendPostRequest(UPLOAD_URL,data);
//
//                return result;
//            }
//        }
//
//        UploadImage ui = new UploadImage();
//        ui.execute(bitmap);
    }

    /**
     * Showing the progress dialog
     */
    private void showProgress(String title, String message) {
        progressDialog = ProgressDialog.show(this, title, message, false);
    }

    /**
     * Dismissing the progress dialog if showing
     */
    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onButtonClick(DialogFragment dialog, int action) {
        dialog.dismiss();
        setResult(RESULT_OK);
        finish();
    }

}
