package com.example.demo.core.repository;

import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.service.GenericService;
import com.example.demo.core.service.GenericServiceImpl;
import com.example.demo.helper.DbMetadataHelper;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RepositoryFactory {
    private final DbMetadataHelper dbMetadataHelper;
    private final DynamicDataSourceConfig dynamicDataSourceConfig;
    private final GenericService genericService;

    private final Map<Class<?>, JdbcBaseRepository<?>> cache = new ConcurrentHashMap<>();

    public RepositoryFactory(DbMetadataHelper dbMetadataHelper, DynamicDataSourceConfig dynamicDataSourceConfig,
                             GenericService genericService) {
        this.dbMetadataHelper = dbMetadataHelper;
        this.dynamicDataSourceConfig = dynamicDataSourceConfig;
        this.genericService = genericService;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> JdbcBaseRepository<T> getRepository(Class<T> clazz) {
        return (JdbcBaseRepository<T>) cache.computeIfAbsent(clazz,
                c -> new JdbcBaseRepository<>(dbMetadataHelper, clazz, dynamicDataSourceConfig, genericService) {
                });
    }
}
