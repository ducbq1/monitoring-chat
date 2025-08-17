package com.example.demo.touchpoint.repository;

import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.core.repository.JdbcBaseRepository;
import com.example.demo.core.repository.JdbcRepository;
import com.example.demo.core.service.GenericService;
import com.example.demo.touchpoint.entity.GlobalDefault;
import com.example.demo.touchpoint.mapper.GlobalDefaultRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GlobalDefaultRepository extends JdbcBaseRepository<GlobalDefault> {

    public GlobalDefaultRepository(DynamicDataSourceConfig dynamicDataSourceConfig, GenericService genericService) {
        super(GlobalDefault.class, dynamicDataSourceConfig, genericService);
    }
}
