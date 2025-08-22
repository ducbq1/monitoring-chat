package com.example.demo.core.model;

public class DatabaseDTO {
    private String name;
    private String catalog;
    private String dbType;
    private String version;
    private String driver;

    private DatabaseDTO(String name) {
        this.name = name;
    }

    private DatabaseDTO(String catalog, String dbType, String version, String driver) {
        this.catalog = catalog;
        this.dbType = dbType;
        this.version = version;
        this.driver = driver;
    }

    public DatabaseDTO update(DatabaseDTO databaseDTO) {
        this.catalog = databaseDTO.catalog;
        this.dbType = databaseDTO.dbType;
        this.version = databaseDTO.version;
        this.driver = databaseDTO.driver;
        return this;
    }

    public static DatabaseDTO of(String catalog, String dbType, String version, String driver) {
        return new DatabaseDTO(catalog, dbType, version, driver);
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

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
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
