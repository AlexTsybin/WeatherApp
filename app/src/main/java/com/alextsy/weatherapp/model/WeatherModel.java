package com.alextsy.weatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by os_mac on 13.02.18.
 */

public class WeatherModel {

    public Query getQuery() {
        return query;
    }

    @SerializedName("query")
    @Expose
    public Query query;

    public class Query {

        public Results getResults() {
            return results;
        }

        @SerializedName("results")
        @Expose
        private Results results;

    }

    public class Results {

        public Channel getChannel() {
            return channel;
        }

        @SerializedName("channel")
        @Expose
        private Channel channel;

    }

    public class Channel {

        public Location getLocation() {
            return location;
        }

        public Item getItem() {
            return item;
        }

        @SerializedName("location")
        @Expose
        private Location location;
        @SerializedName("item")
        @Expose
        private Item item;

    }

    public class Location {

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        @SerializedName("country")
        @Expose
        private String country;

        @SerializedName("city")
        @Expose
        private String city;

    }

    public class Astronomy {

        public String getSunrise() {
            return sunrise;
        }

        public String getSunset() {
            return sunset;
        }

        @SerializedName("sunrise")
        @Expose
        private String sunrise;
        @SerializedName("sunset")
        @Expose
        private String sunset;

    }

    public class Item {

        @SerializedName("condition")
        @Expose
        private Condition condition;

        @SerializedName("forecast")
        @Expose
        private ArrayList<Forecast> forecast = new ArrayList<>();

        public Condition getCondition() {
            return condition;
        }

        public ArrayList<Forecast> getForecast() {
            return forecast;
        }

    }

    public class Condition {

        public String getTemp() {
            return temp;
        }

        public String getText() {
            return text;
        }

        @SerializedName("temp")
        @Expose
        private String temp;
        @SerializedName("text")
        @Expose
        private String text;

    }

}
