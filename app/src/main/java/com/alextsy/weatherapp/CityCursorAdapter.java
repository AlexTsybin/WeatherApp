package com.alextsy.weatherapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.alextsy.weatherapp.data.WeatherContract.WeatherEntry;

/**
 * Created by os_mac on 21.01.18.
 */

public class CityCursorAdapter extends CursorAdapter {

    public CityCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.city_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        // Extract properties from cursor
        String cityName = cursor.getString(cursor.getColumnIndex(WeatherEntry.COLUMN_CITY_NAME));
        // Populate fields with extracted properties
        nameTextView.setText(cityName);
    }
}
