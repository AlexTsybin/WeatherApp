package com.alextsy.weatherapp.activities;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alextsy.weatherapp.R;
import com.alextsy.weatherapp.utils.LocationPref;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.Manifest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by os_mac on 25.02.18.
 */

public class LocationActivity extends AppCompatActivity {

    private TextView curr_lat;
    private TextView curr_long;
    private TextView curr_city;
    private Button get_curr_city;
    private FusedLocationProviderClient mFusedLocationClient;

    LocationPref sharedPref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        sharedPref = new LocationPref(this);

        curr_lat = findViewById(R.id.current_lat);
        curr_long = findViewById(R.id.current_long);
        curr_city = findViewById(R.id.current_geo_city);
        get_curr_city = findViewById(R.id.get_current_city);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        get_curr_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });

    }

    private void displayLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0);
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            double latD = location.getLatitude();
                            double lngD = location.getLongitude();
                            curr_lat.setText(String.valueOf(latD));
                            curr_long.setText(String.valueOf(lngD));

                            String city = getCityFromLocation(latD, lngD);
                            String lat_lng = "(" + String.valueOf(latD) + "," + String.valueOf(lngD) + ")";

                            curr_city.setText(city);

                            sharedPref.setData(lat_lng);
                        }
                    }
                });

    }

    @Nullable
    private String getCityFromLocation(double latitude, double longtitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longtitude, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                String city = returnedAddress.getLocality();
                return city;
            } else {
                return null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

}
