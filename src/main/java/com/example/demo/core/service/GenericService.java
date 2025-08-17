package com.example.demo.core.service;

import com.example.demo.core.annotation.Column;
import com.example.demo.core.annotation.Table;
import com.example.demo.core.annotation.Transient;
import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.repository.JdbcRepository;
import com.example.demo.infradb.entity.RequestLog;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class GenericService {

    public String getTableName(Class<?> clazz) {
        Table t = clazz.getAnnotation(Table.class);
        return t != null ? t.name() : clazz.getSimpleName().toLowerCase();
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

    public <T extends BaseEntity, R extends JdbcRepository<T>> List<T> findAll(R repository) {
        return repository.findAll();
    }
}
