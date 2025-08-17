package com.example.demo.core.service;

import java.util.List;
import java.util.Map;

public interface GenericService {
    void setPrimaryKeyValue(Object entity, Object value);

    <T> T create(Class<T> clazz);

    String getTableTitle(Class<?> clazz);

    String getTableName(Class<?> clazz);

    String getDataSource(Class<?> clazz);

    List<Map<String, String>> getColumns(Class<?> clazz);

    Object getFieldValue(Object entity, String fieldName);

    <T> Object[] getFieldValues(T entity, boolean includeIdLast) throws IllegalAccessException;

    String getPrimaryKey(Class<?> clazz);
}
