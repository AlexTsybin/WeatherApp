package com.alextsy.weatherapp;

import com.alextsy.weatherapp.model.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("/v1/public/yql")
    Call<WeatherModel> getData(@Query("q") String question, @Query("format") String format);

}
