package com.example.demo.core.repository;

import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.core.annotation.Column;
import com.example.demo.core.annotation.DataSource;
import com.example.demo.core.annotation.Table;
import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.mapper.AnnotationBasedRowMapper;
import com.example.demo.core.service.GenericService;
import com.example.demo.infradb.entity.RequestLog;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;

public abstract class JdbcBaseRepository<T extends BaseEntity> implements JdbcRepository<T> {
    private final Class<T> entityClass;
    private final DynamicDataSourceConfig dynamicDataSourceConfig;
    private final GenericService genericService;

    public JdbcBaseRepository(Class<T> entityClass, DynamicDataSourceConfig dynamicDataSourceConfig, GenericService genericService) {
        this.entityClass = entityClass;
        this.dynamicDataSourceConfig = dynamicDataSourceConfig;
        this.genericService = genericService;
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        return jdbcTemplate().query(sql, new AnnotationBasedRowMapper<>(entityClass));
    }

    @Override
    public int insert(T entity) {
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");

        try {
            for (Field field : entityClass.getDeclaredFields()) {
                Column colAnno = field.getAnnotation(Column.class);
                if (colAnno != null && !"id".equalsIgnoreCase(colAnno.name())) { // bỏ id nếu auto-gen
                    columns.add(colAnno.name());
                    placeholders.add("?");
                }
            }

            String sql = "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + placeholders + ")";
            return jdbcTemplate().update(sql, genericService.getFieldValues(entity, false));

        } catch (Exception e) {
            throw new RuntimeException("Error inserting " + getTableName(), e);
        }
    }

    @Override
    public int update(T entity) {
        StringJoiner setClauses = new StringJoiner(", ");

        try {
            for (Field field : entityClass.getDeclaredFields()) {
                Column colAnno = field.getAnnotation(Column.class);
                if (colAnno != null && !"id".equalsIgnoreCase(colAnno.name())) {
                    setClauses.add(colAnno.name() + " = ?");
                }
            }

            String sql = "UPDATE " + getTableName() + " SET " + setClauses + " WHERE id = ?";
            return jdbcTemplate().update(sql, genericService.getFieldValues(entity, true));

        } catch (Exception e) {
            throw new RuntimeException("Error updating " + getTableName(), e);
        }
    }

    @Override
    public T findById(Long id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        return jdbcTemplate().queryForObject(sql, new AnnotationBasedRowMapper<>(entityClass), id);
    }

    @Override
    public T findByName(String name) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE name = ?";
        return jdbcTemplate().queryForObject(sql, new AnnotationBasedRowMapper<>(entityClass), name);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        return jdbcTemplate().update(sql, id);
    }

    @Override
    public int deleteByName(String name) {
        String sql = "DELETE FROM " + getTableName() + " WHERE name = ?";
        return jdbcTemplate().update(sql, name);
    }

    private String getTableName() {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            return table.name();
        }
        return entityClass.getSimpleName().toLowerCase();
    }

    private String getDataSource() {
        DataSource dataSource = entityClass.getAnnotation(DataSource.class);
        if (dataSource != null && !dataSource.name().isEmpty()) {
            return dataSource.name();
        }
        return "h2";
    }

    private JdbcTemplate jdbcTemplate() {
        return dynamicDataSourceConfig.getJdbcTemplate(getDataSource());
    }
}
