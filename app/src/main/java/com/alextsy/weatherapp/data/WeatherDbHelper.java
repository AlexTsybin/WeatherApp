package com.alextsy.weatherapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alextsy.weatherapp.data.WeatherContract.WeatherEntry;

/**
 * Created by os_mac on 14.01.18.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = WeatherDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "weather.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the cities table
        String SQL_CREATE_CITIES_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " ("
                + WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeatherEntry.COLUMN_CITY_NAME + " TEXT NOT NULL);";

        Log.v(LOG_TAG, SQL_CREATE_CITIES_TABLE);

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CITIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
