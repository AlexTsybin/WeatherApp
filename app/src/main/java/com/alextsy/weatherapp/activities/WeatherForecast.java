package com.alextsy.weatherapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.alextsy.weatherapp.ForecastAdapter;
import com.alextsy.weatherapp.R;
import com.alextsy.weatherapp.ServiceGenerator;
import com.alextsy.weatherapp.WeatherService;
import com.alextsy.weatherapp.model.Forecast;
import com.alextsy.weatherapp.model.WeatherModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by os_mac on 20.02.18.
 */

public class WeatherForecast extends AppCompatActivity {

    private static final String LOG_TAG = WeatherForecast.class.getSimpleName();

    private String forecastCity;

    private ListView forecastListView;

    private ArrayList<Forecast> forecastList;
    private ForecastAdapter fcAdapter;

    private TextView fcCity;
    private TextView fcCountry;
    private TextView fcTemp;
    private TextView fcDescr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            forecastCity = extras.getString("cityForecast");
        }
        setContentView(R.layout.forecast_activity);

        /**
         * Progress Dialog for User Interaction
         */
        final ProgressDialog dialog;
        dialog = new ProgressDialog(WeatherForecast.this);
        dialog.setTitle(getString(R.string.string_getting_json_title));
        dialog.setMessage(getString(R.string.string_getting_json_message));
        dialog.show();

        forecastList = new ArrayList<>();

        fcCity = findViewById(R.id.forecast_city_name);
        fcCountry = findViewById(R.id.forecast_country);
        fcTemp = findViewById(R.id.forecast_temp);
        fcDescr = findViewById(R.id.forecast_descr);

        // Find a reference to the {@link ListView} in the layout
        forecastListView = findViewById(R.id.forecast_list);

        String q = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text = \"" + forecastCity + "\") and u = \"c\"";
        String format = "json";

        WeatherService api = ServiceGenerator.createService();

        api.getMyJSON(q, format).enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                // Dismiss dialog
                dialog.dismiss();

                fcCity.setText(response.body().getQuery().getResults().getChannel().getLocation().getCity());
                fcCountry.setText(response.body().getQuery().getResults().getChannel().getLocation().getCountry());
                fcTemp.setText(response.body().getQuery().getResults().getChannel().getItem().getCondition().getTemp() + "\u00b0");
                fcDescr.setText(response.body().getQuery().getResults().getChannel().getItem().getCondition().getText());

                forecastList = response.body().getQuery().getResults().getChannel().getItem().getForecast();
                fcAdapter = new ForecastAdapter(WeatherForecast.this, forecastList);
                forecastListView.setAdapter(fcAdapter);
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {
                Log.i(LOG_TAG, "An error occurred during networking");
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
