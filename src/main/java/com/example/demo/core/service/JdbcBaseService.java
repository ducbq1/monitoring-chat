package com.example.demo.core.service;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.model.ColumnData;
import com.example.demo.core.model.DatabaseDTO;
import com.example.demo.core.repository.JdbcRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class JdbcBaseService<T extends BaseEntity> implements JdbcService<T> {
    private final JdbcRepository<T> repository;

    protected JdbcBaseService(JdbcRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> paginate(int page, int size) {
        return repository.paginate(page, size);
    }

    @Override
    public List<T> paginate(int page, int size, Map<String, Object> filters) {
        return repository.paginate(page, size, filters);
    }

    @Override
    public int insert(T entity) {
        return repository.insert(entity);
    }

    @Override
    public int update(T entity) {
        return repository.update(entity);
    }

    @Override
    public T findById(Object id) {
        return repository.findById(id);
    }

    @Override
    public List<T> findByColumn(String columnName, Object value) {
        return repository.findByColumn(columnName, value);
    }

    @Override
    public List<T> findByColumns(Map<String, Object> conditions) {
        return repository.findByColumns(conditions);
    }

    @Override
    public int deleteById(Object id) {
        return repository.deleteById(id);
    }

    @Override
    public int deleteByColumn(String columnName, Object value) {
        return repository.deleteByColumn(columnName, value);
    }

    @Override
    public int count() {
        return repository.count();
    }

    @Override
    public int count(Map<String, Object> filters) {
        return repository.count(filters);
    }

    @Override
    public DatabaseDTO getDatabaseInfo() {
        return repository.getDatabaseInfo();
    }

    @Override
    public List<ColumnData> getRecordWithMetadata(String tableName, Object idValue) throws SQLException {
        return repository.getRecordWithMetadata(tableName, idValue);
    }

    @Override
    public String getPrimaryKeyLabel(String tableName) throws SQLException {
        return repository.getPrimaryKeyLabel(tableName);
    }
}
