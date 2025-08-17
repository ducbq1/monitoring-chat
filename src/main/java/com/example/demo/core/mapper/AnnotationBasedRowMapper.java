package com.example.demo.core.mapper;

import com.example.demo.core.annotation.Column;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public record AnnotationBasedRowMapper<T>(Class<T> type) implements RowMapper<T> {

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            T instance = type.getDeclaredConstructor().newInstance();

            for (Field field : type.getDeclaredFields()) {
                String colName = null;
                Column colAnno = field.getAnnotation(Column.class);

                if (colAnno != null && colAnno.name() != null && !colAnno.name().isBlank()) {
                    colName = colAnno.name();
                } else {
                    colName = field.getName(); // fallback sang tên field
                }

                field.setAccessible(true);

                Object value = rs.getObject(colName);

                if (value instanceof Timestamp timestamp && field.getType().equals(LocalDateTime.class)) {
                    value = timestamp.toLocalDateTime();
                }

                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new SQLException("Failed to map row to " + type.getSimpleName(), e);
        }
    }
}
