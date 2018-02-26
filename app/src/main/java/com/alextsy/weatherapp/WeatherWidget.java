package com.alextsy.weatherapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

import com.alextsy.weatherapp.data.WeatherContract.WeatherEntry;
import com.alextsy.weatherapp.data.WeatherDbHelper;
import com.alextsy.weatherapp.model.WeatherModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by os_mac on 26.01.18.
 */

public class WeatherWidget extends AppWidgetProvider {

    private static final String LOG_TAG = WeatherWidget.class.getSimpleName();

    private static WeatherService weatherService;

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

//        SQLiteDatabase db = new WeatherDbHelper(context).getReadableDatabase();
//        Cursor cursor = db.query(WeatherEntry.TABLE_NAME, new String[] {WeatherEntry.COLUMN_CITY_NAME}, null, null, null, null, null);
//        cursor.moveToFirst();
//        String city = cursor.getString(cursor.getColumnIndexOrThrow(WeatherEntry.COLUMN_CITY_NAME));

        SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE);
        String pref_city = sharedPref.getString(String.valueOf(R.string.saved_city), "");

        String q = "select item.condition, location.city from weather.forecast where woeid in (select woeid from geo.places(1) where text = \"" + pref_city + "\") and u = \"c\"";
        String format = "json";

        weatherService = ServiceGenerator.createService();

//        Intent intent = new Intent(context, WeatherActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//        // Get the layout for the App Widget and attach an on-click listener to the button
//        final RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget);
//        view.setOnClickPendingIntent(R.xml.widget_provider_info, pendingIntent);

        weatherService.getData(q, format).enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                views.setTextViewText(R.id.widget_temperature, response.body().getQuery().getResults().getChannel().getItem().getCondition().getTemp() + "\u00b0");
                views.setTextViewText(R.id.widget_city_name, response.body().getQuery().getResults().getChannel().getLocation().getCity());
                views.setTextViewText(R.id.widget_description, response.body().getQuery().getResults().getChannel().getItem().getCondition().getText());

                appWidgetManager.updateAppWidget(appWidgetIds, views);
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.i(LOG_TAG, "An error occurred during networking");
            }
        });

    }
}
