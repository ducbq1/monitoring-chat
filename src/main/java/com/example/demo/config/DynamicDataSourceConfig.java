package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Configuration
public class DynamicDataSourceConfig {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceConfig.class);

    private final DatabaseProperties databaseProperties;
    private final Map<String, Supplier<JdbcTemplate>> jdbcTemplates = new ConcurrentHashMap<>();

    public DynamicDataSourceConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    public JdbcTemplate getJdbcTemplate(String name) {
        return jdbcTemplates.computeIfAbsent(name, k -> () -> new JdbcTemplate(buildDataSourceWithRetry(databaseProperties.getMultiDatabases().get(k), 3, 2000))).get();
    }

    private DataSource buildDataSourceWithRetry(DatabaseProperties.DbConfig config, int maxRetries, long delayMillis) {
        SQLException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Class.forName(config.getDriverClassName());

                DataSource ds = DataSourceBuilder.create()
                        .driverClassName(config.getDriverClassName())
                        .url(config.getUrl())
                        .username(config.getUsername())
                        .password(config.getPassword())
                        .build();

                try (Connection conn = ds.getConnection()) {
                    log.info("Connected to database {} successfully on attempt {}", config.getUrl(), attempt);
                }

                return ds;
            } catch (SQLException | ClassNotFoundException e) {
                lastException = e instanceof SQLException ? (SQLException) e : null;
                log.warn("Failed to connect to database {} on attempt {}: {}", config.getUrl(), attempt, e.getMessage());
                try { Thread.sleep(delayMillis); } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("Cannot connect to database: " + config.getUrl(), lastException);
    }
}
