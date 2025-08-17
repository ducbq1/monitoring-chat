package com.example.demo.infradb.repository;

import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.core.repository.JdbcBaseRepository;
import com.example.demo.core.repository.JdbcRepository;
import com.example.demo.core.service.GenericService;
import com.example.demo.infradb.entity.RequestLog;
import org.springframework.stereotype.Repository;

@Repository
public class RequestLogRepository extends JdbcBaseRepository<RequestLog> {

    public RequestLogRepository(DynamicDataSourceConfig dynamicDataSourceConfig, GenericService genericService) {
        super(RequestLog.class, dynamicDataSourceConfig, genericService);
    }
}
