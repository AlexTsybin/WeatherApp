package com.alextsy.weatherapp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by os_mac on 13.02.18.
 */

public class ServiceGenerator {

    private static final String BASE_URL = "https://query.yahooapis.com/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder sBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static WeatherService createService() {

        // Подключаем логирование
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(logging);

        Retrofit retrofit = sBuilder
                .client(httpClient.build())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);

        return weatherService;
    }

}
