package com.example.demo.core.service;

import com.example.demo.core.entity.BaseEntity;

import java.util.List;
import java.util.Map;

public interface JdbcService<T extends BaseEntity> {
    List<T> paginate(int page, int size);

    List<T> findAll();

    int insert(T entity);

    int update(T entity);

    T findById(Long id);

    T findByName(String name);

    int deleteById(Long id);

    int deleteByName(String name);

    int count();
}
