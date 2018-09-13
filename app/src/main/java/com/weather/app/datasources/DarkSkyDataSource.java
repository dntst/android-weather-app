package com.weather.app.datasources;

import android.net.Uri;

import com.weather.app.BuildConfig;
import com.weather.app.entities.Weather;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class DarkSkyDataSource extends AbstractDataSource {

    private String buildUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.darksky.net")
                .appendPath("forecast")
                .appendPath(BuildConfig.DARK_SKY_API_KEY)
                .appendPath(String.valueOf(location.getLat()) + "," + String.valueOf(location.getLng()))
                .appendQueryParameter("units", "si")
                .appendQueryParameter("lang", Locale.getDefault().getLanguage());

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
            JSONObject currentWeatherJson = rootJsonObject.getJSONObject("currently");

            Weather weather = new Weather();
            weather.setTemperature(currentWeatherJson.getDouble("temperature"));
            weather.setHumidity(currentWeatherJson.getInt("humidity") * 100);
            weather.setPressure(currentWeatherJson.getInt("pressure"));
            weather.setDescription(currentWeatherJson.getString("summary"));

            callback.onDataLoaded(weather);
        } catch (JSONException e) {
            callback.onDataLoadError();
            e.printStackTrace();
        }
    }

}
