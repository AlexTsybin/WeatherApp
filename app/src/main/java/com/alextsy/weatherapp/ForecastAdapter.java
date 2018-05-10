package com.alextsy.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alextsy.weatherapp.model.Forecast;
import com.alextsy.weatherapp.WeatherAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by os_mac on 03.03.18.
 */

public class ForecastAdapter extends ArrayAdapter<Forecast> {

    private List<Forecast> forecastList;
    private Context context;
    private LayoutInflater mInflater;

    public ForecastAdapter(Context context, List<Forecast> forecasts) {
        super(context, 0, forecasts);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        forecastList = forecasts;
    }

    @Override
    public Forecast getItem(int position) {
        return forecastList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;

        if (convertView == null) {
            View view = mInflater.inflate(R.layout.forecast_list_item, parent, false);
            vh = ViewHolder.create((LinearLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // Find the weather forecast at the given position in the list of forecast
        Forecast item = getItem(position);

        vh.fcWd.setText(item.getDay());
        vh.fcWdDate.setText(item.getDate());
//        vh.fcWdDescr.setText(item.getText());
        vh.fcWdHigh.setText(item.getHigh() + "\u00b0");
        vh.fcWdLow.setText(item.getLow() + "\u00b0");
        vh.fcWdIcon.setImageResource(WeatherAdapter.getIcon(item.getCode()));

        return vh.rootView;
    }

    private static class ViewHolder {
        private final LinearLayout rootView;
        private final TextView fcWd;
        private final TextView fcWdDate;
//        private final TextView fcWdDescr;
        private final TextView fcWdHigh;
        private final TextView fcWdLow;
        private final ImageView fcWdIcon;

        private ViewHolder(LinearLayout rootView, TextView fcWd, TextView fcWdDate, TextView fcWdHigh, TextView fcWdLow, ImageView fcWdIcon) {
            this.rootView = rootView;
            this.fcWd = fcWd;
            this.fcWdDate = fcWdDate;
//            this.fcWdDescr = fcWdDescr;
            this.fcWdHigh = fcWdHigh;
            this.fcWdLow = fcWdLow;
            this.fcWdIcon = fcWdIcon;
        }

        private static ViewHolder create(LinearLayout rootView) {
            TextView fcWd = rootView.findViewById(R.id.forecast_wd);
            TextView fcWdDate = rootView.findViewById(R.id.forecast_wd_date);
//            TextView fcWdDescr = rootView.findViewById(R.id.forecast_wd_descr);
            TextView fcWdHigh = rootView.findViewById(R.id.forecast_wd_high);
            TextView fcWdLow = rootView.findViewById(R.id.forecast_wd_low);
            ImageView fcWdIcon = rootView.findViewById(R.id.forecast_wd_icon);

            return new ViewHolder(rootView, fcWd, fcWdDate, fcWdHigh, fcWdLow, fcWdIcon);
        }
    }

}
