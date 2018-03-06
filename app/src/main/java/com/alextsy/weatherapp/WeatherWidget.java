package com.alextsy.weatherapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.alextsy.weatherapp.activities.WeatherActivity;
import com.alextsy.weatherapp.model.Forecast;
import com.alextsy.weatherapp.model.WeatherModel;
import com.alextsy.weatherapp.utils.LocationPref;

import java.util.ArrayList;

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

        LocationPref sharedPref = new LocationPref(context);

        String pref_city = sharedPref.getData();

        String q = "select item.condition, location.city from weather.forecast where woeid in (select woeid from geo.places(1) where text = \"" + pref_city + "\") and u = \"c\"";
        String format = "json";

        weatherService = ServiceGenerator.createService();

        /*
         * Start activity by clicking on widget
         */
        Intent intent = new Intent(context, WeatherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, views);

        weatherService.getMyJSON(q, format).enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

                if (response.body().getQuery() == null) {
                    views.setTextViewText(R.id.widget_temperature, "n/a" + "\u00b0");
                    views.setTextViewText(R.id.widget_city_name, "n/a");
                    views.setTextViewText(R.id.widget_description, "n/a");
                } else {
                    views.setTextViewText(R.id.widget_temperature, response.body().getQuery().getResults().getChannel().getItem().getCondition().getTemp() + "\u00b0");
                    views.setTextViewText(R.id.widget_city_name, response.body().getQuery().getResults().getChannel().getLocation().getCity());
                    views.setTextViewText(R.id.widget_description, response.body().getQuery().getResults().getChannel().getItem().getCondition().getText());
                }

                appWidgetManager.updateAppWidget(appWidgetIds, views);
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.i(LOG_TAG, "An error occurred during networking");
            }
        });

    }
}
