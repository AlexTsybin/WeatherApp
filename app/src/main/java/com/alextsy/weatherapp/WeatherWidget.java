package com.alextsy.weatherapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import com.alextsy.weatherapp.data.WeatherContract.WeatherEntry;
import com.alextsy.weatherapp.data.WeatherDbHelper;

/**
 * Created by os_mac on 26.01.18.
 */

public class WeatherWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SQLiteDatabase db = new WeatherDbHelper(context).getReadableDatabase();
        Cursor cursor = db.query(WeatherEntry.TABLE_NAME, new String[] {WeatherEntry.COLUMN_CITY_NAME}, null, null, null, null, null);
        cursor.moveToFirst();
        String city = cursor.getString(
                cursor.getColumnIndexOrThrow(WeatherEntry.COLUMN_CITY_NAME));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.widget_city_name, city);
        views.setTextViewText(R.id.widget_description, "Sunny");

        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }
}
