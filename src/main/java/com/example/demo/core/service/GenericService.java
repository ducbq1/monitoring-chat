package com.example.demo.core.service;

import com.example.demo.core.model.MetaDataDTO;

import java.util.List;
import java.util.Map;

public interface GenericService {
    void setPrimaryKeyValue(Object entity, Object value);

    <T> T create(Class<T> clazz);

    MetaDataDTO getMetadata(Class<?> clazz);

    String getTableName(Class<?> clazz);

    String getDataSource(Class<?> clazz);

    List<Map<String, Object>> getColumns(Class<?> clazz);

    Object getFieldValue(Object entity, String fieldName);

    List<String> getColumns(Class<?> clazz, boolean includeId);

    <T> Object[] getFieldValues(T entity, boolean includeIdLast) throws IllegalAccessException;

    String getPrimaryKey(Class<?> clazz);
}
