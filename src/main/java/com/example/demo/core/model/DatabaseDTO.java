package com.example.demo.core.model;

public class DatabaseDTO {
    private String name;
    private String dbType;
    private String version;
    private String driver;

    private DatabaseDTO(String name) {
        this.name = name;
    }

    private DatabaseDTO(String name, String dbType, String version, String driver) {
        this.name = name;
        this.dbType = dbType;
        this.version = version;
        this.driver = driver;
    }

    public static DatabaseDTO of(String name, String dbType, String version, String driver) {
        return new DatabaseDTO(name, dbType, version, driver);
    }

    public static DatabaseDTO of(String name) {
        return new DatabaseDTO(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
