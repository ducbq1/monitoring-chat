package com.example.demo.core.repository;

import com.example.demo.core.entity.BaseEntity;

import java.util.List;
import java.util.Map;

public interface JdbcRepository<T extends BaseEntity> {
    List<T> findAll();

    List<T> paginate(int page, int limit);

    int insert(T entity);

    int update(T entity);

    T findById(Object id);

    List<T> findByColumn(String columnName, Object value);

    List<T> findByColumns(Map<String, Object> conditions);

    int deleteById(Object id);

    int deleteByColumn(String columnName, Object value);

    int count();
}
