package com.weather.app.views;

import com.weather.app.entities.Weather;
import com.weather.app.utils.location.Location;

public interface MainContract {
    interface View {
        void showWeather(Weather w);

        void showRefresh();

        void hideRefresh();

        void requestFineLocationPermission();

        void showToast(int resId);
    }

    interface Presenter {
        void detachView();

        void attachView(View mainView);

        void onViewCreated();

        void onRefreshWeather();

        void onNewLocationSelected(Location location);

        void onLocationPermissionGranted();
    }
}
