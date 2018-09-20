package com.weather.app.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.weather.app.databinding.ActivityMainBinding;
import com.weather.app.databases.SQLiteDatabase;
import com.weather.app.datasources.OpenWeatherMapDataSource;
import com.weather.app.entities.Weather;
import com.weather.app.R;
import com.weather.app.presenters.MainPresenter;
import com.weather.app.repositories.EntityManager;
import com.weather.app.utils.location.FusedLocation;
import com.weather.app.utils.location.Location;
import com.weather.app.views.MainContract;


public class MainActivity extends AppCompatActivity implements MainContract.View {

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    private ActivityMainBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setWeather(new Weather());

        refreshLayout = findViewById(R.id.swipe_refresh_layout);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                showRefresh();
                presenter.onNewLocationSelected(new Location(place.getLatLng().latitude, place.getLatLng().longitude, 0, place.getName().toString()));
            }

            @Override
            public void onError(Status status) {
                hideRefresh();
            }
        });

        SQLiteDatabase db = SQLiteDatabase.getInstance(getApplicationContext());
        EntityManager entityManager = new EntityManager(db);

        FusedLocation fusedLocation = new FusedLocation(this);
        OpenWeatherMapDataSource dataSource = new OpenWeatherMapDataSource();

        presenter = new MainPresenter(entityManager, dataSource, fusedLocation);
        presenter.attachView(this);
        refreshLayout.setOnRefreshListener(presenter::onRefreshWeather);

        presenter.onViewCreated();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showWeather(Weather weather) {
        if(weather != null) {
            binding.setWeather(weather);
        }
    }

    @Override
    public void showRefresh() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefresh() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestFineLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onLocationPermissionGranted();
                } else {
                    hideRefresh();
                }
            }
        }
    }

}
