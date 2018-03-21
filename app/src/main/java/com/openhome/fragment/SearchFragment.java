package com.openhome.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.openhome.R;
import com.openhome.activity.PropertyDetailsActivity;
import com.openhome.adapter.RegionsAdapter;
import com.openhome.adapter.SearchAdapter;
import com.openhome.model.PropertyInfo;
import com.openhome.model.RegionsResponse;
import com.openhome.model.MyProfileRequest;
import com.openhome.model.SearchRefineDto;
import com.openhome.model.SearchRequest;
import com.openhome.model.SearchResponse;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.ui.SpaceItemDecoration;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.DividerItemDecoration;
import com.openhome.utils.Logger;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.RecyclerViewItemClickListener;
import com.openhome.utils.ShPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/5/2015.
 */
public class SearchFragment extends Fragment implements OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RegionsAdapter regionsAdapter;
    private SearchAdapter searchAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progressDialog;
    private Button viewListingsButton, viewListingsButton2;
    private TextView listingsCountDisplayTextView, displayingNowTextView;
    String currentAdapter = "";
    int listingsCount = 0, displayingLevel = 0;
    String userId;
    int currentDisplayingParentId = 0;
    private ImageView searchBack;

    private List<PropertyInfo> propertyList;
    private List<RegionsResponse.Regions> regionsList;
    List<RegionsResponse.Regions> adapterList = new ArrayList<>();
    private static final int VERTICAL_ITEM_SPACE = 48;
    private EditText searchView;
    private TextView cancelTextView;

    LinearLayout locationOptionButton, refineOptionButton;
    ScrollView refineLayout;
    RelativeLayout locationLayout;
    // Refine Toggle Elements
    LinearLayout refinePriceToggle, refineBedroomToggle, refineBathroomToggle, refinePropertyToggle;
    Spinner startPrice, endPrice, startBedroom, endBedroom, startBathroom, endBathroom, startProperty, sortBy;
    TextView refinePriceResultTV, refineBedroomsResultTV, refineBathroomsResultTV, refinePropertyTypeResultTV;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.lvSearchView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(2));

        viewListingsButton = (Button) v.findViewById(R.id.viewListingsButton);
        viewListingsButton2 = (Button) v.findViewById(R.id.viewListingsButton2);

        displayingNowTextView = (TextView) v.findViewById(R.id.displayingNowTextView);
        listingsCountDisplayTextView = (TextView) v.findViewById(R.id.listingsCountDisplayTextView);
        searchBack = (ImageView) v.findViewById(R.id.searchBack);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        searchView = (EditText) toolbar.findViewById(R.id.searchTextView);
        cancelTextView = (TextView) toolbar.findViewById(R.id.cancelTextView);

        refineLayout = (ScrollView) v.findViewById(R.id.refineLayout);
        locationLayout = (RelativeLayout) v.findViewById(R.id.locationLayout);
        //refineLayout.setVisibility(View.GONE);
        locationOptionButton = (LinearLayout) v.findViewById(R.id.locationOptionButton);
        refineOptionButton = (LinearLayout) v.findViewById(R.id.refineOptionButton);
        userId = ShPrefManager.with(getActivity()).getUserId();
        //tv.setTextColor(Color.parseColor("#FFFFFF"));
        setupListeners();
        setupToggleforRefineLayout(v);
        //loadSearchResults();
        if (regionsList == null)
            loadSearchRegions();
        return v;
    }


    /**
     * Setup Listeners
     */
    private void setupListeners() {
//        displayingNowTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (RegionsResponse.Regions region : regionsList) {
//                    adapterList.clear();
//                    if(region.getParentId() == currentDisplayingParentId){
//                        adapterList.add(region);
//                    }
//                }
//                refreshList();
//            }
//        });
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // TODO Auto-generated method stub
//                loadSearchResults(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // TODO Auto-generated method stub
//
//                return false;
//            }
//        });
//
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean queryTextFocused) {
//                if (!queryTextFocused) {
//                    cancelTextView.setVisibility(View.VISIBLE);
//                } else {
//                    cancelTextView.setVisibility(View.GONE);
//                }
//            }
//        });

        locationOptionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                locationLayout.setVisibility(View.VISIBLE);
                refineLayout.setVisibility(View.GONE);
                displayingNowTextView.setText("Regions");
            }
        });

        refineOptionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                locationLayout.setVisibility(View.GONE);
                refineLayout.setVisibility(View.VISIBLE);
                displayingNowTextView.setText("Refine");
            }
        });


        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SearchRequest request = new SearchRequest();
                    request.setSearchText(v.getText().toString());
                    request.setUserId(Integer.parseInt(userId));
                    request.setSearchType("");
                    request.setRefineOptions(new SearchRefineDto());
                    request.setSortBy(ApplicationConstants.LATEST_LISTINGS);
                    request.setLimitEnd(0);
                    request.setLimitStart(0);
                    loadSearchResults(request);
                    return true;
                }
                return false;
            }
        });

        viewListingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (searchView.getText().toString().trim().length() == 0 && (displayingNowTextView.getText().toString().equals("Regions"))) {
                    String message = "Please enter search text or select region(s) to search for property listings.";
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.try_again,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getActivity().getFragmentManager(), "SearchRegionsFragment");
                } else {
                    String searchText = searchView.getText().toString();
                    ArrayList<String> locations = new ArrayList<String>();

                    for (RegionsResponse.Regions region : regionsList) {
                        if (region.isSelected())
                            locations.add(region.getRegionName());
                    }
                    if (locations.size() == 0)
                        locations.add(displayingNowTextView.getText().toString().trim());
                    SearchRequest request = new SearchRequest();
                    request.setSearchText(searchText);
                    request.setUserId(Integer.parseInt(userId));
                    request.setSearchType((locations.isEmpty()) ? "" : ApplicationConstants.LOCATION);
                    SearchRefineDto refine = new SearchRefineDto();
                    refine.setLocation(locations);
                    request.setRefineOptions(refine);
                    request.setSortBy(ApplicationConstants.LATEST_LISTINGS);
                    request.setLimitEnd(0);
                    request.setLimitStart(0);
                    loadSearchResults(request);
                }
            }
        });

        viewListingsButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String startPriceVal = startPrice.getSelectedItem().toString();
                String endPriceVal = endPrice.getSelectedItem().toString();
                String startBedVal = startBedroom.getSelectedItem().toString();
                String endBedVal = endBedroom.getSelectedItem().toString();
                String startBathVal = startBathroom.getSelectedItem().toString();
                String endBathVal = endBathroom.getSelectedItem().toString();
                String propertyTypeVal = startProperty.getSelectedItem().toString();
                String sortByVal = sortBy.getSelectedItem().toString();

                if (startBathVal.equals("6+")) {
                    startBathVal = "100";
                }
                if (endBathVal.equals("6+")) {
                    endBathVal = "100";
                }
                if (startBedVal.equals("6+")) {
                    startBedVal = "100";
                }
                if (endBedVal.equals("6+")) {
                    endBedVal = "100";
                }

                SearchRequest request = new SearchRequest();
                request.setSearchText(searchView.getText().toString());
                request.setUserId(Integer.parseInt(userId));
                request.setSearchType(ApplicationConstants.REFINE);
                SearchRefineDto refine = new SearchRefineDto();
                refine.setLocation(new ArrayList<String>());
                refine.setBathrooms(new SearchRefineDto().new Bathrooms(startBathVal, endBathVal));
                refine.setBedrooms(new SearchRefineDto().new Bedrooms(startBedVal, endBedVal));
                refine.setPrice(new SearchRefineDto().new Price(startPriceVal, endPriceVal));
                refine.setPropertyType(propertyTypeVal);
                request.setRefineOptions(refine);
                request.setSortBy((sortByVal.equals("Choose Sort by...")) ? "" : sortByVal);
                request.setLimitEnd(0);
                request.setLimitStart(0);
                loadSearchResults(request);
            }
        });

        searchBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPreviousSearchState();
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getActivity(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (mRecyclerView.getAdapter() instanceof RegionsAdapter) {
                    RegionsResponse.Regions selectedRegion = adapterList.get(position);
                    displayingLevel = selectedRegion.getLevel();
                    Logger.logMessage(TAG, "addOnItemTouchListener()::selectedRegion ID:: " + selectedRegion.getRegionId());
                    Logger.logMessage(TAG, "addOnItemTouchListener()::selected region level ID:: " + selectedRegion.getLevel());
                    if (selectedRegion.getLevel() == 1 || selectedRegion.getLevel() == 2) {
                        boolean hasChildren = false;
                        for (RegionsResponse.Regions region : regionsList) {
                            Logger.logMessage("SEARCH_FRAGMENT region Parent ID::::: ", region.getParentId() + "");
                            if (region.getParentId() == selectedRegion.getRegionId()) {
                                currentDisplayingParentId = region.getParentId();
                                listingsCount = 0;
                                listingsCount += selectedRegion.getListingsCount();
                                if (!hasChildren) {
                                    adapterList.clear();
                                    Logger.logMessage(TAG, "addOnItemTouchListener()::List Count::After Clearing " + adapterList.size());
                                    regionsAdapter.notifyDataSetChanged();
                                    regionsAdapter.notifyItemRangeRemoved(0, adapterList.size());
                                    hasChildren = true;
                                }
                                Logger.logMessage("SEARCH_FRAGMENT Parent ID matched with Selected Region ID::::: ", selectedRegion.getRegionName() + "");
                                adapterList.add(region);
                                listingsCountDisplayTextView.setText(listingsCount + " Listings");
                                searchBack.setVisibility(View.VISIBLE);
                                displayingNowTextView.setText(selectedRegion.getRegionName());
                            }
                        }
                        if (!hasChildren)
                            OpenHomeUtils.showToast(getActivity().getApplicationContext(), "No sub regions to display.", Toast.LENGTH_SHORT);
                        Logger.logMessage("SEARCH_FRAGMENT Adapter list size after filtering::::: ", adapterList.size() + "");
                    } else {
                        if (selectedRegion.isSelected())
                            selectedRegion.setIsSelected(false);
                        else
                            selectedRegion.setIsSelected(true);
                    }
                    refreshList();

                } else if (mRecyclerView.getAdapter() instanceof SearchAdapter) {
                    PropertyInfo propertyInfo = propertyList.get(position);
                    Intent intent = new Intent(getActivity(), PropertyDetailsActivity.class);
                    intent.putExtra("PropertyDetails", propertyInfo);
                    startActivity(intent);
                }
            }
        }));
    }

    /**
     * Search back implementaion. Navigates to previous search state.
     */
    private void navigateToPreviousSearchState() {
        Logger.logMessage(TAG, "Current adapter in nav back is" + currentAdapter);
        switch (currentAdapter) {
            case "R":
                navToPreviousRegions();
                break;
            case "S":
                mRecyclerView.setAdapter(regionsAdapter);
                if (currentDisplayingParentId == 0) {
                    displayingNowTextView.setText("Regions");
                    searchBack.setVisibility(View.GONE);
                } else {
                    navToPreviousRegions();
                }
                currentAdapter = "R";
                break;
        }
    }

    private void navToPreviousRegions() {

        if (adapterList.size() > 0) {
            RegionsResponse.Regions currentRegion = adapterList.get(0);
            int currentRegionLevel = currentRegion.getLevel();
            if (currentRegionLevel > 1) {
                int levelToBeDisplayedOnBack = currentRegionLevel - 1;
                adapterList.clear();
                regionsAdapter.notifyDataSetChanged();
                int listingsCount = 0;
                for (RegionsResponse.Regions region : regionsList) {
                    if (region.getLevel() == levelToBeDisplayedOnBack) {
                        adapterList.add(region);
                        listingsCount += region.getListingsCount();

                    }
                }
                listingsCountDisplayTextView.setText(listingsCount + " Listings");
                regionsAdapter.notifyDataSetChanged();
                // For getting Region Name
                currentRegion = adapterList.get(0);
                int parentId = currentRegion.getParentId();
                if (parentId == 0)
                    displayingNowTextView.setText("Regions");
                else
                    for (RegionsResponse.Regions region : regionsList) {
                        if (region.getRegionId() == parentId)
                            displayingNowTextView.setText(region.getRegionName());
                    }
            } else {
                displayingNowTextView.setText("Regions");
                int listingsCount = 0;
                for (RegionsResponse.Regions region : regionsList) {
                    listingsCount += region.getListingsCount();
                    listingsCountDisplayTextView.setText(listingsCount + " Listings");
                }
            }
        }
        if (displayingNowTextView.getText().toString().equalsIgnoreCase("Regions"))
            searchBack.setVisibility(View.GONE);
    }

    private void refreshList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                regionsAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Method to load search list
     */
    private void loadSearchRegions() {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we retrieve matching results.");
        String authToken = ShPrefManager.with(getActivity()).getToken();
        RestClient.getAPI().getRegions(authToken, new RestCallback<RegionsResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getActivity().getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(RegionsResponse regionsResponse, Response response) {
                hideProgress();
                if (regionsResponse.getMessage().size() > 0) {
                    regionsList = regionsResponse.getMessage();
                    for (RegionsResponse.Regions region : regionsList) {
                        listingsCount += region.getListingsCount();
                        if (region.getParentId() == 0)
                            adapterList.add(region);
                    }
                    regionsAdapter = new RegionsAdapter(getActivity(), adapterList, 0);
                    mRecyclerView.setAdapter(regionsAdapter);
                    currentAdapter = "R";
                    listingsCountDisplayTextView.setText(listingsCount + " Listings");
                    currentDisplayingParentId = 0;

                } else {
                    String message = "No results to display. Please modify the search criteria and try again.";
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.try_again,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getActivity().getFragmentManager(), "SearchRegionsFragment");
                }
            }
        });
    }

    /**
     * Method to load search list
     */
    private void loadSearchResults(SearchRequest request) {
        showProgress(null, "Loading search results...");

        String authToken = ShPrefManager.with(getActivity()).getToken();
        RestClient.getAPI().search(authToken, request, new RestCallback<SearchResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getActivity().getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(SearchResponse searchResponse, Response response) {
                hideProgress();
                if (searchResponse.getMessage().size() > 0) {
                    propertyList = searchResponse.getMessage();
                    String userId = ShPrefManager.with(getActivity()).getUserId();
                    searchAdapter = new SearchAdapter(getActivity(), propertyList, userId, 'S');
                    mRecyclerView.setAdapter(searchAdapter);
                    currentAdapter = "S";
                    //displayingNowTextView.setText("Searching \"" + searchText + "\"");
                    searchBack.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
                    locationLayout.setVisibility(View.VISIBLE);
                    refineLayout.setVisibility(View.GONE);
                } else {
                    String message = "No results to display. Please modify the search criteria and try again.";
                    CustomDialogFragment regSuccessDialogFragment = CustomDialogFragment.newInstance(R.string.try_again,
                            message, ApplicationConstants.BUTTON_OK, 0);
                    regSuccessDialogFragment.show(getActivity().getFragmentManager(), "SearchResultsFragment");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentAdapter.equalsIgnoreCase("R"))
            mRecyclerView.setAdapter(regionsAdapter);
        else if (currentAdapter.equalsIgnoreCase("S"))
            mRecyclerView.setAdapter(searchAdapter);
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

    /**
     * Sets up the toggle UI
     */
    public void setupToggleforRefineLayout(View v) {
        // Initialize
        refinePriceResultTV = (TextView) v.findViewById(R.id.refinePriceResultTV);
        refineBedroomsResultTV = (TextView) v.findViewById(R.id.refineBedroomsResultTV);
        refineBathroomsResultTV = (TextView) v.findViewById(R.id.refineBathroomsResultTV);
        refinePropertyTypeResultTV = (TextView) v.findViewById(R.id.refinePropertyTypeResultTV);

        refinePriceToggle = (LinearLayout) v.findViewById(R.id.refinePriceToggle);
        refineBedroomToggle = (LinearLayout) v.findViewById(R.id.refineBedroomToggle);
        refineBathroomToggle = (LinearLayout) v.findViewById(R.id.refineBathroomToggle);
        refinePropertyToggle = (LinearLayout) v.findViewById(R.id.refinePropertyToggle);

        RelativeLayout refinePriceToggleTrigger = (RelativeLayout) v.findViewById(R.id.refinePriceToggleTrigger);
        RelativeLayout refineBathroomToggleTrigger = (RelativeLayout) v.findViewById(R.id.refineBathroomToggleTrigger);
        RelativeLayout refineBedroomToggleTrigger = (RelativeLayout) v.findViewById(R.id.refineBedroomToggleTrigger);
        RelativeLayout refinePropertyTypeToggleTrigger = (RelativeLayout) v.findViewById(R.id.refinePropertyTypeToggleTrigger);

        refinePriceToggleTrigger.setOnClickListener(this);
        refineBathroomToggleTrigger.setOnClickListener(this);
        refineBedroomToggleTrigger.setOnClickListener(this);
        refinePropertyTypeToggleTrigger.setOnClickListener(this);

        refinePriceToggleTrigger.setTag("price");
        refineBedroomToggleTrigger.setTag("bed");
        refineBathroomToggleTrigger.setTag("bath");
        refinePropertyTypeToggleTrigger.setTag("type");

        startPrice = (Spinner) v.findViewById(R.id.startPrice);
        endPrice = (Spinner) v.findViewById(R.id.endPrice);
        startBedroom = (Spinner) v.findViewById(R.id.startBed);
        endBedroom = (Spinner) v.findViewById(R.id.endBed);
        startBathroom = (Spinner) v.findViewById(R.id.startBath);
        endBathroom = (Spinner) v.findViewById(R.id.endBath);
        startProperty = (Spinner) v.findViewById(R.id.startProperty);
        sortBy = (Spinner) v.findViewById(R.id.sortBy);

        startPrice.setTag("SPR");
        endPrice.setTag("EPR");
        startBedroom.setTag("SBD");
        endBedroom.setTag("EBD");
        startBathroom.setTag("SBT");
        endBathroom.setTag("EBT");
        startProperty.setTag("SPT");

        startPrice.setOnItemSelectedListener(this);
        endPrice.setOnItemSelectedListener(this);
        startBedroom.setOnItemSelectedListener(this);
        endBedroom.setOnItemSelectedListener(this);
        startBathroom.setOnItemSelectedListener(this);
        endBathroom.setOnItemSelectedListener(this);
        startProperty.setOnItemSelectedListener(this);

    }

    /**
     * To Show date picker dialog (for expiry date)
     *
     * @param v
     */
    public void onClick(View v) {
        switch ((String) v.getTag()) {
            case "price":
                if (refinePriceToggle.isShown()) {
                    slideUp(getActivity(), refinePriceToggle);
                    refinePriceToggle.setVisibility(View.GONE);
                } else {
                    refinePriceToggle.setVisibility(View.VISIBLE);
                    slideDown(getActivity(), refinePriceToggle);

                    slideUp(getActivity(), refineBedroomToggle);
                    slideUp(getActivity(), refineBathroomToggle);
                    slideUp(getActivity(), refinePropertyToggle);
                    // Hider Other Toggles
                    refineBedroomToggle.setVisibility(View.GONE);
                    refineBathroomToggle.setVisibility(View.GONE);
                    refinePropertyToggle.setVisibility(View.GONE);
                }
                break;
            case "bed":
                if (refineBedroomToggle.isShown()) {
                    slideUp(getActivity(), refineBedroomToggle);
                    refineBedroomToggle.setVisibility(View.GONE);
                } else {
                    refineBedroomToggle.setVisibility(View.VISIBLE);
                    slideDown(getActivity(), refineBedroomToggle);
                    slideUp(getActivity(), refinePriceToggle);
                    slideUp(getActivity(), refineBathroomToggle);
                    slideUp(getActivity(), refinePropertyToggle);
                    // Hider Other Toggles
                    refinePriceToggle.setVisibility(View.GONE);
                    refineBathroomToggle.setVisibility(View.GONE);
                    refinePropertyToggle.setVisibility(View.GONE);
                }
                break;
            case "bath":
                if (refineBathroomToggle.isShown()) {
                    slideUp(getActivity(), refineBathroomToggle);
                    refineBathroomToggle.setVisibility(View.GONE);
                } else {
                    refineBathroomToggle.setVisibility(View.VISIBLE);
                    slideDown(getActivity(), refineBathroomToggle);
                    slideUp(getActivity(), refineBedroomToggle);
                    slideUp(getActivity(), refinePriceToggle);
                    slideUp(getActivity(), refinePropertyToggle);
                    // Hider Other Toggles
                    refineBedroomToggle.setVisibility(View.GONE);
                    refinePriceToggle.setVisibility(View.GONE);
                    refinePropertyToggle.setVisibility(View.GONE);
                }
                break;
            case "type":
                if (refinePropertyToggle.isShown()) {
                    slideUp(getActivity(), refinePropertyToggle);
                    refinePropertyToggle.setVisibility(View.GONE);
                } else {
                    refinePropertyToggle.setVisibility(View.VISIBLE);
                    slideDown(getActivity(), refinePropertyToggle);
                    slideUp(getActivity(), refineBedroomToggle);
                    slideUp(getActivity(), refineBathroomToggle);
                    slideUp(getActivity(), refinePriceToggle);
                    // Hider Other Toggles
                    refineBedroomToggle.setVisibility(View.GONE);
                    refineBathroomToggle.setVisibility(View.GONE);
                    refinePriceToggle.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null)
            if (parent.getTag().equals("SPR") || parent.getTag().equals("EPR")) {
                refinePriceResultTV.setText(startPrice.getSelectedItem().toString() + " - " + endPrice.getSelectedItem().toString());
            } else if (parent.getTag().equals("SBD") || parent.getTag().equals("EBD")) {
                refineBedroomsResultTV.setText(startBedroom.getSelectedItem().toString() + " - " + endBedroom.getSelectedItem().toString());
            } else if (parent.getTag().equals("SBT") || parent.getTag().equals("EBT")) {
                refineBathroomsResultTV.setText(startBathroom.getSelectedItem().toString() + " - " + endBathroom.getSelectedItem().toString());
            } else if (parent.getTag().equals("SPT")) {
                refinePropertyTypeResultTV.setText(startProperty.getSelectedItem().toString());
            }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static void slideDown(Context ctx, View v) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slideUp(Context ctx, View v) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

}