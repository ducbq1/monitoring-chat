package com.example.demo.infradb.service;

import com.example.demo.infradb.entity.RequestLog;
import com.example.demo.infradb.repository.RequestLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestLogService {

    private final RequestLogRepository repository;

    public RequestLogService(RequestLogRepository repository) {
        this.repository = repository;
    }

    public List<RequestLog> getAllLogs(String dbName) {
        return repository.findAll(dbName);
    }

    public RequestLog getLogById(String dbName, Long id) {
        return repository.findById(dbName, id);
    }

    public void createLog(String dbName, RequestLog log) {
        repository.insert(dbName, log);
    }

    public void updateLog(String dbName, RequestLog log) {
        repository.update(dbName, log);
    }

    public void deleteLog(String dbName, Long id) {
        repository.deleteById(dbName, id);
    }
}
