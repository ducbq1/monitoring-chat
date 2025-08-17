package com.example.demo.core.service;

import com.example.demo.core.annotation.Column;
import com.example.demo.core.annotation.DataSource;
import com.example.demo.core.annotation.Table;
import com.example.demo.core.annotation.Transient;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
public class GenericServiceImpl implements GenericService {

    public <T> T create(Class<T> clazz) {
        T record = null;
        try {
            record = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return record;
    }

    public String getTableTitle(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table != null && !table.title().isEmpty()) {
            return table.title();
        }
        return clazz.getSimpleName().toLowerCase();
    }

    public String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            return table.name();
        }
        return clazz.getSimpleName().toLowerCase();
    }

    public String getDataSource(Class<?> clazz) {
        DataSource dataSource = clazz.getAnnotation(DataSource.class);
        if (dataSource != null && !dataSource.name().isEmpty()) {
            return dataSource.name();
        }
        return "h2";
    }


    public List<Map<String,String>> getColumns(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Map<String,String>> cols = new ArrayList<>();
        for(Field f : fields){
            if(f.isAnnotationPresent(Transient.class)) continue;
            Map<String,String> col = new HashMap<>();
            Column c = f.getAnnotation(Column.class);
            col.put("field", f.getName());
            col.put("column", c != null ? c.title() : f.getName());
            cols.add(col);
        }
        return cols;
    }

    public Object getFieldValue(Object entity, String fieldName) {
        try {
            Field f = entity.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(entity);
        } catch (Exception e){
            return null;
        }
    }

    public <T> Object[] getFieldValues(T entity, boolean includeIdLast) throws IllegalAccessException {
        List<Object> values = new ArrayList<>();
        Field idField = null;

        for (Field field : entity.getClass().getDeclaredFields()) {
            Column colAnno = field.getAnnotation(Column.class);
            if (colAnno != null) {
                field.setAccessible(true);
                if ("id".equalsIgnoreCase(colAnno.name())) {
                    idField = field;
                } else {
                    values.add(field.get(entity));
                }
            }
        }
        if (includeIdLast && idField != null) {
            values.add(idField.get(entity));
        }
        return values.toArray();
    }
}
