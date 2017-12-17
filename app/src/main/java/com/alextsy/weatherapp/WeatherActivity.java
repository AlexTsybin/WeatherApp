package com.alextsy.weatherapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements LoaderCallbacks<List<Weather>>,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = WeatherActivity.class.getName();

    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://query.yahooapis.com/v1/public/yql?q=select%20item.condition%20from%20weather.forecast%20where%20woeid%20%3D%202123260&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    /**
     * Constant value for the weather loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int WEATHER_LOADER_ID = 1;

    /** Adapter for the list of weathers */
    private WeatherAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Find a reference to the {@link ListView} in the layout
        ListView weatherListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        weatherListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of weathers as input
        mAdapter = new WeatherAdapter(this, new ArrayList<Weather>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        weatherListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(WEATHER_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Weather>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new WeatherLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Weather>> loader, List<Weather> weathers) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No weathers found."
        mEmptyStateTextView.setText(R.string.no_weathers);

        // Clear the adapter of previous weather data
        mAdapter.clear();

        // If there is a valid list of {@link Weather}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (weathers != null && !weathers.isEmpty()) {
            mAdapter.addAll(weathers);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Weather>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public void onRefresh() {

        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);

        getLoaderManager().restartLoader(WEATHER_LOADER_ID, null, this);

        mSwipeRefreshLayout.setRefreshing(false);

    }
}
