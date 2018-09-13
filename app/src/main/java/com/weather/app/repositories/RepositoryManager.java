package com.weather.app.repositories;

import com.weather.app.databases.Database;

import java.lang.reflect.Constructor;

public class RepositoryManager {

    private Database db;

    public RepositoryManager(Database db) {
        this.db = db;
    }

    public Repository getRepository(Class entityClass) {

        StringBuilder repositoryClassName =  new StringBuilder();
        repositoryClassName.append("com.weather.app.repositories.");
        repositoryClassName.append(db.getDatabaseTypeName().toLowerCase());
        repositoryClassName.append(".");
        repositoryClassName.append(db.getDatabaseTypeName());
        repositoryClassName.append(entityClass.getSimpleName());
        repositoryClassName.append("Repository");

        try {
            Class repositoryClass = Class.forName(repositoryClassName.toString());
            Constructor constructor = repositoryClass.getConstructor(Database.class);
            return (Repository) constructor.newInstance(db);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
