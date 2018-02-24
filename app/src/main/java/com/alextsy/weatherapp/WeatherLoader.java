package com.alextsy.weatherapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.alextsy.weatherapp.model.Weather;
import com.alextsy.weatherapp.utils.QueryUtils;

import java.util.List;

/**
 * Created by os_mac on 11.12.17.
 */

public class WeatherLoader extends AsyncTaskLoader<List<Weather>> {
    private static final String LOG_TAG = WeatherLoader.class.getName();

    /** Query URL */
    private String mUrl;

    public WeatherLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading() called...");
        forceLoad();
    }

    @Override
    public List<Weather> loadInBackground() {

        Log.i(LOG_TAG, "TEST: loadInBackground() called...");

        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of weathers.
        List<Weather> weathers = QueryUtils.fetchWeatherData(mUrl);
        return weathers;
    }
}
