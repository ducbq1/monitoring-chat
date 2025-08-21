package com.example.demo.core.model;

public class DatabaseDTO {
    private String name;
    private String dbType;
    private String version;
    private String driver;
    private String primaryKey;

    private DatabaseDTO(String name) {
        this.name = name;
    }

    private DatabaseDTO(String name, String dbType, String version, String driver) {
        this.name = name;
        this.dbType = dbType;
        this.version = version;
        this.driver = driver;
    }

    private DatabaseDTO(String name, String dbType, String version, String driver, String primaryKey) {
        this.name = name;
        this.dbType = dbType;
        this.version = version;
        this.driver = driver;
        this.primaryKey = primaryKey;
    }

    public static DatabaseDTO of(String name, String dbType, String version, String driver) {
        return new DatabaseDTO(name, dbType, version, driver);
    }

    public static DatabaseDTO of(String name, String dbType, String version, String driver, String primaryKey) {
        return new DatabaseDTO(name, dbType, version, driver, primaryKey);
    }

    public static DatabaseDTO of(String name) {
        return new DatabaseDTO(name);
    }

    public String getName() {
        return name;
    }

    public String getDbType() {
        return dbType;
    }

    public String getVersion() {
        return version;
    }

    public String getDriver() {
        return driver;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }


}
