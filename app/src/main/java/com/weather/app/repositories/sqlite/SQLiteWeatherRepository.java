package com.weather.app.repositories.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;

import com.weather.app.databases.Database;
import com.weather.app.databases.SQLiteDatabase;
import com.weather.app.entities.City;
import com.weather.app.entities.Weather;
import com.weather.app.repositories.Repository;

import java.util.ArrayList;

public class SQLiteWeatherRepository implements Repository<Weather> {

    private SQLiteDatabase db;

    public SQLiteWeatherRepository(Database db) {
        this.db = (SQLiteDatabase) db;
    }

    @Override
    public Weather find(int id) {
        Cursor cursor = db.query(
                "SELECT w.*, c.id city_id, c.name city_name " +
                        "FROM " + Weather.getDatabaseTableName() + " w " +
                        "LEFT JOIN city c ON c.id = w.city_id " +
                        "WHERE w.id = ?",
                new String[] { String.valueOf(id) }
        );

        if(cursor.moveToFirst()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("city_id")));
            city.setName(cursor.getString(cursor.getColumnIndex("city_name")));

            Weather weather = new Weather();
            weather.setId(cursor.getInt(cursor.getColumnIndex("id")));
            weather.setTemperature(cursor.getDouble(cursor.getColumnIndex("temperature")));
            weather.setHumidity(cursor.getInt(cursor.getColumnIndex("humidity")));
            weather.setPressure(cursor.getInt(cursor.getColumnIndex("pressure")));
            weather.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            weather.setCity(city);

            return weather;
        }

        return null;
    }

    @Override
    public ArrayList<Weather> findAll() {
        Cursor cursor = db.query(
                "SELECT w.*, c.id city_id, c.name city_name " +
                        "FROM " + Weather.getDatabaseTableName() + " w " +
                        "LEFT JOIN city c ON c.id = w.city_id "
        );

        ArrayList<Weather> array = new ArrayList<>();

        while (cursor.moveToNext()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("city_id")));
            city.setName(cursor.getString(cursor.getColumnIndex("city_name")));

            Weather weather = new Weather();
            weather.setId(cursor.getInt(cursor.getColumnIndex("id")));
            weather.setTemperature(cursor.getDouble(cursor.getColumnIndex("temperature")));
            weather.setHumidity(cursor.getInt(cursor.getColumnIndex("humidity")));
            weather.setPressure(cursor.getInt(cursor.getColumnIndex("pressure")));
            weather.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            weather.setCity(city);

            array.add(weather);
        }

        return array;
    }

    @Override
    public ArrayList<Weather> findBy(String where, String[] args, String[] order, String[] limit) {
        Cursor cursor = db.query(
                "SELECT w.*, c.id city_id, c.name city_name " +
                        "FROM " + Weather.getDatabaseTableName() + " w " +
                        "LEFT JOIN city c ON c.id = w.city_id " +
                        (!TextUtils.isEmpty(where) ? " WHERE " + where : "") +
                        (order.length == 2 && !TextUtils.isEmpty(order[0]) && !TextUtils.isEmpty(order[1]) ? " ORDER BY " + order[0] + " " + order[1] : "") +
                        (limit.length == 2 && !TextUtils.isEmpty(limit[0]) && !TextUtils.isEmpty(limit[1]) ? " LIMIT " + limit[0] + "," + limit[1] : ""),
                args
        );

        ArrayList<Weather> array = new ArrayList<>();

        while (cursor.moveToNext()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("city_id")));
            city.setName(cursor.getString(cursor.getColumnIndex("city_name")));

            Weather weather = new Weather();
            weather.setId(cursor.getInt(cursor.getColumnIndex("id")));
            weather.setTemperature(cursor.getDouble(cursor.getColumnIndex("temperature")));
            weather.setHumidity(cursor.getInt(cursor.getColumnIndex("humidity")));
            weather.setPressure(cursor.getInt(cursor.getColumnIndex("pressure")));
            weather.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            weather.setCity(city);

            array.add(weather);
        }


        return array;
    }

    @Override
    public Weather findOneBy(String where, String[] args, String[] order) {
        Cursor cursor = db.query(
                "SELECT w.*, c.id city_id, c.name city_name " +
                        "FROM " + Weather.getDatabaseTableName() + " w " +
                        "LEFT JOIN city c ON c.id = w.city_id " +
                        (!TextUtils.isEmpty(where) ? " WHERE " + where : "") +
                        (order.length == 2 && !TextUtils.isEmpty(order[0]) && !TextUtils.isEmpty(order[1]) ? " ORDER BY " + order[0] + " " + order[1] : "") +
                        " LIMIT 1",
                args
        );

        if(cursor.moveToFirst()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("city_id")));
            city.setName(cursor.getString(cursor.getColumnIndex("city_name")));

            Weather weather = new Weather();
            weather.setId(cursor.getInt(cursor.getColumnIndex("id")));
            weather.setTemperature(cursor.getDouble(cursor.getColumnIndex("temperature")));
            weather.setHumidity(cursor.getInt(cursor.getColumnIndex("humidity")));
            weather.setPressure(cursor.getInt(cursor.getColumnIndex("pressure")));
            weather.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            weather.setCity(city);

            return weather;
        }

        return null;
    }

    @Override
    public Weather save(Weather weather) {
        if(weather == null) {
            return null;
        }

        if(weather.getCity() == null || (weather.getCity() != null && weather.getCity().getId() == 0)) {
            throw new SQLException("Invalid City");
        }

        ContentValues values = new ContentValues();
        values.put("temperature", weather.getTemperature());
        values.put("humidity", weather.getHumidity());
        values.put("pressure", weather.getPressure());
        values.put("description", weather.getDescription());
        values.put("city_id", weather.getCity().getId());

        if(weather.getId() > 0) {
            db.update(Weather.getDatabaseTableName(), values, "id = ?", new String[] { String.valueOf(weather.getId()) });
        } else {
            int insertId = db.insert(Weather.getDatabaseTableName(), values);
            weather.setId(insertId);
        }

        return weather;
    }

    @Override
    public void delete(int id) {
        db.delete(Weather.getDatabaseTableName(), "id = ?", new String[] { String.valueOf(id) });
    }

}
