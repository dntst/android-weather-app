package com.weather.app.presenters;

import com.weather.app.R;
import com.weather.app.datasources.AbstractDataSource;
import com.weather.app.entities.City;
import com.weather.app.entities.Weather;
import com.weather.app.repositories.EntityManager;
import com.weather.app.utils.location.AbstractLocation;
import com.weather.app.utils.location.Location;
import com.weather.app.views.MainContract;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private EntityManager entityManager;
    private AbstractLocation locationUtil;
    private AbstractDataSource dataSource;

    public MainPresenter(EntityManager entityManager, AbstractDataSource dataSource, AbstractLocation locationUtil) {
        this.entityManager = entityManager;
        this.locationUtil = locationUtil;
        this.dataSource = dataSource;
    }

    @Override
    public void attachView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void onViewCreated() {
        showLatestSavedWeather();
    }

    @Override
    public void onRefreshWeather() {
        updateWeather();
    }

    @Override
    public void onNewLocationSelected(Location location) {
        getWeatherFromApi(location);
    }

    @Override
    public void onLocationPermissionGranted() {
        updateWeather();
    }

    private void showLatestSavedWeather() {
        Weather weather = (Weather) entityManager.getRepository(Weather.class).findOneBy("", new String[] {}, new String[] { "id", "desc" });
        if(weather != null) {
            if(view != null) {
                view.showWeather(weather);
            }
        } else {
            if(view != null) {
                view.showRefresh();
            }
            updateWeather();
        }
    }

    private void updateWeather() {
        locationUtil.setCallback(new AbstractLocation.Callback() {
            @Override
            public void onLocationFound(Location location) {
                if(location.getAccuracy() <= 100) {
                    getWeatherFromApi(location);
                } else {
                    if(view != null) {
                        view.hideRefresh();
                        view.showToast(R.string.weather_update_failed);
                    }
                }
            }

            @Override
            public void onPermissionRequired() {
                if(view != null) {
                    view.requestFineLocationPermission();
                }
            }

            @Override
            public void onError() {
                if(view != null) {
                    view.hideRefresh();
                    view.showToast(R.string.weather_update_failed);
                }
            }
        });
        locationUtil.getCurrentLocation();
    }

    private void getWeatherFromApi(Location location) {
        dataSource.setLocation(location);
        dataSource.setCallback(new AbstractDataSource.Callback() {
            @Override
            public void onDataLoaded(Weather w) {
                City c = (City) entityManager.getRepository(City.class).findOneBy("name = ?", new String[] { location.getName() }, new String[] {});

                if(c == null) {
                    City city = new City();
                    city.setName(location.getName());
                    city = (City) entityManager.save(city);
                    w.setCity(city);
                } else {
                    w.setCity(c);
                }

                entityManager.save(w);

                if(view != null) {
                    view.hideRefresh();
                    view.showWeather(w);
                    view.showToast(R.string.weather_updated);
                }
            }

            @Override
            public void onDataLoadError() {
                if(view != null) {
                    view.hideRefresh();
                    view.showToast(R.string.weather_update_failed);
                }
            }
        });
        dataSource.getData();
    }
}
