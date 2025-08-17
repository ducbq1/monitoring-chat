package com.example.demo.core.service;

import com.example.demo.core.annotation.*;
import com.example.demo.helper.FieldUtil;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GenericServiceImpl implements GenericService {

    private final FieldUtil fieldUtil;

    public GenericServiceImpl(FieldUtil fieldUtil) {
        this.fieldUtil = fieldUtil;
    }

    public void setPrimaryKeyValue(Object entity, Object value) {
        fieldUtil.setPrimaryKeyValue(entity, value);
    }

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
            col.put("title", c != null && !c.title().isEmpty() ? c.title() : f.getName());
            Class<?> type = f.getType();
            if (type == Boolean.class || type == boolean.class) {
                col.put("type", "boolean");
            } else if (type == Integer.class || type == int.class
                    || type == Long.class || type == long.class
                    || type == Double.class || type == double.class
                    || type == Float.class || type == float.class) {
                col.put("type", "number");
            } else if (type == LocalDate.class) {
                col.put("type", "date");
            } else if (type == LocalDateTime.class || type == Date.class) {
                col.put("type", "datetime");
            } else {
                col.put("type", "text");
            }
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
        String primaryKey = getPrimaryKey(entity.getClass());

        for (Field field : entity.getClass().getDeclaredFields()) {
            Column colAnno = field.getAnnotation(Column.class);
            if (colAnno != null) {
                field.setAccessible(true);
                if (primaryKey.equalsIgnoreCase(colAnno.name())) {
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

    public String getPrimaryKey(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                Column colAnno = field.getAnnotation(Column.class);
                return (colAnno != null && !colAnno.name().isBlank())
                        ? colAnno.name()
                        : field.getName();
            }
        }
        throw new RuntimeException("No @PrimaryKey field found in " + clazz.getSimpleName());
    }

}
