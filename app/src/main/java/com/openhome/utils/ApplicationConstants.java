package com.openhome.utils;

import com.openhome.R;

/**
 * Created by Bhargav
 */
public class ApplicationConstants {
    //Rest client configuration constants
    public static final int CONNECTION_TIME_OUT = 15000;
    public static final int READ_TIME_OUT = 15000;
    public static final int WRITE_TIME_OUT = 15000;

    public static final String DIALOG_HEADER = "Ophttp://www.opennhome.com/api/images/qrcode/en Home";


    public static final String BASE_URL = "http://www.opennhome.com/api";

    public static final String PROFILE_IMAGE_BASE_URL = "http://www.opennhome.com/api/images/profile/";

    public static final String PROPERTY_IMAGE_BASE_URL = "http://www.opennhome.com/images/property/";

  //  public static final String QRCODE_IMAGE_BASE_URL = "http://www.opennhome.com/api/images/qrcode/";
    public static final String QRCODE_IMAGE_BASE_URL = "http://www.opennhome.com/api/images/qrcode/";

    public static final String API_REGISTER_URL = "/register";

    public static final String API_REGISTER_VISITOR_URL = "/registerVisitor";

    public static final String API_LOGIN_URL = "/login";

    public static final String API_GET_DASHBOARD_CONTENT = "/protected/dashboard/{userId}";

    public static final String API_GET_HOME_CONTENT = "/protected/publicHome/{userId}";

    public static final String API_GET_USER_PROFILE = "/protected/user/{userId}";

    public static final String API_UPLOAD_PROFILE_IMAGE = "/protected/uploadProfileImage";

    public static final String API_UPDATE_MORE_INFORMATION_REQUESTED_URL = "/protected/updatePropertyInfo/{requestType}/{propertyId}/{userId}";

    public static final String API_UPDATE_PROPERTY_ARCHIVE_STATUS = "/protected/property/moveToArchive/{agentId}/{propertyId}/{archiveStatus}";


    public static final String API_SEARCH_URL = "/protected/v2/search";
    public static final String API_ADD_TO_WATCHLIST_URL = "/protected/watchlist/add";
    public static final String API_GET_WATCHLIST_URL = "/protected/watchlist/{userId}";
    public static final String API_REMOVE_FROM_WATCHLIST_URL = "/protected/watchlist/delete/{propertyId}/{userId}";



    public static final String API_EDIT_USER_PROFILE = "/protected/user/edit";

    public static final String API_GET_REGIONS = "/protected/getRegions";


    public static final String API_ADD_PROPERTY = "/protected/addProperty";

    public static final String API_GET_AGENT_CURRENT_LISTINGS = "/protected/getAgentProperties/{agentId}";

    public static final String API_UPDATE_SCANNED_CODES_IMAGE = "/protected/addScannedCodes";

    public static final String API_GET_NEAR_BY_PROPERTIES = "/protected/nearByProperties/{userId}/{lattitude}/{longitude}/{proximityRadius}";

    public static final String API_ADD_SCHEDULE_EVENT = "/protected/addScheduleEvent";

    public static final String API_GET_ALL_HAZARDS = "/protected/getAllHazards";

    public static final String API_GET_PROPERTY_HAZARDS = "/protected/getHazardsByProperty/{propertyId}";

    public static final String API_CHANGE_PASSWORD = "/protected/changePassword";

    public static final String API_FORGOT_PASSWORD = "/forgotPassword";

    public static final String API_DELETE_PROPERTY = "/protected/property/delete/{propertyId}";

    public static final int ARCHIVED_STATUS = 1, CURRENT_STATUS = 0, ALL_STATUSES = 3;

    public static final String LISTINGS_TYPE = "listingsType";

    public static final String LISTINGS_TYPE_CURRENT = "current";
    public static final String LISTINGS_TYPE_ARCHIVED = "archived";
    public static final String LISTINGS_TYPE_ALL = "all";

    public static final String IMAGE_PNG_EXTENSION = ".png";

    public static final String MOS_CHOOSE = "Choose method of sale";
    public static final String MOS_ASKING = "Asking Price";
    public static final String MOS_ENQUIRIES = "Enquiries over";
    public static final String MOS_AUCTIONED = "To be auctioned on";
    public static final String MOS_NEGOTIATION = "Price by negotiation";

    public static final int MORE_INFO_REQUESTED = 1;
    public static final int MORE_INFO_NOT_REQUESTED = 0;

    public static final String OPEN_SEARCH = "OPEN_SEARCH";
    public static final String LOCATION = "LOCATION";
    public static final String REFINE = "REFINE";

    public static final String RESIDENTIAL = "RESIDENTIAL";
    public static final String COMMERCIAL = "RURAL";
    public static final String RURAL = "RURAL";
    public static final String RETIREMENT_VILLAGES = "RETIREMENT VILLAGES";

    public static final String FEATURED_FIRST = "FEATURED_FIRST";
    public static final String PRICE_LOWEST = "PRICE_LOWEST";
    public static final String PRICE_HGHEST = "PRICE_HGHEST";
    public static final String LATEST_LISTINGS = "LATEST_LISTINGS";

    public static final String ANY = "ANY";

    //Dialog fragments constants
    public static final int BUTTON_OK = 1;
    public static final int BUTTON_TRY_AGAIN = 2;
    public static final int BUTTON_PROCEED = 3;
    public static final int BUTTON_CANCEL = 4;
    public static final int OPERATION_FAILED = 5;
    public static final int BUTTON_YES = 6;
    public static final int BUTTON_NO = 7;
    public static final int BUTTON_DASH = 8;
    public static final int BUTTON_ACCEPT = 9;
    public static final int BUTTON_REJECT = 10;

    public static final String DATE_SEPERATOR = "-";
    public static final String TIME_SEPERATOR = ":";
    public static final String SPACE_SEPERATOR = " ";
    public static final String COMMA_SEPERATOR = ", ";

    public static int getActionTitle(int action) {
        switch (action) {
            case BUTTON_OK:
                return R.string.dialog_action_ok;
            case BUTTON_TRY_AGAIN:
                return R.string.dialog_action_try_again;
            case BUTTON_PROCEED:
                return R.string.dialog_action_proceed;
            case BUTTON_CANCEL:
                return R.string.dialog_action_cancel;
            case OPERATION_FAILED:
                return R.string.dialog_action_ok;
            case BUTTON_YES:
                return R.string.dialog_action_yes;
            case BUTTON_NO:
                return R.string.dialog_action_no;
            case BUTTON_DASH:
                return R.string.dialog_action_ok;
            case BUTTON_ACCEPT:
                return R.string.dialog_action_Accept;
            case BUTTON_REJECT:
                return R.string.dialog_action_Reject;
            default:
                return R.string.dialog_action_ok;
        }
    }
}
