package com.example.demo.core.service;

import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.repository.JdbcBaseRepository;
import com.example.demo.core.repository.JdbcRepository;
import com.example.demo.infradb.entity.RequestLog;

import java.util.List;

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
    public int insert(T entity) {
        return repository.insert(entity);
    }

    @Override
    public int update(T entity) {
        return repository.update(entity);
    }

    @Override
    public T findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public T findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public int deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public int deleteByName(String name) {
        return repository.deleteByName(name);
    }
}
