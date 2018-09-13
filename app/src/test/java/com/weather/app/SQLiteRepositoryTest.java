package com.weather.app;

import com.weather.app.databases.SQLiteDatabase;
import com.weather.app.entities.City;
import com.weather.app.entities.Entity;
import com.weather.app.entities.Weather;
import com.weather.app.repositories.EntityManager;
import com.weather.app.repositories.Repository;
import com.weather.app.repositories.RepositoryManager;
import com.weather.app.repositories.sqlite.SQLiteCityRepository;
import com.weather.app.repositories.sqlite.SQLiteWeatherRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
public class SQLiteRepositoryTest {

    private SQLiteDatabase db;
    private EntityManager entityManager;

    @Before
    public void setUp() {
        db = SQLiteDatabase.getInstance(RuntimeEnvironment.application);
        entityManager = new EntityManager(db);

        db.delete(Weather.getDatabaseTableName(), null, null);
        db.delete(City.getDatabaseTableName(), null, null);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void createWeatherAndCityRowsAndFindItById_rowWithPrimaryKeyEquals1CreatedAndFound() {
        City city = new City();
        city.setName("Moscow");
        city = (City) entityManager.save(city);

        Weather weather = new Weather();
        weather.setTemperature(12.3);
        weather.setHumidity(76);
        weather.setPressure(750);
        weather.setDescription("Clouds");
        weather.setCity(city);
        entityManager.save(weather);

        Weather w = (Weather) entityManager.getRepository(Weather.class).find(1);

        assertEquals(1, w.getId());
        assertEquals(12.3, w.getTemperature(), 0);
        assertEquals(76, w.getHumidity());
        assertEquals(750, w.getPressure());
        assertEquals("Clouds", w.getDescription());
        assertEquals(1, w.getCity().getId());
        assertEquals("Moscow", w.getCity().getName());
    }

    @Test
    public void updateWeatherRowAndFindItById_rowWithPrimaryKeyEquals1Updated() {
        City city = new City();
        city.setName("Moscow");
        city = (City) entityManager.save(city);

        Weather weather = new Weather();
        weather.setTemperature(12.3);
        weather.setHumidity(76);
        weather.setPressure(750);
        weather.setDescription("Rain");
        weather.setCity(city);
        entityManager.save(weather);

        city = new City();
        city.setName("Toronto");
        city = (City) entityManager.save(city);

        weather.setTemperature(10.1);
        weather.setHumidity(100);
        weather.setPressure(780);
        weather.setDescription("Clouds");
        weather.setCity(city);
        entityManager.save(weather);

        Weather w = (Weather) entityManager.getRepository(Weather.class).find(1);

        assertEquals(1, w.getId());
        assertEquals(10.1, w.getTemperature(), 0);
        assertEquals(100, w.getHumidity());
        assertEquals(780, w.getPressure());
        assertEquals("Clouds", w.getDescription());
        assertEquals(2, w.getCity().getId());
        assertEquals("Toronto", w.getCity().getName());
    }

    @Test
    public void updateCityRowAndFindItById_rowWithPrimaryKeyEquals1Updated() {
        City city = new City();
        city.setName("Moscow");
        city = (City) entityManager.save(city);

        city.setName("Toronto");
        entityManager.save(city);

        City c = (City) entityManager.getRepository(City.class).find(1);

        assertEquals(1, c.getId());
        assertEquals("Toronto", c.getName());
    }


    @Test
    public void deleteWeatherRow_rowWithPrimaryKeyEquals1Deleted() {
        City city = new City();
        city.setName("Moscow");
        city = (City) entityManager.save(city);

        Weather weather = new Weather();
        weather.setTemperature(12.3);
        weather.setHumidity(76);
        weather.setPressure(750);
        weather.setDescription("Clouds");
        weather.setCity(city);
        entityManager.save(weather);

        entityManager.delete(weather);

        Weather w = (Weather) entityManager.getRepository(Weather.class).find(1);

        assertNull(w);
    }

    @Test
    public void deleteCityRow_rowWithPrimaryKeyEquals1Deleted() {
        City city = new City();
        city.setName("Moscow");
        city = (City) entityManager.save(city);

        entityManager.delete(city);

        City c = (City) entityManager.getRepository(City.class).find(1);

        assertNull(c);
    }

    @Test
    public void findAllAndFindByWeatherAndCityRows_rowsFound() {
        City city = new City();
        city.setName("Moscow");
        city = (City) entityManager.save(city);

        Weather weather = new Weather();
        weather.setTemperature(12.3);
        weather.setHumidity(76);
        weather.setPressure(750);
        weather.setDescription("Rain");
        weather.setCity(city);
        entityManager.save(weather);

        city = new City();
        city.setName("Toronto");
        city = (City) entityManager.save(city);

        weather = new Weather();
        weather.setTemperature(10.1);
        weather.setHumidity(100);
        weather.setPressure(780);
        weather.setDescription("Clouds");
        weather.setCity(city);
        entityManager.save(weather);

        SQLiteWeatherRepository weatherRepository = (SQLiteWeatherRepository) entityManager.getRepository(Weather.class);
        ArrayList<Weather> w = weatherRepository.findAll();
        ArrayList<Weather> w2 = weatherRepository.findBy("", new String[] {}, new String[] { "id", "desc" }, new String[] {});
        ArrayList<Weather> w3 = weatherRepository.findBy("", new String[] {}, new String[] { "id", "desc" }, new String[] { "0", "1" });

        assertEquals(2, w.size());
        assertEquals(2, w2.size());
        assertEquals(10.1, w2.get(0).getTemperature(), 0);
        assertEquals(12.3, w2.get(1).getTemperature(), 0);
        assertEquals(1, w3.size());
        assertEquals(10.1, w3.get(0).getTemperature(), 0);

        SQLiteCityRepository cityRepository = (SQLiteCityRepository) entityManager.getRepository(City.class);
        ArrayList<City> c = cityRepository.findAll();
        ArrayList<City> c2 = cityRepository.findBy("", new String[] {}, new String[] { "id", "desc" }, new String[] {});
        ArrayList<City> c3 = cityRepository.findBy("", new String[] {}, new String[] { "id", "desc" }, new String[] { "0", "1" });

        assertEquals(2, c.size());
        assertEquals(2, c2.size());
        assertEquals("Toronto", c2.get(0).getName());
        assertEquals("Moscow", c2.get(1).getName());
        assertEquals(1, c3.size());
        assertEquals("Toronto", c3.get(0).getName());
    }

    @Test
    public void getRepositoryByEntityFromRepositoryManager_repositoryRetrieved() {
        RepositoryManager repositoryManager = new RepositoryManager(db);

        Repository weatherRepository = repositoryManager.getRepository(Weather.class);
        Repository cityRepository = repositoryManager.getRepository(City.class);

        assertTrue(weatherRepository instanceof SQLiteWeatherRepository);
        assertTrue(cityRepository instanceof SQLiteCityRepository);
    }

    @Test
    public void getRepositoryByEntityFromEntityManager_repositoryRetrieved() {
        EntityManager entityManager = new EntityManager(db);

        Repository weatherRepository = entityManager.getRepository(Weather.class);
        Repository cityRepository = entityManager.getRepository(City.class);

        assertTrue(weatherRepository instanceof SQLiteWeatherRepository);
        assertTrue(cityRepository instanceof SQLiteCityRepository);
    }


    @Test
    public void saveNullEntityByEntityManager_nullReturned() {
        Entity e = entityManager.save(null);

        assertNull(e);
    }

}
