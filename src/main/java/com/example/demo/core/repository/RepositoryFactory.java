package com.example.demo.core.repository;

import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.model.TableInfoDTO;
import com.example.demo.helper.DbMetadataHelper;
import com.example.demo.helper.FieldUtil;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class RepositoryFactory {
    private final DynamicDataSourceConfig dynamicDataSourceConfig;
    private final FieldUtil fieldUtil;

    private final Map<Class<?>, JdbcBaseRepository<?>> cache = new ConcurrentHashMap<>();

    public RepositoryFactory(DynamicDataSourceConfig dynamicDataSourceConfig,
                             FieldUtil fieldUtil) {
        this.dynamicDataSourceConfig = dynamicDataSourceConfig;
        this.fieldUtil = fieldUtil;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> JdbcBaseRepository<T> getRepository(Class<T> clazz) {
        return (JdbcBaseRepository<T>) cache.computeIfAbsent(clazz, c -> {
//            try {
//                validatePrimaryKey(clazz, dynamicDataSourceConfig);
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
            return new JdbcBaseRepository<>(clazz, dynamicDataSourceConfig, fieldUtil) {
            };
        }
        );
    }

    private void validatePrimaryKey(Class<?> entityClass, DynamicDataSourceConfig dynamicDataSourceConfig) throws SQLException {
        TableInfoDTO tableInfoDTO = fieldUtil.getTable(entityClass);
        String tableName = tableInfoDTO.name();
        String datasource = tableInfoDTO.datasource();
        String primaryKeyField = fieldUtil.getPrimaryKey(entityClass).name();
        boolean isValid = DbMetadataHelper.isPrimaryKeyValid(
                dynamicDataSourceConfig.getJdbcTemplate(datasource),
                tableName, primaryKeyField);

        if (!isValid) {
            throw new IllegalStateException("The primary key field '" + primaryKeyField + "' does not match the primary key in the database table '" + tableName + "'");
        }
    }
}
