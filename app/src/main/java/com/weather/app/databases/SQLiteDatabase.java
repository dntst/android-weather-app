package com.weather.app.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.weather.app.entities.City;
import com.weather.app.entities.Weather;

public class SQLiteDatabase extends SQLiteOpenHelper implements Database {

    private static final String DATABASE_TYPE = "SQLite";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weather.db";

    private static volatile SQLiteDatabase instance;

    public static SQLiteDatabase getInstance(Context context) {
        synchronized (SQLiteDatabase.class) {
            if (instance == null) {
                instance = new SQLiteDatabase(context);
            }
        }

        return instance;
    }

    private SQLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + City.getDatabaseTableName() + "(" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT NOT NULL" +
                ")"
        );

        db.execSQL("CREATE TABLE " + Weather.getDatabaseTableName() + "(" +
                "id INTEGER PRIMARY KEY, " +
                "temperature REAL NOT NULL, " +
                "pressure INTEGER, " +
                "humidity INTEGER," +
                "description TEXT," +
                "city_id INTEGER NOT NULL," +
                "FOREIGN KEY (city_id) REFERENCES city(id) ON DELETE CASCADE" +
                ")"
        );
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public String getDatabaseTypeName() {
        return DATABASE_TYPE;
    }

    public Cursor query(String rawSql, String[] selectionArgs) {
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        return db.rawQuery(rawSql, selectionArgs);
    }

    public Cursor query(String rawSql) {
        return query(rawSql, null);
    }

    public int insert(String table, ContentValues values) {
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        return (int) db.insert(table, null, values);
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        db.update(table, values, whereClause, whereArgs);
    }

    public void delete(String table, String whereClause, String[] whereArgs) {
        android.database.sqlite.SQLiteDatabase db = getWritableDatabase();

        db.delete(table, whereClause, whereArgs);
    }
}
