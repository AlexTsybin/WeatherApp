package com.alextsy.weatherapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by os_mac on 11.12.17.
 */

public class WeatherAdapter extends ArrayAdapter<Weather> {

    /**
     * Constructs a new {@link WeatherAdapter}.
     *
     * @param context of the app
     * @param weathers is the list of weathers, which is the data source of the adapter
     */
    public WeatherAdapter(Context context, List<Weather> weathers) {
        super(context, 0, weathers);
    }

    /**
     * Returns a list item view that displays information about the weather at the given position
     * in the list of weathers.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the weather at the given position in the list of weathers
        Weather currentWeather = getItem(position);

        // Find the TextView with view ID temperature
        TextView tempView = (TextView) listItemView.findViewById(R.id.temperature);
        // Display the temperature of the current weather in that TextView

        int tempC = (int) ((currentWeather.getTemp() - 32) / 1.8);

        String temp = String.valueOf(tempC);

        if (tempC > 0) {
            temp = "+ " + tempC;
        }

        tempView.setText(temp);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) tempView.getBackground();
        // Get the appropriate background color based on the current weather magnitude
        int magnitudeColor = getTempColor(tempC);
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        String city = currentWeather.getCity();
        TextView cityView = listItemView.findViewById(R.id.city_name);
        cityView.setText(city);

        String descr = currentWeather.getDescr();
        // Find the TextView with view ID description
        TextView descrView = (TextView) listItemView.findViewById(R.id.description);
        // Display the description of the current weather in that TextView
        descrView.setText(descr);

        int code = currentWeather.getCode();
        ImageView miniIcon = listItemView.findViewById(R.id.mini_icon);
        miniIcon.setImageResource(getIcon(code));

        String originalDate = currentWeather.getDate();

        String date = originalDate.substring(5, 16);
        String time = originalDate.substring(17, 25);

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        dateView.setText(date);

        // Find the TextView with view ID time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        timeView.setText(time);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the color for the temp circle based on the intensity of the weather.
     *
     * @param temp of the weather
     */
    private int getTempColor(int temp) {
        int tempColorResourceId;

        if (temp >= 25) {
            tempColorResourceId = R.color.weather1;
        } else if (temp >= 15) {
            tempColorResourceId = R.color.weather2;
        } else if (temp >= 5) {
            tempColorResourceId = R.color.weather3;
        } else if (temp >= -5) {
            tempColorResourceId = R.color.weather4;
        } else if (temp >= -15) {
            tempColorResourceId = R.color.weather5;
        } else if (temp >= -25) {
            tempColorResourceId = R.color.weather6;
        } else {
            tempColorResourceId = R.color.weather7;
        }

        return ContextCompat.getColor(getContext(), tempColorResourceId);
    }


    /**
     * Return the icon resource id based on weather code
     * @param code
     * @return icon resource id
     */
    private int getIcon(int code) {
        int iconResourceId = R.drawable.term;

        switch (code) {
            case 0:
            case 2:
                iconResourceId = R.drawable.tornado;
                break;
            case 3:
                iconResourceId = R.drawable.lightning;
                break;
            case 4:
            case 37:
            case 38:
            case 39:
                iconResourceId = R.drawable.storm;
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 14:
            case 18:
                iconResourceId = R.drawable.sleet;
                break;
            case 9:
                iconResourceId = R.drawable.light_rain;
                break;
            case 1:
            case 11:
            case 12:
            case 40:
            case 45:
            case 47:
                iconResourceId = R.drawable.showers;
                break;
            case 13:
            case 41:
            case 42:
            case 43:
            case 46:
                iconResourceId = R.drawable.light_snow;
                break;
            case 15:
                iconResourceId = R.drawable.blowing_snow;
                break;
            case 16:
                iconResourceId = R.drawable.snow;
                break;
            case 17:
            case 35:
                iconResourceId = R.drawable.hail;
                break;
            case 19:
                iconResourceId = R.drawable.dust;
                break;
            case 20:
            case 21:
            case 22:
                iconResourceId = R.drawable.haze;
                break;
            case 23:
            case 24:
                iconResourceId = R.drawable.wind;
                break;
            case 25:
                iconResourceId = R.drawable.cold;
                break;
            case 26:
            case 27:
            case 28:
                iconResourceId = R.drawable.cloudy;
                break;
            case 29:
            case 30:
            case 44:
                iconResourceId = R.drawable.partly_cloud;
                break;
            case 31:
            case 33:
                iconResourceId = R.drawable.clear_night;
                break;
            case 32:
            case 34:
                iconResourceId = R.drawable.sun;
                break;
            case 36:
                iconResourceId = R.drawable.dry;
                break;
        }
        return iconResourceId;
    }
}
