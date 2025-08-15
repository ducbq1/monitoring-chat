package com.example.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
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
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private final Map<String, JdbcTemplate> jdbcTemplates = new ConcurrentHashMap<>();

    public DynamicDataSourceConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    public JdbcTemplate getJdbcTemplate(String name) {
        return jdbcTemplates.computeIfAbsent(name, k -> {
            DatabaseProperties.DbConfig config = databaseProperties.getMultiDatabases().get(k);
            if (config == null) {
                throw new IllegalArgumentException("No database config for key: " + k);
            }
            DataSource ds = dataSources.computeIfAbsent(k, key -> buildDataSourceWithRetry(config, 3, 2000));
            return new JdbcTemplate(ds);
        });
    }

    private DataSource buildDataSourceWithRetry(DatabaseProperties.DbConfig config, int maxRetries, long delayMillis) {
        SQLException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Class.forName(config.getDriverClassName());
                HikariDataSource ds = new HikariDataSource();
                ds.setDriverClassName(config.getDriverClassName());
                ds.setJdbcUrl(config.getUrl());
                ds.setUsername(config.getUsername());
                ds.setPassword(config.getPassword());
                ds.setMaximumPoolSize(10);
                ds.setConnectionTimeout(30000);

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

    @PreDestroy
    public void closeAllPools() {
        dataSources.values().forEach(ds -> {
            if (ds instanceof HikariDataSource hikari) {
                log.info("Closing Hikari pool for {}", hikari.getJdbcUrl());
                hikari.close();
            }
        });
    }
}
