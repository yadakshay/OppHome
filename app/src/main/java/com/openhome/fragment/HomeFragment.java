package com.openhome.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.openhome.R;
import com.openhome.activity.PropertyDetailsActivity;
import com.openhome.model.HomeContentRequest;
import com.openhome.model.HomeContentResponse;
import com.openhome.model.PropertyInfo;
import com.openhome.rest.RestCallback;
import com.openhome.rest.RestClient;
import com.openhome.rest.RestError;
import com.openhome.utils.ApplicationConstants;
import com.openhome.utils.OpenHomeUtils;
import com.openhome.utils.ShPrefManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit.client.Response;

/**
 * Created by Bhargav on 11/5/2015.
 */
public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    LinearLayout hotPropertiesLayout, savedSearchLayout;
    TextView hotTextView, savedTextView;
    private ProgressDialog progressDialog;
    private List<PropertyInfo> hotPropertiesList, primaryPropertiesList;

    private SliderLayout mDemoSlider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mDemoSlider = (SliderLayout) v.findViewById(R.id.slider);

        hotPropertiesLayout = (LinearLayout) v.findViewById(R.id.linear_hot);
        savedSearchLayout = (LinearLayout) v.findViewById(R.id.linear_saved_search);

        hotTextView = (TextView) v.findViewById(R.id.hotPropTextView);
        savedTextView = (TextView) v.findViewById(R.id.savedPropTextView);

        loadHomeContent();
        return v;
    }

    /**
     * Loads the home page content.
     */
    private void loadHomeContent() {
        showProgress(ApplicationConstants.DIALOG_HEADER, "Please wait while we load your dashboard details.");
        String userId = ShPrefManager.with(getActivity()).getUserId();
        String authToken = ShPrefManager.with(getActivity()).getToken();
        final HomeContentRequest homeContentRequest = new HomeContentRequest(userId);
        RestClient.getAPI().getHomeContent(authToken, userId, new RestCallback<HomeContentResponse>() {
            @Override
            public void failure(RestError restError) {
                hideProgress();
                OpenHomeUtils.showToast(getActivity().getApplicationContext(), restError.getErrorMessage(), Toast.LENGTH_LONG);
            }

            @Override
            public void success(HomeContentResponse homeContentResponse, Response response) {
                hideProgress();
                if (homeContentResponse.getMessage().equalsIgnoreCase("success")) {
                    hotPropertiesList = homeContentResponse.getHotPropertiesList();
                    primaryPropertiesList = homeContentResponse.getPrimaryPropertiesList();
                    setContentToViews();
                }
            }
        });
    }

    /**
     * Sets the content to hViews after getting the data from server.
     */
    public void setContentToViews() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(250, 250);
        if (primaryPropertiesList != null) {
//            HashMap<String, String> url_maps = new HashMap<String, String>();
//            url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//            url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//            url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//            url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");
            for (PropertyInfo hotProperty : primaryPropertiesList) {
                TextSliderView textSliderView = new TextSliderView(getActivity());
                // initialize a SliderLayout
                textSliderView
                        .description(hotProperty.getShortDescription())
                        .image(ApplicationConstants.PROPERTY_IMAGE_BASE_URL + hotProperty.getPropertyId() + ".jpg")
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //  RequestCreator requestCreator= Picasso.with(getActivity()).load(ApplicationConstants.PROPERTY_IMAGE_BASE_URL + hotProperty.getPropertyId() + ".jpg").placeholder(R.drawable.home_default).fit().centerCrop();

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", hotProperty.getPropertyId());

                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);
            mDemoSlider.addOnPageChangeListener(this);
//            ListView l = (ListView)findViewById(R.id.transformers);
//            l.setAdapter(new TransformerAdapter(this));
//            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    mDemoSlider.setPresetTransformer(((TextView) view).getText().toString());
//                    Toast.makeText(getActivity(), ((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
//                }
//            });


//            for (HomeContentResponse.PropertyInfo hotProperty : hotPropertiesList) {
//                ImageView imageView = new ImageView(getActivity());
//                imageView.setLayoutParams(layoutParams);
//                imageView.setId(Integer.parseInt(hotProperty.getPropertyId()));
//                imageView.setPadding(4,4,4,4);
//                Logger.logMessage("HOME FRAGMENT", ApplicationConstants.PROPERTY_IMAGE_BASE_URL + hotProperty.getPropertyId() + ".jpg");
//                Picasso.with(getActivity()).load(ApplicationConstants.PROPERTY_IMAGE_BASE_URL + hotProperty.getPropertyId() + ".jpg").placeholder(R.drawable.home_default).fit().centerCrop().into(imageView);
//                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                hotPropertiesLayout.addView(imageView);
//                addPropertyImageOnClickListener(imageView, hotProperty);
//            }
//            hotTextView.setText(hotPropertiesList.size() + " Hot Properties");
        }

        if (hotPropertiesList != null) {
            for (PropertyInfo savedProperty : hotPropertiesList) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                imageView.setId(Integer.parseInt(savedProperty.getPropertyId()));
                imageView.setPadding(4, 4, 4, 4);

                Picasso.with(getActivity()).load(ApplicationConstants.PROPERTY_IMAGE_BASE_URL + savedProperty.getPropertyId() + ".jpg").placeholder(R.drawable.home_default).fit().centerCrop().into(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                savedSearchLayout.addView(imageView);
                addPropertyImageOnClickListener(imageView, savedProperty);
            }
            savedTextView.setText(primaryPropertiesList.size() + " Hot properties.");
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //Toast.makeText(getActivity(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
        PropertyInfo propertyInfo = null;
        for (PropertyInfo propertyInLoop : primaryPropertiesList) {
            if ((String) slider.getBundle().get("extra") == propertyInLoop.getPropertyId())
                propertyInfo = propertyInLoop;
        }
        Intent intent = new Intent(getActivity(), PropertyDetailsActivity.class);
        intent.putExtra("PropertyDetails", propertyInfo);
        startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * Add onlclick listener to property image
     *
     * @param imageView
     */
    private void addPropertyImageOnClickListener(ImageView imageView, final PropertyInfo propertyInfo) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PropertyDetailsActivity.class);
                intent.putExtra("PropertyDetails", propertyInfo);
                startActivity(intent);
            }
        });
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
}