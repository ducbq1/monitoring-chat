package com.example.demo.core.service;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.model.ColumnData;
import com.example.demo.core.model.DatabaseDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface JdbcService<T extends BaseEntity> {
    List<T> paginate(int page, int size);

    List<T> findAll();

    List<T> paginate(int page, int size, Map<String, Object> filters);

    int insert(T entity);

    int update(T entity);

    T findById(Object id);

    List<T> findByColumn(String columnName, Object value);

    List<T> findByColumns(Map<String, Object> conditions);

    int deleteById(Object id);

    int deleteByColumn(String columnName, Object value);

    int count();

    int count(Map<String, Object> filters);

    DatabaseDTO getDatabaseInfo();

    List<ColumnData> getRecordWithMetadata(String tableName, Object idValue) throws SQLException;

    String getPrimaryKeyLabel(String tableName) throws SQLException;
}
