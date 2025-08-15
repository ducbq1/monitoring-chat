package com.example.demo.infradb.repository;

import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.infradb.annotation.Column;
import com.example.demo.infradb.annotation.Table;
import com.example.demo.infradb.entity.RequestLog;
import com.example.demo.infradb.mapper.AnnotationBasedRowMapper;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;

@Repository
public class RequestLogRepository {

    private final DynamicDataSourceConfig dynamicDataSourceConfig;

    public RequestLogRepository(DynamicDataSourceConfig dynamicDataSourceConfig) {
        this.dynamicDataSourceConfig = dynamicDataSourceConfig;
    }

    private String getTableName() {
        return RequestLog.class.getAnnotation(Table.class).name();
    }

    public List<RequestLog> findAll(String dbName) {
        String sql = "SELECT * FROM " + getTableName();
        return dynamicDataSourceConfig.getJdbcTemplate(dbName)
                .query(sql, new AnnotationBasedRowMapper<>(RequestLog.class));
    }

    public RequestLog findById(String dbName, Long id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        return dynamicDataSourceConfig.getJdbcTemplate(dbName)
                .queryForObject(sql, new AnnotationBasedRowMapper<>(RequestLog.class), id);
    }

    public int insert(String dbName, RequestLog log) {
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");

        try {
            for (Field field : RequestLog.class.getDeclaredFields()) {
                Column colAnno = field.getAnnotation(Column.class);
                if (colAnno != null && !"id".equalsIgnoreCase(colAnno.name())) { // bỏ id nếu auto-gen
                    columns.add(colAnno.name());
                    placeholders.add("?");
                }
            }

            String sql = "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + placeholders + ")";
            return dynamicDataSourceConfig.getJdbcTemplate(dbName)
                    .update(sql, getFieldValues(log, false));

        } catch (Exception e) {
            throw new RuntimeException("Error inserting RequestLog", e);
        }
    }

    public int update(String dbName, RequestLog log) {
        StringJoiner setClauses = new StringJoiner(", ");

        try {
            for (Field field : RequestLog.class.getDeclaredFields()) {
                Column colAnno = field.getAnnotation(Column.class);
                if (colAnno != null && !"id".equalsIgnoreCase(colAnno.name())) {
                    setClauses.add(colAnno.name() + " = ?");
                }
            }

            String sql = "UPDATE " + getTableName() + " SET " + setClauses + " WHERE id = ?";
            return dynamicDataSourceConfig.getJdbcTemplate(dbName)
                    .update(sql, getFieldValues(log, true));

        } catch (Exception e) {
            throw new RuntimeException("Error updating RequestLog", e);
        }
    }

    public int deleteById(String dbName, Long id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        return dynamicDataSourceConfig.getJdbcTemplate(dbName).update(sql, id);
    }

    private Object[] getFieldValues(RequestLog log, boolean includeIdLast) throws IllegalAccessException {
        List<Object> values = new java.util.ArrayList<>();
        Field idField = null;

        for (Field field : RequestLog.class.getDeclaredFields()) {
            Column colAnno = field.getAnnotation(Column.class);
            if (colAnno != null) {
                field.setAccessible(true);
                if ("id".equalsIgnoreCase(colAnno.name())) {
                    idField = field;
                } else {
                    values.add(field.get(log));
                }
            }
        }
        if (includeIdLast && idField != null) {
            values.add(idField.get(log));
        }
        return values.toArray();
    }
}
