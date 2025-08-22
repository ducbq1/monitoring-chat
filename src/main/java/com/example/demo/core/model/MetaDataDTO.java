package com.example.demo.core.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MetaDataDTO {
    private String menu;
    private String title;
    private String icon;
    private DatabaseDTO database;
    private String table;
    private String type;
    private String primaryKey;

    public MetaDataDTO() {
    }

    public MetaDataDTO(Builder builder) {
        this.menu = builder.menu;
        this.title = builder.title;
        this.icon = builder.icon;
        this.database = builder.database;
        this.table = builder.table;
        this.primaryKey = builder.primaryKey;
    }

    public Map<String, Object> toMap() throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        for (Field field : getClass().getDeclaredFields()) {
            map.put(field.getName(), field.get(this));
        }
        map.put("href", String.format("/admin/generic/%s/%s/%s", type, database.getName(), table));
        return map;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public DatabaseDTO getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseDTO database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String menu;
        private String title;
        private String icon;
        private DatabaseDTO database;
        private String table;
        private String primaryKey;

        public Builder menu(String menu) {
            this.menu = menu;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder database(DatabaseDTO database) {
            this.database = database;
            return this;
        }

        public Builder table(String table) {
            this.table = table;
            return this;
        }

        public Builder primaryKey(String primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        public MetaDataDTO build() {
            return new MetaDataDTO(this);
        }
    }
}
