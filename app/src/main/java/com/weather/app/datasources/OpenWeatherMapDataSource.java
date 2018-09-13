package com.weather.app.datasources;

import android.net.Uri;

import com.weather.app.BuildConfig;
import com.weather.app.entities.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class OpenWeatherMapDataSource extends AbstractDataSource {

    private String buildUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("weather")
                .appendQueryParameter("lat", String.valueOf(location.getLat()))
                .appendQueryParameter("lon", String.valueOf(location.getLng()))
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("lang", Locale.getDefault().getLanguage())
                .appendQueryParameter("appid", BuildConfig.OPEN_WEATHER_API_KEY);

        return builder.build().toString();
    }

    @Override
    public void getData() {
        loadFromApi();
    }

    private void loadFromApi() {
        LoadDataFromApiTask loadTask = new LoadDataFromApiTask(buildUrl());
        loadTask.setCallback(this::onDataLoadedFromApi);
        loadTask.execute();
    }

    private void onDataLoadedFromApi(String response) {
        try {
            JSONObject rootJsonObject = new JSONObject(response);
            JSONObject currentWeatherJson = rootJsonObject.getJSONObject("main");

            Weather weather = new Weather();
            weather.setTemperature(currentWeatherJson.getDouble("temp"));
            weather.setHumidity(currentWeatherJson.getInt("humidity"));
            weather.setPressure(currentWeatherJson.getInt("pressure"));
            weather.setDescription(rootJsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));

            callback.onDataLoaded(weather);
        } catch (JSONException e) {
            callback.onDataLoadError();
            e.printStackTrace();
        }
    }

}
