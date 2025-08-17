package com.example.demo.core.service;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.repository.RepositoryFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServiceFactory {

    private final RepositoryFactory repositoryFactory;
    private final Map<Class<?>, JdbcBaseService<?>> cache = new ConcurrentHashMap<>();

    public ServiceFactory(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> JdbcBaseService<T> getService(Class<T> clazz) {
        return (JdbcBaseService<T>) cache.computeIfAbsent(clazz,
                c -> new JdbcBaseService<T>(repositoryFactory.getRepository(clazz)) {
                });
    }
}
