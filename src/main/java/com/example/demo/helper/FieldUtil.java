package com.example.demo.helper;

import org.springframework.stereotype.Component;

@Component("fieldUtil")
public class FieldUtil {
    public Object get(Object record, String fieldName) {
        try {
            var field = record.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(record);
        } catch (Exception e) {
            return null;
        }
    }
}
