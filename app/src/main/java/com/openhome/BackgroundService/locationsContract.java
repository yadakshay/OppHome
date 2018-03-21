package com.openhome.BackgroundService;

/**
 * Created by user on 26-10-2017.
 */

import android.provider.BaseColumns;

/**
 * Created by user on 26-10-2017.
 */

public class locationsContract {

    private locationsContract(){}
    public static final class locationsEntry implements BaseColumns {
        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_LATITUDE = "movieName";
        public static final String COLUMN_LONGITUDE = "posterPath";
    }
}
