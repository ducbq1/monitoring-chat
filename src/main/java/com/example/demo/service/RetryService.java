package com.example.demo.service;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class RetryService {

    @Retryable(
        value = {SQLException.class, CannotGetJdbcConnectionException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    public <T> T execute(DBSupplier<T> supplier) throws Exception {
        return supplier.get();
    }

    @FunctionalInterface
    public interface DBSupplier<T> {
        T get() throws Exception;
    }
}
