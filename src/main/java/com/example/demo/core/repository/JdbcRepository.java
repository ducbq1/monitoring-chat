package com.example.demo.core.repository;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.model.ColumnDataDTO;
import com.example.demo.core.model.DatabaseDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface JdbcRepository<T extends BaseEntity> {
    List<T> findAll();

    List<T> paginateSkip(int page, int limit);

    List<T> paginate(int page, int limit);

    List<T> paginate(int page, int limit, Map<String, Object> filters);

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

    DatabaseDTO getDatabaseInfo(String tableName);

    List<ColumnDataDTO> getRecordWithMetadata(String tableName, Object idValue) throws SQLException;

    List<ColumnDataDTO> getRecordWithMetadata(String tableName, String primaryKey, Object idValue) throws SQLException;
}
