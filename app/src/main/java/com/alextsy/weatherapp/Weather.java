package com.alextsy.weatherapp;

import android.support.annotation.NonNull;

/**
 * Created by os_mac on 11.12.17.
 */

public class Weather {
    private int mTemp;
    private String mDescr;
    private String mDate;
    private String mCity;
    private int mCode;

    public Weather(int Temperature, String Description, String Date, String City, int Code) {
        this.mTemp = Temperature;
        this.mDescr = Description;
        this.mDate = Date;
        this.mCity = City;
        this.mCode = Code;
    }

    public int getTemp() {
        return mTemp;
    }

    public String getDescr() {
        return mDescr;
    }

    public String getDate() {
        return mDate;
    }

    public String getCity() {
        return mCity;
    }

    public int getCode() {
        return mCode;
    }
}
