package com.weather.app.repositories;

import java.util.ArrayList;

public interface Repository<T> {

    T find(int id);

    ArrayList<T> findAll();

    ArrayList<T> findBy(String where, String[] args, String[] order, String[] limit);

    T findOneBy(String where, String[] args, String[] order);

    T save(T entity);

    void delete(int id);

}