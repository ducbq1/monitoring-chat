package com.example.demo.core.model;

public record TableInfoDTO(String datasource, String name) {

    public static TableInfoDTO of(String datasource, String name) {
        return new TableInfoDTO(datasource, name);
    }
}
