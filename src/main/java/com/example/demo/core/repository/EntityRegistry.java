package com.example.demo.core.repository;

import com.example.demo.core.annotation.Table;
import com.example.demo.core.entity.BaseEntity;
import org.reflections.Reflections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntityRegistry {

    private static final Map<String, Class<? extends BaseEntity>> registry;

    static {
        Map<String, Class<? extends BaseEntity>> temp = new HashMap<>();
        Reflections reflections = new Reflections("com.example.demo.core.entity");
        Set<Class<? extends BaseEntity>> entities = reflections.getSubTypesOf(BaseEntity.class);
        for (Class<? extends BaseEntity> clazz : entities) {
            Table table = clazz.getAnnotation(Table.class);
            String name = (table != null && !table.name().isEmpty()) ? table.name() : clazz.getSimpleName().toLowerCase();
            temp.put(name, clazz);
        }
        registry = Collections.unmodifiableMap(temp);
    }

    public static Class<? extends BaseEntity> get(String name) {
        return registry.get(name);
    }

    public static Map<String, Class<? extends BaseEntity>> all() {
        return registry;
    }
}
