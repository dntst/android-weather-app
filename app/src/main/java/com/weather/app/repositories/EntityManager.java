package com.weather.app.repositories;

import com.weather.app.databases.Database;
import com.weather.app.entities.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityManager {

    private Map<String, Repository> repositoryList = new HashMap<>();
    private Database db;

    public EntityManager(Database db) {
        this.db = db;
    }

    public Repository getRepository(Class entityClass) {
        return createRepository(entityClass);
    }

    private Repository createRepository(Class entityClass) {
        String entityName = entityClass.toString();

        if(repositoryList.containsKey(entityName)) {
            return repositoryList.get(entityName);
        } else {
            RepositoryManager repositoryManager = new RepositoryManager(db);
            Repository repository = repositoryManager.getRepository(entityClass);
            repositoryList.put(entityName, repository);

            return repository;
        }
    }

    public void delete(Entity entity) {
        getRepository(entity.getClass()).delete(entity.getId());
    }

    public Entity save(Entity entity) {
        if(entity == null) {
            return null;
        }

        Repository repository = getRepository(entity.getClass());
        if(repository != null) {
            return (Entity) repository.save(entity);
        }

        return null;
    }

}
