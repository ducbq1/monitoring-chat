package com.example.demo.helper;

import com.example.demo.core.annotation.PrimaryKey;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
}
