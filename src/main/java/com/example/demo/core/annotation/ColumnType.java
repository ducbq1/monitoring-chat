package com.example.demo.core.annotation;

public enum ColumnType {

    AUTO(""),
    TEXT("text"),
    NUMBER("number"),
    DATE("date"),
    DATETIME("datetime"),
    BOOLEAN("boolean"),
    CURRENCY("currency"),
    EMAIL("email"),
    PASSWORD("password"),
    URL("url"),
    PHONE("phone");

    private final String type;

    ColumnType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}

