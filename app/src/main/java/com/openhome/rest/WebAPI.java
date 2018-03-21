package com.openhome.rest;

import com.openhome.model.AddPropertyRequest;
import com.openhome.model.ChangePasswordRequest;
import com.openhome.model.DashboardResponse;
import com.openhome.model.ErrorLog;
import com.openhome.model.ForgotPasswordRequest;
import com.openhome.model.HazardsResponse;
import com.openhome.model.HazardsResponse2;
import com.openhome.model.HomeContentResponse;
import com.openhome.model.ImageUploadRequest;
import com.openhome.model.LoginRequest;
import com.openhome.model.LoginResponse;
import com.openhome.model.ProfileResponse;
import com.openhome.model.RegionsResponse;
import com.openhome.model.RegistrationRequest;
import com.openhome.model.ResponseData;
import com.openhome.model.ScannedCodesRequest;
import com.openhome.model.ScheduleEventRequest;
import com.openhome.model.SearchRequest;
import com.openhome.model.SearchResponse;
import com.openhome.model.StandartPropertyListResponse;
import com.openhome.model.VisitorRegistrationRequest;
import com.openhome.model.VisitorResponse;
import com.openhome.model.WatchlistRequest;
import com.openhome.utils.ApplicationConstants;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Bhargav
 */
public interface WebAPI {

    String AUTHORIZATION_HEADER = "Authorization";

    String PATH_VALUE_USER_ID = "userId";

    String PATH_VALUE_AGENT_ID = "agentId";

    String PATH_VALUE_PROPERTY_ID = "propertyId";

    String PATH_VALUE_REQUEST_TYPE = "requestType";

    String PATH_VALUE_ARCHIVE_STATUS = "archiveStatus";

    @POST(ApplicationConstants.API_LOGIN_URL)
    void login(@Body LoginRequest loginRequest, RestCallback<LoginResponse> callback);

    @POST(ApplicationConstants.API_REGISTER_URL)
    void registerUser(@Body RegistrationRequest registrationRequest, RestCallback<LoginResponse> callback);

    /*******Added by Akshay*********/
    @POST(ApplicationConstants.API_REGISTER_VISITOR_URL)
    void registerVisitorUser(@Body VisitorRegistrationRequest registrationRequest, RestCallback<VisitorResponse> callback);

    @GET(ApplicationConstants.API_GET_DASHBOARD_CONTENT)
    void getDashboardContent(@Header(AUTHORIZATION_HEADER) String token, @Path(value = PATH_VALUE_USER_ID) String userId, RestCallback<DashboardResponse> callback);

    @GET(ApplicationConstants.API_GET_HOME_CONTENT)
    void getHomeContent(@Header(AUTHORIZATION_HEADER) String token, @Path(value = PATH_VALUE_USER_ID) String userId, RestCallback<HomeContentResponse> callback);

    @GET(ApplicationConstants.API_GET_USER_PROFILE)
    void getUserProfile(@Header(AUTHORIZATION_HEADER) String token, @Path(value = PATH_VALUE_USER_ID) String userId, RestCallback<ProfileResponse> callback);

    @POST(ApplicationConstants.API_UPLOAD_PROFILE_IMAGE)
    void updateProfileImage(@Header(AUTHORIZATION_HEADER) String token, @Body ImageUploadRequest imageUploadRequest, RestCallback<ResponseData> callback);

    @GET(ApplicationConstants.API_GET_AGENT_CURRENT_LISTINGS)
    void getPropertiesByAgent(@Header(AUTHORIZATION_HEADER) String token, @Path(value = PATH_VALUE_AGENT_ID) String agentId, @Query("listingsType") String listingType, RestCallback<StandartPropertyListResponse> callback);

    @PUT(ApplicationConstants.API_EDIT_USER_PROFILE)
    void editProfile(@Header(AUTHORIZATION_HEADER) String token, @Body RegistrationRequest registrationRequest, RestCallback<LoginResponse> callback);

    @POST(ApplicationConstants.API_SEARCH_URL)
    void search(@Header(AUTHORIZATION_HEADER) String token, @Body SearchRequest searchRequest, RestCallback<SearchResponse> callback);

    @POST(ApplicationConstants.API_ADD_TO_WATCHLIST_URL)
    void addToWatchlist(@Header(AUTHORIZATION_HEADER) String token, @Body WatchlistRequest watchlistRequest, RestCallback<ResponseData> callback);

    @DELETE(ApplicationConstants.API_REMOVE_FROM_WATCHLIST_URL)
    void removeFromWatchlist(@Header(AUTHORIZATION_HEADER) String token, @Path(value = PATH_VALUE_PROPERTY_ID) String propertyId, @Path(value = PATH_VALUE_USER_ID) String userId, RestCallback<ResponseData> callback);

    @GET(ApplicationConstants.API_GET_WATCHLIST_URL)
    void getWatchlist(@Header(AUTHORIZATION_HEADER) String token, @Path(value = PATH_VALUE_USER_ID) String userId, RestCallback<SearchResponse> callback);

    @GET(ApplicationConstants.API_GET_REGIONS)
    void getRegions(@Header(AUTHORIZATION_HEADER) String token, RestCallback<RegionsResponse> callback);

    @POST(ApplicationConstants.API_ADD_PROPERTY)
    void addProperty(@Header(AUTHORIZATION_HEADER) String token, @Body AddPropertyRequest addPropertyRequest, RestCallback<ResponseData> callback);

    @PUT(ApplicationConstants.API_UPDATE_MORE_INFORMATION_REQUESTED_URL)
    void updatePropertyInfo(@Header(AUTHORIZATION_HEADER) String token, @Path(value = PATH_VALUE_REQUEST_TYPE) String requestType, @Path(value = PATH_VALUE_PROPERTY_ID) String propertyId, @Path(value = PATH_VALUE_USER_ID) String userId, RestCallback<ResponseData> callback);

    @PUT(ApplicationConstants.API_UPDATE_PROPERTY_ARCHIVE_STATUS)
    void updateArchiveStatus(@Header(AUTHORIZATION_HEADER) String token, @Path(value = PATH_VALUE_AGENT_ID) String agentId, @Path(value = PATH_VALUE_PROPERTY_ID) String propertyId, @Path(value = "archiveStatus") int archiveStatus, RestCallback<ResponseData> callback);

    @POST(ApplicationConstants.API_UPDATE_SCANNED_CODES_IMAGE)
    void updateScannedCodes(@Header(AUTHORIZATION_HEADER) String token, @Body ScannedCodesRequest scannedCodesRequest, RestCallback<ResponseData> callback);

    @GET(ApplicationConstants.API_GET_NEAR_BY_PROPERTIES)
    void getNearByProperties(@Header(AUTHORIZATION_HEADER) String token,
                             @Path(value = PATH_VALUE_USER_ID) String userId,
                             @Path(value = "lattitude") String lattitude,
                             @Path(value = "longitude") String longitude,
                             @Path(value = "proximityRadius") String proximityRadius, RestCallback<SearchResponse> callback);

    @POST(ApplicationConstants.API_ADD_SCHEDULE_EVENT)
    void addScheduleEvent(@Header(AUTHORIZATION_HEADER) String token, @Body ScheduleEventRequest scheduleEventRequest, RestCallback<ResponseData> callback);

    @GET(ApplicationConstants.API_GET_ALL_HAZARDS)
    void getAllHazards(@Header(AUTHORIZATION_HEADER) String token, RestCallback<HazardsResponse2> callback);

    @GET(ApplicationConstants.API_GET_PROPERTY_HAZARDS)
    void getPropertyHazards(@Header(AUTHORIZATION_HEADER) String token, @Path(value = "propertyId") String propertyId, @Query(value = "userId") String userId, RestCallback<HazardsResponse> callback);

    @POST(ApplicationConstants.API_CHANGE_PASSWORD)
    void changePassword(@Header(AUTHORIZATION_HEADER) String token, @Body ChangePasswordRequest changePasswordRequest, RestCallback<ResponseData> callback);

    @POST(ApplicationConstants.API_FORGOT_PASSWORD)
    void forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest, RestCallback<ResponseData> callback);

    @POST("/errorReport")
    void reportErrorToServer(@Body ErrorLog errorLog, RestCallback<String> callback);


    @DELETE(ApplicationConstants.API_DELETE_PROPERTY)
    void deleteProperty(@Header(AUTHORIZATION_HEADER) String token, @Path(value = "propertyId") String propertyId, RestCallback<ResponseData> callback);

}
