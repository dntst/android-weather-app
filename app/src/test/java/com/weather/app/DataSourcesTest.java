package com.weather.app;

import com.weather.app.datasources.AbstractDataSource;
import com.weather.app.datasources.DarkSkyDataSource;
import com.weather.app.datasources.OpenWeatherMapDataSource;
import com.weather.app.entities.Weather;
import com.weather.app.utils.location.Location;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class DataSourcesTest {
    @Test
    public void getDataFromOpenWeatherMapDataSource_dataLoaded() {
        OpenWeatherMapDataSource source = new OpenWeatherMapDataSource();
        source.setLocation(new Location(56.6, 84.85, 0, "Moscow"));
        source.setCallback(new AbstractDataSource.Callback() {
            @Override
            public void onDataLoaded(Weather w) {
                assertTrue(true);
            }

            @Override
            public void onDataLoadError() {
                assertTrue(false);
            }
        });
        source.getData();
    }

    @Test
    public void getDataFromDarkSkyDataSource_dataLoaded() {
        DarkSkyDataSource source = new DarkSkyDataSource();
        source.setLocation(new Location(56.6, 84.85, 0, "Moscow"));
        source.setCallback(new AbstractDataSource.Callback() {
            @Override
            public void onDataLoaded(Weather w) {
                assertTrue(true);
            }

            @Override
            public void onDataLoadError() {
                assertTrue(false);
            }
        });
        source.getData();
    }
}
