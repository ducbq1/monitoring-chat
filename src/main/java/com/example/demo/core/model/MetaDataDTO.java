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

    public MetaDataDTO() {
    }

    public MetaDataDTO(Builder builder) {
        this.menu = builder.menu;
        this.title = builder.title;
        this.icon = builder.icon;
        this.database = builder.database;
        this.table = builder.table;
    }

    public Map<String, Object> toMap() throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        for (Field field : getClass().getDeclaredFields()) {
            map.put(field.getName(), field.get(this));
        }
        map.put("href", "/admin/generic/" + type + "/" + table);
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

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String menu;
        private String title;
        private String icon;
        private DatabaseDTO database;
        private String table;

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

        public MetaDataDTO build() {
            return new MetaDataDTO(this);
        }
    }
}
