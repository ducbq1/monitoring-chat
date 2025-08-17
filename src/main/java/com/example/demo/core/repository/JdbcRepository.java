package com.example.demo.core.repository;

import com.example.demo.core.entity.BaseEntity;

import java.util.List;
import java.util.Map;

public interface JdbcRepository<T extends BaseEntity> {
    List<T> findAll();

    List<T> paginate(int offset, int limit);

    int insert(T entity);

    int update(T entity);

    T findById(Long id);

    T findByName(String name);

    int deleteById(Long id);

    int deleteByName(String name);

    int count();
}
