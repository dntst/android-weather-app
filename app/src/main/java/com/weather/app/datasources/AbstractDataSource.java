package com.weather.app.datasources;

import android.os.AsyncTask;

import com.weather.app.entities.Weather;
import com.weather.app.utils.location.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public abstract class AbstractDataSource {

    Callback callback;
    Location location;

    public interface Callback {
        void onDataLoaded(Weather weather);
        void onDataLoadError();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    static class LoadDataFromApiTask extends AsyncTask<Void, Void, String> {

        private String apiUrl;
        private CallbackInterface callback;

        interface CallbackInterface {
            void onDataLoadedFromApi(String response);
        }

        void setCallback(CallbackInterface callback) {
            this.callback = callback;
        }

        LoadDataFromApiTask(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        @Override
        protected String doInBackground(Void... params) {

            StringBuilder response = new StringBuilder();
            HttpsURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(this.apiUrl);

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            callback.onDataLoadedFromApi(response);
        }

    }

    public abstract void getData();

}
