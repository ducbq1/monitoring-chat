package com.example.demo.helper;

import com.example.demo.core.annotation.Column;
import com.example.demo.core.annotation.PrimaryKey;
import com.example.demo.core.annotation.Table;
import com.example.demo.core.model.MetaDataDTO;
import com.example.demo.core.model.PrimaryKeyInfoDTO;
import com.example.demo.core.model.TableInfoDTO;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component("fieldUtil")
public class FieldUtil {

    public Field getPrimaryKeyField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                field.setAccessible(true);
                return field;
            }
        }

        try {
            Field idField = clazz.getDeclaredField("id");
            idField.setAccessible(true);
            return idField;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public Object get(Object record, String fieldName) {
        try {
            var field = record.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(record);
        } catch (Exception e) {
            return null;
        }
    }

    public Object getPrimaryKeyValue(Object entity) {
        Field pk = getPrimaryKeyField(entity.getClass());
        if (pk == null) return null;
        try {
            return pk.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access primary key field", e);
        }
    }

    public void setPrimaryKeyValue(Object entity, Object value) {
        Field pk = getPrimaryKeyField(entity.getClass());
        if (pk == null) return;
        try {
            Class<?> type = pk.getType();
            Object converted = convertToType(value, type);
            pk.set(entity, converted);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set primary key field", e);
        }
    }

    private Object convertToType(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isAssignableFrom(value.getClass())) {
            return value; // cùng kiểu rồi
        }

        String strVal = value.toString();

        if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(strVal);
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(strVal);
        } else if (targetType == String.class) {
            return strVal;
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(strVal);
        } else if (targetType == LocalDateTime.class) {
            return LocalDateTime.parse(strVal);
        } else if (targetType == LocalDate.class) {
            return LocalDate.parse(strVal);
        } else if (targetType == LocalTime.class) {
            return LocalTime.parse(strVal);
        }

        throw new IllegalArgumentException("Unsupported type: " + targetType);
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

    public MetaDataDTO getMetadata(Class<?> clazz) {
        return MetadataValidator.extractMetaData(clazz);
    }

    public TableInfoDTO getTable(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty() && !table.datasource().isEmpty()) {
            return TableInfoDTO.of(table.datasource(), table.name());
        }
        return TableInfoDTO.of("h2", clazz.getSimpleName());
    }


    public List<Map<String,Object>> getColumns(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Map<String,Object>> cols = new ArrayList<>();
        for(Field f : fields){
            Map<String,Object> col = new HashMap<>();
            Column c = f.getAnnotation(Column.class);
            col.put("column", c != null && !c.name().isBlank() ? c.name() : f.getName());
            col.put("field", f.getName());
            col.put("title", c != null && !c.title().isEmpty() ? c.title() : f.getName());
            col.put("required", c != null && c.required());
            col.put("readonly", c != null && c.readonly());
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
            } else if (type == String.class ||  type == String[].class) {
                col.put("type", "text");
            } else {
                col.put("type", "object");
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

    public List<String> getColumns(Class<?> clazz, boolean includeId) {
        List<String> columns = new ArrayList<>();
        String primaryKey = getPrimaryKey(clazz).name();

        for (Field field : clazz.getDeclaredFields()) {
            Column colAnno = field.getAnnotation(Column.class);
            if (colAnno != null) {
                if (!includeId && primaryKey.equalsIgnoreCase(colAnno.name())) {
                    continue;
                }
                columns.add(colAnno.name());
            }
        }

        return columns;
    }

    public <T> Object[] getFieldValues(T entity, boolean includeIdLast) throws IllegalAccessException {
        List<Object> values = new ArrayList<>();
        Field idField = null;
        String primaryKey = getPrimaryKey(entity.getClass()).name();

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

    public PrimaryKeyInfoDTO getPrimaryKey(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                Column colAnno = field.getAnnotation(Column.class);
                String name =  (colAnno != null && !colAnno.name().isEmpty())
                        ? colAnno.name()
                        : field.getName();
                return PrimaryKeyInfoDTO.of(name, primaryKey.sequence());
            }
        }
        throw new RuntimeException("No @PrimaryKey field found in " + clazz.getSimpleName());
    }
}
