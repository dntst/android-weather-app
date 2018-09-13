package com.weather.app.repositories.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.weather.app.databases.Database;
import com.weather.app.databases.SQLiteDatabase;
import com.weather.app.entities.City;
import com.weather.app.repositories.Repository;

import java.util.ArrayList;

public class SQLiteCityRepository implements Repository<City> {

    private SQLiteDatabase db;

    public SQLiteCityRepository(Database db) {
        this.db = (SQLiteDatabase) db;
    }

    @Override
    public City find(int id) {
        Cursor cursor = db.query(
                "SELECT id, name FROM " + City.getDatabaseTableName() + " WHERE id = ?",
                new String[] { String.valueOf(id) }
        );

        if(cursor.moveToFirst()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setName(cursor.getString(cursor.getColumnIndex("name")));

            return city;
        }

        return null;
    }

    @Override
    public ArrayList<City> findAll() {
        Cursor cursor = db.query("SELECT * FROM " + City.getDatabaseTableName());

        ArrayList<City> array = new ArrayList<>();

        while (cursor.moveToNext()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setName(cursor.getString(cursor.getColumnIndex("name")));
            array.add(city);
        }

        return array;
    }

    @Override
    public ArrayList<City> findBy(String where, String[] args, String[] order, String[] limit) {
        Cursor cursor = db.query(
                "SELECT * FROM " + City.getDatabaseTableName() + " " +
                        (!TextUtils.isEmpty(where) ? " WHERE " + where : "") +
                        (order.length == 2 && !TextUtils.isEmpty(order[0]) && !TextUtils.isEmpty(order[1]) ? " ORDER BY " + order[0] + " " + order[1] : "") +
                        (limit.length == 2 && !TextUtils.isEmpty(limit[0]) && !TextUtils.isEmpty(limit[1]) ? " LIMIT " + limit[0] + "," + limit[1] : "")
                ,
                args
        );

        ArrayList<City> array = new ArrayList<>();

        while (cursor.moveToNext()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setName(cursor.getString(cursor.getColumnIndex("name")));
            array.add(city);
        }

        return array;
    }

    @Override
    public City findOneBy(String where, String[] args, String[] order) {
        Cursor cursor = db.query(
                "SELECT * FROM " + City.getDatabaseTableName() + " "  +
                        (!TextUtils.isEmpty(where) ? " WHERE " + where : "" +
                        (order.length == 2 && !TextUtils.isEmpty(order[0]) && !TextUtils.isEmpty(order[1]) ? " ORDER BY " + order[0] + " " + order[1] : "") +
                        " LIMIT 1"),
                args
        );

        if(cursor.moveToFirst()) {
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setName(cursor.getString(cursor.getColumnIndex("name")));

            return city;
        }

        return null;
    }

    @Override
    public City save(City city) {
        if(city == null) {
            return null;
        }

        ContentValues values = new ContentValues();
        values.put("name", city.getName());

        if(city.getId() > 0) {
            db.update(City.getDatabaseTableName(), values, "id = ?", new String[] { String.valueOf(city.getId()) });
        } else {
            int insertId = db.insert(City.getDatabaseTableName(), values);
            city.setId(insertId);
        }

        return city;
    }

    @Override
    public void delete(int id) {
        db.delete(City.getDatabaseTableName(), "id = ?", new String[] { String.valueOf(id) });
    }

}
