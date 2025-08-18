package com.example.demo.helper;

import com.example.demo.core.annotation.*;
import com.example.demo.core.model.MetaDataDTO;

public class MetadataValidator {

    public static MetaDataDTO extractMetaData(Class<?> clazz) {
        MetaDataDTO dto = new MetaDataDTO();

        // Check @Metadata
        if (!clazz.isAnnotationPresent(Metadata.class)) {
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " must have the @Metadata annotation");
        }
        Metadata metadata = clazz.getAnnotation(Metadata.class);

        if (metadata.menu() == null || metadata.menu().isEmpty()) {
            throw new IllegalArgumentException("@Metadata.menu cannot be empty");
        }
        if (metadata.title() == null || metadata.title().isEmpty()) {
            throw new IllegalArgumentException("@Metadata.title cannot be empty");
        }
        dto.setMenu(metadata.menu());
        dto.setTitle(metadata.title());
        dto.setIcon(metadata.icon().isEmpty() ? null : metadata.icon());

        // Check @DataSource
        if (!clazz.isAnnotationPresent(DataSource.class)) {
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " must have the @DataSource annotation");
        }
        DataSource dataSource = clazz.getAnnotation(DataSource.class);
        if (dataSource.name() == null || dataSource.name().isEmpty()) {
            throw new IllegalArgumentException("@DataSource.name cannot be empty");
        }
        dto.setDatabase(dataSource.name());

        // Check @Table
        if (!clazz.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " must have the @Table annotation");
        }
        Table table = clazz.getAnnotation(Table.class);
        if (table.name() == null || table.name().isEmpty()) {
            throw new IllegalArgumentException("@Table.name cannot be empty");
        }
        dto.setTable(table.name());

        return dto;
    }
}
