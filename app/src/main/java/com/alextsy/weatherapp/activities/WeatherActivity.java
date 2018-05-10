package com.alextsy.weatherapp.activities;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alextsy.weatherapp.R;
import com.alextsy.weatherapp.WeatherAdapter;
import com.alextsy.weatherapp.WeatherLoader;
import com.alextsy.weatherapp.data.WeatherDbHelper;
import com.alextsy.weatherapp.data.WeatherContract.WeatherEntry;
import com.alextsy.weatherapp.model.Weather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements LoaderCallbacks<List<Weather>>,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = WeatherActivity.class.getName();

    WeatherDbHelper mDbHelper = new WeatherDbHelper(this);

    /** URL for weather data from the Yahoo dataset */
    private static final String BASE_URL =
            "https://query.yahooapis.com/v1/public/yql?q=";

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.weather_swipe);
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

        /*
         *
         * Попытка реализовать просмотр прогноза на неделю по нажатию на город
         *
         */

        weatherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Find the current weather that was clicked on
                Weather currentWeather = (Weather) mAdapter.getItem(position);

                // Create a new intent to go to WeatherForecast
                Intent forecastIntent = new Intent(WeatherActivity.this, WeatherForecast.class);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri currentWeatherUri = Uri.parse(currentWeather.getCity());

                forecastIntent.putExtra("cityForecast", currentWeatherUri.toString());

                // Send the intent to launch a new activity
                startActivity(forecastIntent);
            }
        });


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
        String queryStart = "select item.condition, location.city, item.description from weather.forecast where woeid in " +
                "(select woeid from geo.places(1) where text in (";

        String newCityName;
        StringBuilder endOfLine = new StringBuilder();

        {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            String[] projection = {
                    WeatherEntry._ID,
                    WeatherEntry.COLUMN_CITY_NAME };

            Cursor cursor = db.query(WeatherEntry.TABLE_NAME,
                    projection, null, null, null, null, null);

            while (cursor.moveToNext()) {
                newCityName = cursor.getString(
                        cursor.getColumnIndexOrThrow(WeatherEntry.COLUMN_CITY_NAME));
                endOfLine.append("%2C+%27" + newCityName + "%27");
            }

            cursor.close();
        }

        String queryEnd = "))";

        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder(BASE_URL);
            stringBuilder.append(URLEncoder.encode(queryStart, "UTF-8"));
            endOfLine.delete(0, 4);
            stringBuilder.append(endOfLine.toString());
            stringBuilder.append(URLEncoder.encode(queryEnd, "UTF-8"));
            stringBuilder.append("&format=json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return new WeatherLoader(this, stringBuilder.toString());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_city:
                Intent intent_catalog = new Intent(WeatherActivity.this, CatalogActivity.class);
                startActivity(intent_catalog);
                return true;

            case R.id.action_geo:
                Intent intent_geo = new Intent(WeatherActivity.this, LocationActivity.class);
                startActivity(intent_geo);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);

        getLoaderManager().restartLoader(WEATHER_LOADER_ID, null, this);

        mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
