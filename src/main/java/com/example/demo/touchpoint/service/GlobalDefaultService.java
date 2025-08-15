package com.example.demo.touchpoint.service;

import com.example.demo.touchpoint.entity.GlobalDefault;
import com.example.demo.touchpoint.repository.GlobalDefaultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalDefaultService {

    private final GlobalDefaultRepository repository;

    public GlobalDefaultService(GlobalDefaultRepository repository) {
        this.repository = repository;
    }

    public List<GlobalDefault> getAll() {
        return repository.findAll();
    }

    public GlobalDefault getByName(String name) {
        return repository.findByName(name);
    }

    public void save(GlobalDefault globalDefault) {
        repository.insert(globalDefault);
    }

    public void update(GlobalDefault globalDefault) {
        repository.update(globalDefault);
    }

    public void delete(String name) {
        repository.deleteByName(name);
    }
}
