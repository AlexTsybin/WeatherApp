package com.alextsy.weatherapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by os_mac on 11.12.17.
 */

public final class QueryUtils {
    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Yahoo dataset and return a list of {@link Weather} objects.
     */
    public static List<Weather> fetchWeatherData(String requestUrl) {

        Log.i(LOG_TAG, "TEST: fetchWeatherData() called...");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Weather}s
        List<Weather> weathers = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Weather}s
        return weathers;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Weather} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Weather> extractFeatureFromJson(String weatherJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(weatherJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding weathers to
        List<Weather> weathers = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(weatherJSON);

            JSONObject queryObject = baseJsonResponse.getJSONObject("query");
            JSONObject resultsObject = queryObject.getJSONObject("results");

            Object channel;
            JSONObject channelObject;
            JSONArray channelArray;

            channel = resultsObject.get("channel");

            // Todo: Выбор между объектом и массивом надо переделать

            if (channel instanceof JSONObject) {
                channelObject = resultsObject.getJSONObject("channel");

                JSONObject itemObject = channelObject.getJSONObject("item");
                JSONObject conditionObject = itemObject.getJSONObject("condition");

                int temperatureF = conditionObject.getInt("temp");
                String descr = conditionObject.getString("text");
                String date = conditionObject.getString("date");
                int code = conditionObject.getInt("code");

                JSONObject locationObject = channelObject.getJSONObject("location");

                String name = locationObject.getString("city");

                // Create a new {@link Weather} object with the temperatureF, descr, date,
                // and name from the JSON response.
                Weather weather = new Weather(temperatureF, descr, date, name, code);

                // Add the new {@link Weather} to the list of weathers.
                weathers.add(weather);
            } else {
                channelArray = resultsObject.getJSONArray("channel");
                for (int i = 0; i < channelArray.length(); i++) {

                    JSONObject currentWeather = channelArray.getJSONObject(i);
                    JSONObject itemObject = currentWeather.getJSONObject("item");
                    JSONObject conditionObject = itemObject.getJSONObject("condition");

                    int temperatureF = conditionObject.getInt("temp");
                    String descr = conditionObject.getString("text");
                    String date = conditionObject.getString("date");
                    int code = conditionObject.getInt("code");

                    JSONObject locationObject = currentWeather.getJSONObject("location");

                    String name = locationObject.getString("city");

                    // Create a new {@link Weather} object with the temperatureF, descr, date,
                    // and name from the JSON response.
                    Weather weather = new Weather(temperatureF, descr, date, name, code);

                    // Add the new {@link Weather} to the list of weathers.
                    weathers.add(weather);

                }
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the weather JSON results", e);
        }

        // Return the list of weathers
        return weathers;
    }
}
