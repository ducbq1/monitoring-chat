package com.example.demo.helper;

import com.example.demo.core.annotation.PrimaryKey;

import java.lang.reflect.Field;

public class AnnotationValidator {
    public static void validatePrimaryKey(Object obj) {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    PrimaryKey pk = field.getAnnotation(PrimaryKey.class);

                    if (pk.required() && value == null) {
                        throw new IllegalArgumentException("Primary key '" + field.getName() + "' is required but null.");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access field: " + field.getName(), e);
                }
            }
        }
    }
}
