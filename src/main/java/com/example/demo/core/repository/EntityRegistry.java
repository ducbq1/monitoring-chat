package com.example.demo.core.repository;

import com.example.demo.core.annotation.Metadata;
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
            Metadata metadata = clazz.getAnnotation(Metadata.class);
            String datasource = (table != null && !table.name().isEmpty()) ? table.datasource() : "anonymous";
            String name = (table != null && !table.name().isEmpty()) ? table.name() : clazz.getSimpleName().toLowerCase();
            temp.put(metadata.type() + ":" + datasource + ":" + name, clazz);
        }
        registry = Collections.unmodifiableMap(temp);
    }

    public static Class<? extends BaseEntity> get(String datasource, String name) {
        return get("list", datasource, name);
    }

    public static Class<? extends BaseEntity> get(String type, String datasource, String name) {
        return registry.get(type + ":" + datasource + ":" + name);
    }

    public static Map<String, Class<? extends BaseEntity>> all() {
        return registry;
    }
}
