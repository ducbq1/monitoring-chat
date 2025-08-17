package com.example.demo.core.service;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.mapper.AnnotationBasedRowMapper;
import com.example.demo.core.repository.JdbcRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class JdbcBaseService<T extends BaseEntity> implements JdbcService<T> {
    private final JdbcRepository<T> repository;

    protected JdbcBaseService(JdbcRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return reverseCopy(repository.findAll());
    }

    @Override
    public List<T> paginate(int page, int size) {
        return reverseCopy(repository.paginate(page, size));
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

    private List<T> reverseCopy(List<T> original) {
        int size = original.size();
        List<T> reversed = new ArrayList<>(size);
        for (int i = size - 1; i >= 0; i--) {
            reversed.add(original.get(i));
        }
        return reversed;
    }

}
