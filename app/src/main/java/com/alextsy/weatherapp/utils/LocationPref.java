package com.alextsy.weatherapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.alextsy.weatherapp.R;

/**
 * Created by os_mac on 27.02.18.
 */

public class LocationPref {

    private static final String PREF = "pref";
    private static final String S_CITY = "s_city";

    private SharedPreferences sharedPref;

    public LocationPref(Context context) {
        sharedPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void setData(String city){
        Editor editor = sharedPref.edit();
        editor.putString(S_CITY, city);
        editor.apply();
    }

    public String getData() {
        String city = sharedPref.getString(S_CITY, "");
        return city;
    }

}
