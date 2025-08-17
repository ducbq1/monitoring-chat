package com.example.demo.infradb.service;

import com.example.demo.core.repository.JdbcRepository;
import com.example.demo.core.service.JdbcBaseService;
import com.example.demo.core.service.JdbcService;
import com.example.demo.infradb.entity.RequestLog;
import com.example.demo.infradb.repository.RequestLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestLogService extends JdbcBaseService<RequestLog> {
    protected RequestLogService(JdbcRepository<RequestLog> repository) {
        super(repository);
    }
}
