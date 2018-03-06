package com.alextsy.weatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by os_mac on 03.03.18.
 */

public class Forecast {

    public String getDate() {
        return date;
    }

    public String getDay() {
        return day;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getText() {
        return text;
    }

    public int getCode() {
        return code;
    }

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("high")
    @Expose
    private String high;
    @SerializedName("low")
    @Expose
    private String low;
    @SerializedName("text")
    @Expose
    private String text;

}
