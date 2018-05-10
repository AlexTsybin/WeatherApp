package com.alextsy.weatherapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by os_mac on 14.01.18.
 */

public final class WeatherContract {

    private WeatherContract() {}

    public static final String CONTENT_AUTHORITY = "com.alextsy.weatherapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CITIES = "cities";

    public static final class WeatherEntry implements BaseColumns {

        /** The content URI to access the city data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CITIES);

        public final static String TABLE_NAME = "cities";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_CITY_NAME = "name";

        // The MIME type of the {@link #CONTENT_URI} for a list of cities.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITIES;

        //The MIME type of the {@link #CONTENT_URI} for a single city.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITIES;

    }

}
