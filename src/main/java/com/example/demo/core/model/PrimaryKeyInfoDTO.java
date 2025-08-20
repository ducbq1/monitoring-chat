package com.example.demo.core.model;

public record PrimaryKeyInfoDTO(String name, String sequence) {

    public static PrimaryKeyInfoDTO of(String name, String sequence) {
        return new PrimaryKeyInfoDTO(name, sequence);
    }
}
