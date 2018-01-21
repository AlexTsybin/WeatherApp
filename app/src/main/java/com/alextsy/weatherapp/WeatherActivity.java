package com.alextsy.weatherapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alextsy.weatherapp.data.WeatherContract;
import com.alextsy.weatherapp.data.WeatherContract.WeatherEntry;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WeatherActivity extends AppCompatActivity implements LoaderCallbacks<List<Weather>>, SharedPreferences.OnSharedPreferenceChangeListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = WeatherActivity.class.getName();

    /** URL for weather data from the Yahoo dataset */
    private static final String BASE_URL =
            "https://query.yahooapis.com/v1/public/yql?q=";

//    "https://query.yahooapis.com/v1/public/yql?q=select%20item.condition%2C%20location.city%2C%20item.description%20from%20weather.forecast%20where%20woeid%20in%20" +
//                    "(2123260%2C%202111479%2C%202120169%2C%202122265%2C%202122296%2C%202086230%2C%202112237%2C%202065482)" +
//                            "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

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

        // Obtain a reference to the SharedPreferences file for this app
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_min_magnitude_key))) {
            mAdapter.clear();

            mEmptyStateTextView.setVisibility(View.GONE);

            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(WEATHER_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<Weather>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key), getString(R.string.settings_min_city_default));

//        SQLiteDatabase myDB = openOrCreateDatabase("my.db", MODE_PRIVATE, null);
//        myDB.execSQL(
//                "CREATE TABLE IF NOT EXISTS user (name VARCHAR(200))"
//        );
//
//        ContentValues row1 = new ContentValues();
//        row1.put("name", "Kaluga");
//        ContentValues row2 = new ContentValues();
//        row2.put("name", "Astana");
//
//        myDB.insert("user", null, row1);
//        myDB.insert("user", null, row2);
//
//        Cursor myCursor = myDB.rawQuery("select name from user", null);
//
//        String cityNameFromDb = "";
//
//
//
//        StringBuilder strB = new StringBuilder(queryStart);
//
//        while(myCursor.moveToNext()) {
//            cityNameFromDb = myCursor.getString(0);
//            strB.append(", '" + cityNameFromDb + "'");
//        }
//
//        myCursor.close();
//        myDB.close();

        String queryStart = "select item.condition, location.city, item.description from weather.forecast where woeid in " +
                "(select woeid from geo.places(1) where text in " +
                "('saint petersburg', 'ярославль', 'barnaul', 'moscow', 'murmansk', 'sochi', 'yekaterinburg', 'petropavlovsk-kamchatskiy'";

        String queryEnd = "))";

        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder(BASE_URL);
            stringBuilder.append(URLEncoder.encode(queryStart, "UTF-8"));
            stringBuilder.append(", \'");
            stringBuilder.append(cityName);
            stringBuilder.append("\'");
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

    private void insertCity() {

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_CITY_NAME, "Toto");

        // Insert a new row for City into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the cities database table.
        // Receive the new content URI that will allow us to access City data in the future.
        Uri newUri = getContentResolver().insert(WeatherEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_new_city:
                //insertCity();
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_delete_all_cities:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
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

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete all pets.
                deleteAllPets();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(WeatherEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }
}
