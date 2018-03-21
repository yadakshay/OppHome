package com.openhome.fragment.agent;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.plus.model.people.Person;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Select;
import com.openhome.R;
import com.openhome.fragment.CustomDialogFragment;
import com.openhome.fragment.HazardsDialogFragment;
import com.openhome.model.AddPropertyRequest;
import com.openhome.model.Hazards;
import com.openhome.model.HazardsResponse;
import com.openhome.model.HazardsResponse2;
import com.openhome.model.RegionsResponse;
import com.openhome.model.ResponseData;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.CalendarUtils;
import com.openhome.utils.Logger;
import com.openhome.utils.NetworkUtils;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/5/2015.
 */
public class AddPropertyFragment extends Fragment implements Validator.ValidationListener, View.OnClickListener {


    Validator validator;
    private RelativeLayout viewTimingsPlaceHolder;
    private Button addPropertyButton;
    private ImageView addViewTimeButton;

    // UI references.
    @NotEmpty(message = "Please Enter Listing Title.")
    private EditText shortDescriptionView;

    @NotEmpty(message = "Please Enter Street #.")
    private EditText streetNumberView;

    private EditText unitFlatView;

    @NotEmpty(message = "Please Enter Street Name.")
    private EditText streetNameView;

    @NotEmpty(message = "Please Enter Property Description.")
    private EditText descriptionView;

    @NotEmpty(message = "Please Enter Price.")
    @Digits(message = "Please enter a numeric value.", integer = 15)
    private EditText priceView;

    @NotEmpty(message = "Please Enter Floor Area.")
    private EditText floorAreaView;

    @NotEmpty(message = "Please Enter Land Area.")
    private EditText landAreaView;

    @NotEmpty(message = "Please Enter Parking Details.")
    private EditText parkingView;

    @Select(message = "Please select Property Type")
    private Spinner typeSpinner;

    @Select(message = "Please select Bedrooms.")
    private Spinner bedroomSpinner;

    @Select(message = "Please select Bedrooms.")
    private Spinner bathroomSpinner;

    @Select(message = "Please select a Region.")
    private Spinner regionSpinner;

    @Select(message = "Please select a District.")
    private Spinner districtSpinner;

    @Select(message = "Please select a Suburb.")
    private Spinner suburbSpinner;

    @Select(message = "Please select a method of sale.")
    private Spinner methodOfSaleSpinner;

    private EditText methodOfSaleEditText;

    private String userId, viewingTimes;

    private Spinner ohStartTime1, ohStartTime2, ohStartTime3, ohStartTime4, ohStartTime5;
    private Spinner ohEndTime1, ohEndTime2, ohEndTime3, ohEndTime4, ohEndTime5;
    private LinearLayout viewingLayout1, viewingLayout2, viewingLayout3, viewingLayout4, viewingLayout5;
    @NotEmpty(message = "Please select Open Home Date.")
    private EditText ohDate1;
    private EditText ohDate2, ohDate3, ohDate4, ohDate5;
    private StringBuilder address;

    private int PICK_IMAGE_REQUEST = 1;

    private ProgressDialog progressDialog;

    private ArrayAdapter<String> regionsSpinnerAdapter;
    private ArrayAdapter<String> districtSpinnerAdapter;
    private ArrayAdapter<String> suburbSpinnerAdapter;

    private List<Hazards> hazardsList = null;

    private List<RegionsResponse.Regions> regionsList;

    private ImageView plus, minus, propertyImage;

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public static final int MIN_VIEWING_TIMES = 1, MAX_VIEWING_TIMES = 5;

    private int visibleOpenHomes = 1;

    private Uri filePath;

    private Bitmap bitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userId = ShPrefManager.with(getActivity()).getUserId();

        View v = inflater.inflate(R.layout.fragment_add_property, container, false);
        validator = new Validator(this);
        validator.setValidationListener(this);

        // Initializing UI elements

        typeSpinner = (Spinner) v.findViewById(R.id.property_type_spinner);
        shortDescriptionView = (EditText) v.findViewById(R.id.property_short_description);

        streetNumberView = (EditText) v.findViewById(R.id.property_street_no);
        unitFlatView = (EditText) v.findViewById(R.id.property_unit_flat);
        streetNameView = (EditText) v.findViewById(R.id.property_street_name);

        regionSpinner = (Spinner) v.findViewById(R.id.property_region_spinner);
        districtSpinner = (Spinner) v.findViewById(R.id.property_district_spinner);
        suburbSpinner = (Spinner) v.findViewById(R.id.property_suburb_spinner);
        districtSpinner.setEnabled(false);
        suburbSpinner.setEnabled(false);


        methodOfSaleSpinner = (Spinner) v.findViewById(R.id.property_methodofsale);
        methodOfSaleEditText = (EditText) v.findViewById(R.id.property_methodofsalevalue);

        priceView = (EditText) v.findViewById(R.id.property_price);

        bedroomSpinner = (Spinner) v.findViewById(R.id.property_bedroooms);
        bathroomSpinner = (Spinner) v.findViewById(R.id.property_bathrooms);

        floorAreaView = (EditText) v.findViewById(R.id.property_floor_area);
        landAreaView = (EditText) v.findViewById(R.id.property_land_area);

        parkingView = (EditText) v.findViewById(R.id.property_parking);

        descriptionView = (EditText) v.findViewById(R.id.property_description);

        regionsSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        districtSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);
        suburbSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, android.R.id.text1);

        ohDate1 = (EditText) v.findViewById(R.id.oh_date1);
        ohStartTime1 = (Spinner) v.findViewById(R.id.oh_start_time1);
        ohEndTime1 = (Spinner) v.findViewById(R.id.oh_end_time1);

        ohDate2 = (EditText) v.findViewById(R.id.oh_date2);
        ohStartTime2 = (Spinner) v.findViewById(R.id.oh_start_time2);
        ohEndTime2 = (Spinner) v.findViewById(R.id.oh_end_time2);

        ohDate3 = (EditText) v.findViewById(R.id.oh_date3);
        ohStartTime3 = (Spinner) v.findViewById(R.id.oh_start_time3);
        ohEndTime3 = (Spinner) v.findViewById(R.id.oh_end_time3);

        ohDate4 = (EditText) v.findViewById(R.id.oh_date4);
        ohStartTime4 = (Spinner) v.findViewById(R.id.oh_start_time4);
        ohEndTime4 = (Spinner) v.findViewById(R.id.oh_end_time4);

        ohDate5 = (EditText) v.findViewById(R.id.oh_date5);
        ohStartTime5 = (Spinner) v.findViewById(R.id.oh_start_time5);
        ohEndTime5 = (Spinner) v.findViewById(R.id.oh_end_time5);

        viewingLayout1 = (LinearLayout) v.findViewById(R.id.viewingLayout1);
        viewingLayout2 = (LinearLayout) v.findViewById(R.id.viewingLayout2);
        viewingLayout3 = (LinearLayout) v.findViewById(R.id.viewingLayout3);
        viewingLayout4 = (LinearLayout) v.findViewById(R.id.viewingLayout4);
        viewingLayout5 = (LinearLayout) v.findViewById(R.id.viewingLayout5);

        plus = (ImageView) v.findViewById(R.id.icon_plus);
        plus.setOnClickListener(this);
        minus = (ImageView) v.findViewById(R.id.icon_minus);
        minus.setOnClickListener(this);

        propertyImage = (ImageView) v.findViewById(R.id.property_image);
        propertyImage.setOnClickListener(this);

        addPropertyButton = (Button) v.findViewById(R.id.add_property_button);

        loadSearchRegions();

        return v;
    }

    /**
     * Method to load search list
     */
    private void loadSearchRegions() {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we retrieve regions from server.");
        String authToken = ShPrefManager.with(getActivity()).getToken();
        RestClient.getAPI().getRegions(authToken, new RestCallback<RegionsResponse>() {
            @Override
            public void failure(RestError restError) {
                OpenHomeUtils.showToast(getActivity().getApplicationContext(), "Could not load regions from the server.", Toast.LENGTH_LONG);
            }

            @Override
            public void success(RegionsResponse regionsResponse, Response response) {
                if (regionsResponse.getMessage().size() > 0) {
                    regionsList = regionsResponse.getMessage();
                    setOnClickListeners();
                    populateRegions();
                } else {
                    String message = "There is an error loading data. Please contact administrator..";
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.try_again,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getActivity().getFragmentManager(), "AddPropertyFragment");
                }
            }
        });

        getAllHazards();
    }

    /**
     * This method retrieves all the Hazards from the server
     */
    private void getAllHazards() {
        //http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
        hideProgress();
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we retrieve hazard list from server.");
        String authToken = ShPrefManager.with(getActivity()).getToken();
        String userId = ShPrefManager.with(getActivity()).getUserId();

        RestClient.getAPI().getAllHazards(authToken, new RestCallback<HazardsResponse2>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getActivity().getApplicationContext(), "Could not load hazards list from the server.", Toast.LENGTH_LONG);
            }

            @Override
            public void success(HazardsResponse2 responseData, Response response) {
                hideProgress();
                hazardsList = responseData.getMessage();
                //loadHazardsView(new AddPropertyRequest());
            }
        });
    }

    /**
     * Once hazards list is retrieved from the server, this method constructs UI to display them.
     */
    private void loadHazardsView(AddPropertyRequest addPropertyRequest) {
        HazardsDialogFragment dialogFragment = HazardsDialogFragment.newInstance(hazardsList, addPropertyRequest);
        dialogFragment.show(getActivity().getFragmentManager(), "HazardsDialogFragement");

    }

    /**
     * Validation Succeeded
     */
    @Override
    public void onValidationSucceeded() {
        if (NetworkUtils.isNetworkConnected(getActivity()))
            addProperty();
        else
            OpenHomeUtils.showToast(getActivity(), "Active internet connection is required.", Toast.LENGTH_LONG);
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
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Sets all on click listeners in this screen
     */
    public void setOnClickListeners() {
        // Region Spinner
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = regionSpinner.getSelectedItem().toString();

                if (!selectedRegion.equals("Choose Region...")) {
                    int selectedRegionID = 0;
                    for (RegionsResponse.Regions region : regionsList) {
                        if (region.getRegionName().equals(selectedRegion))
                            selectedRegionID = region.getRegionId();
                    }
                    districtSpinner.setEnabled(true);
                    populateDistricts(selectedRegionID);
                } else {
                    populateDistricts(0);
                    districtSpinner.setSelection(0);
                    districtSpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // District Spinner
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = districtSpinner.getSelectedItem().toString();
                int selectedDistrictID = 0;
                if (!selectedDistrict.equals("Choose District...")) {
                    for (RegionsResponse.Regions region : regionsList) {
                        if (region.getRegionName().equals(selectedDistrict))
                            selectedDistrictID = region.getRegionId();
                    }
                    suburbSpinner.setEnabled(true);
                    populateSuburbs(selectedDistrictID);

                } else {
                    suburbSpinner.setSelection(0);
                    suburbSpinner.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Method of sale value
        methodOfSaleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (methodOfSaleSpinner.getSelectedItem().toString().equals(ApplicationConstants.MOS_AUCTIONED))
                    showDateTimePickerDialog((EditText) v);
            }
        });

        // Method of sale Spinner
        methodOfSaleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMethodOfSale = methodOfSaleSpinner.getSelectedItem().toString();
                methodOfSaleEditText.setVisibility(View.VISIBLE);
                switch (selectedMethodOfSale) {
                    case ApplicationConstants.MOS_ASKING:

                        break;
                    case ApplicationConstants.MOS_AUCTIONED:

                        break;
                    case ApplicationConstants.MOS_ENQUIRIES:

                        break;
                    case ApplicationConstants.MOS_NEGOTIATION:
                        methodOfSaleEditText.setVisibility(View.GONE);
                        break;
                    default:
                        methodOfSaleEditText.setVisibility(View.GONE);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ohDate1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDateTimePickerDialog((EditText) v);
            }
        });

        ohDate2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDateTimePickerDialog((EditText) v);
            }
        });

        ohDate3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDateTimePickerDialog((EditText) v);
            }
        });

        ohDate4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDateTimePickerDialog((EditText) v);
            }
        });

        ohDate5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDateTimePickerDialog((EditText) v);
            }
        });

//        ohDate3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus)
//                    showDateTimePickerDialog((EditText) v);
//            }
//        });

        addPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

//        addSecurityGuidelinesCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked)
//                    securityGuidelinesLayout.setVisibility(View.VISIBLE);
//                else
//                    securityGuidelinesLayout.setVisibility(View.GONE);
//            }
//        });
    }

    /**
     * This method populates regions
     */
    private void populateRegions() {
        regionsSpinnerAdapter.clear();
        regionsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(regionsSpinnerAdapter);
        regionsSpinnerAdapter.add("Choose Region...");
        for (RegionsResponse.Regions region : regionsList) {
            if (region.getParentId() == 0)
                regionsSpinnerAdapter.add(region.getRegionName());
        }
        regionsSpinnerAdapter.notifyDataSetChanged();
    }

    /**
     * This method populates districts for selected region
     */
    private void populateDistricts(int selectedRegion) {
        districtSpinnerAdapter.clear();
        districtSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtSpinnerAdapter);
        districtSpinnerAdapter.add("Choose District...");
        if (selectedRegion != 0)
            for (RegionsResponse.Regions region : regionsList) {
                if (region.getParentId() == selectedRegion)
                    districtSpinnerAdapter.add(region.getRegionName());
            }
        districtSpinnerAdapter.notifyDataSetChanged();
    }

    /**
     * This method populates districts for selected region
     */
    private void populateSuburbs(int selectedSuburb) {
        suburbSpinnerAdapter.clear();
        suburbSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        suburbSpinner.setAdapter(suburbSpinnerAdapter);
        suburbSpinnerAdapter.add("Choose Suburb...");
        if (selectedSuburb != 0)
            for (RegionsResponse.Regions region : regionsList) {
                if (region.getParentId() == selectedSuburb)
                    suburbSpinnerAdapter.add(region.getRegionName());
            }
        suburbSpinnerAdapter.notifyDataSetChanged();
    }

    /**
     * This method gets the values from UI and sends to server for adding property.
     */
    private void addProperty() {

        String shortDesc = shortDescriptionView.getText().toString();
        String description = descriptionView.getText().toString();
        String price = priceView.getText().toString();
        String floorArea = floorAreaView.getText().toString();
        String landArea = landAreaView.getText().toString();
        String parking = parkingView.getText().toString();
        viewingTimes = ohDate1.getText().toString() + " " + ohStartTime1.getSelectedItem().toString() + " - " + ohEndTime1.getSelectedItem().toString();
        address = new StringBuilder(30);

        if (!ohDate2.getText().toString().isEmpty() && !ohStartTime2.getSelectedItem().toString().isEmpty() && !ohEndTime2.getSelectedItem().toString().isEmpty()) {
            viewingTimes += "::::" + ohDate2.getText().toString() + " " + ohStartTime2.getSelectedItem().toString() + " - " + ohEndTime2.getSelectedItem().toString();
        }
        if (!ohDate3.getText().toString().isEmpty() && !ohStartTime3.getSelectedItem().toString().isEmpty() && !ohEndTime3.getSelectedItem().toString().isEmpty()) {
            viewingTimes += "::::" + ohDate3.getText().toString() + " " + ohStartTime3.getSelectedItem().toString() + " - " + ohEndTime3.getSelectedItem().toString();
        }
        if (!ohDate4.getText().toString().isEmpty() && !ohStartTime4.getSelectedItem().toString().isEmpty() && !ohEndTime4.getSelectedItem().toString().isEmpty()) {
            viewingTimes += "::::" + ohDate4.getText().toString() + " " + ohStartTime4.getSelectedItem().toString() + " - " + ohEndTime4.getSelectedItem().toString();
        }
        if (!ohDate5.getText().toString().isEmpty() && !ohStartTime5.getSelectedItem().toString().isEmpty() && !ohEndTime5.getSelectedItem().toString().isEmpty()) {
            viewingTimes += "::::" + ohDate5.getText().toString() + " " + ohStartTime5.getSelectedItem().toString() + " - " + ohEndTime5.getSelectedItem().toString();
        }

        String suburbValue = suburbSpinner.getSelectedItem().toString();
        String districtValue = districtSpinner.getSelectedItem().toString();
        String regionValue = regionSpinner.getSelectedItem().toString();

        if (!unitFlatView.getText().toString().trim().isEmpty())
            address.append(unitFlatView.getText().toString()).append(ApplicationConstants.SPACE_SEPERATOR);
        address.append(streetNumberView.getText().toString()).append(ApplicationConstants.SPACE_SEPERATOR)
                .append(streetNameView.getText().toString()).append(ApplicationConstants.SPACE_SEPERATOR)
                .append(suburbValue).append(ApplicationConstants.SPACE_SEPERATOR)
                .append(districtValue).append(ApplicationConstants.SPACE_SEPERATOR)
                .append(regionValue).append(ApplicationConstants.SPACE_SEPERATOR);

        String type = typeSpinner.getSelectedItem().toString();
        String bedrooms = bedroomSpinner.getSelectedItem().toString();
        String bathrooms = bathroomSpinner.getSelectedItem().toString();

        String methodOfSale = methodOfSaleSpinner.getSelectedItem().toString();
        String methodOfSaleValue = (methodOfSale == ApplicationConstants.MOS_NEGOTIATION) ? "" : methodOfSaleEditText.getText().toString();

        int hot = 0;

        AddPropertyRequest addPropertyRequest = new AddPropertyRequest();
        addPropertyRequest.setAgentId(userId);
        addPropertyRequest.setShortDescription(shortDesc);
        addPropertyRequest.setDescription(description);
        addPropertyRequest.setPrice(price);
        addPropertyRequest.setFloorArea(floorArea);
        addPropertyRequest.setLandArea(landArea);
        addPropertyRequest.setParking(parking);
        addPropertyRequest.setViewingTime(viewingTimes);
        addPropertyRequest.setAddress(address.toString());
        addPropertyRequest.setType(type);
        addPropertyRequest.setBathrooms(bathrooms);
        addPropertyRequest.setBedrooms(bedrooms);
        addPropertyRequest.setIsHot(hot);
        addPropertyRequest.setMethodOfSale(methodOfSale);
        addPropertyRequest.setMethodOfSaleValue(methodOfSaleValue);

        String imageStr = getStringImage();
        addPropertyRequest.setPropertyImage((imageStr != null) ? imageStr : "");

        if (hazardsList != null) {
            loadHazardsView(addPropertyRequest);
        } else {
            showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we add the property details.");
            String authToken = ShPrefManager.with(getActivity()).getToken();
            RestClient.getAPI().addProperty(authToken, addPropertyRequest, new RestCallback<ResponseData>() {
                @Override
                public void failure(RestError restError) {
                    //hideProgress();
                    OpenHomeUtils.showToast(getActivity(), restError.getErrorMessage(), Toast.LENGTH_LONG);
                    hideProgress();
                }

                @Override
                public void success(ResponseData responseData, Response response) {
                    hideProgress();
                    Logger.logMessage("success()::::responseData.getStatus():: ", responseData.getStatus());
                    int heading = R.string.error;
                    String message = "";
                    CustomDialogFragment dialogFragment;
                    if (responseData.getStatus().equalsIgnoreCase(getString(R.string.success))) {
                        heading = R.string.thank_you;
                        message = "Property added successfully. Open Home timings will be added to your calendar";
                        dialogFragment = CustomDialogFragment.newInstance(heading,
                                message, ApplicationConstants.BUTTON_OK, 0);
                        try {
                            String splitViewingTimes[] = viewingTimes.split("::::");
                            for (String viewTime : splitViewingTimes) {
                                String date = viewTime.split(" ")[0];
                                String time = viewTime.replace(date, "");
                                Calendar beginTime = CalendarUtils.getCalendarFromViewingTime(date + "::::" + time);
                                //startActivity(CalendarUtils.addEventToCalendar(beginTime, beginTime, location.getText().toString()));
                                String calID = CalendarUtils.getCalendar(getActivity().getApplicationContext());
                                // CalendarUtils.addCalendarEvent(getApplicationContext(), calID, location.getText().toString(), "Open Home", beginTime.getTimeInMillis(), beginTime.getTimeInMillis(), 0);

                                CalendarUtils.addCalendarContractEvents("Open Home", "Open Home viewing time for the property added by you.",
                                        calID, address.toString(), beginTime.getTimeInMillis(), beginTime.getTimeInMillis(), getActivity().getContentResolver());

                            }
                        } catch (Exception e) {
                            message = "Property added successfully. Open Home timings could not be added to your calendar. Please contact administrator if this problem persists.";
                        }
                    } else {
                        message = "Error adding property. Please try again after some time.";
                        dialogFragment = CustomDialogFragment.newInstance(heading,
                                message, ApplicationConstants.BUTTON_OK, 0);
                    }
                    dialogFragment.show(getActivity().getFragmentManager(), "RegSuccessDialogFragment");
                }
            });
        }
    }


    /**
     * Date and Time picker dialog
     */
    public void showDateTimePickerDialog(final EditText view) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_viewing_time);
        dialog.setTitle("Select Viewing Date");
        final StringBuilder selectedDateTime = new StringBuilder();
        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);

        Button selectDateTimeButton = (Button) dialog.findViewById(R.id.selectDateTimeButton);

        selectDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDateTime.append(datePicker.getDayOfMonth())
                        .append(ApplicationConstants.DATE_SEPERATOR)
                        .append(datePicker.getMonth() + 1)
                        .append(ApplicationConstants.DATE_SEPERATOR)
                        .append(datePicker.getYear());
                view.setText(selectedDateTime.toString());
                dialog.dismiss();
            }
        });

        dialog.show();
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


    @Override
    public void onClick(View v) {
        if (v == plus) {
            if (viewingLayout2.getVisibility() == View.GONE) {
                viewingLayout2.setVisibility(View.VISIBLE);
            } else if (viewingLayout3.getVisibility() == View.GONE && viewingLayout2.getVisibility() == View.VISIBLE) {
                viewingLayout3.setVisibility(View.VISIBLE);
            } else if (viewingLayout4.getVisibility() == View.GONE && viewingLayout3.getVisibility() == View.VISIBLE && viewingLayout2.getVisibility() == View.VISIBLE) {
                viewingLayout4.setVisibility(View.VISIBLE);
            } else if (viewingLayout5.getVisibility() == View.GONE && viewingLayout4.getVisibility() == View.VISIBLE && viewingLayout3.getVisibility() == View.VISIBLE && viewingLayout2.getVisibility() == View.VISIBLE) {
                viewingLayout5.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "You can only add maximum of 5 Timings.", Toast.LENGTH_LONG).show();
            }
        } else if (v == minus) {
            if (viewingLayout5.getVisibility() == View.VISIBLE) {
                viewingLayout5.setVisibility(View.GONE);
            } else if (viewingLayout4.getVisibility() == View.VISIBLE && viewingLayout5.getVisibility() == View.GONE) {
                viewingLayout4.setVisibility(View.GONE);
            } else if (viewingLayout3.getVisibility() == View.VISIBLE && viewingLayout4.getVisibility() == View.GONE && viewingLayout5.getVisibility() == View.GONE) {
                viewingLayout3.setVisibility(View.GONE);
            } else if (viewingLayout2.getVisibility() == View.VISIBLE && viewingLayout3.getVisibility() == View.GONE && viewingLayout4.getVisibility() == View.GONE && viewingLayout5.getVisibility() == View.GONE) {
                viewingLayout2.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(), "Minimum of one timing is required.", Toast.LENGTH_LONG).show();
            }
        } else if (v == propertyImage) {
            showGalleryChooser();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                propertyImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Unable to load image.", Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * Gets the Image as String from Image view
     *
     * @return
     */
    public String getStringImage() {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            return encodedImage;
        } else {
            return null;
        }
    }
}