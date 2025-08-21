package com.example.demo.core.model;

import com.example.demo.core.annotation.Column;
import com.example.demo.core.entity.BaseEntity;

import java.util.Objects;

public class ColumnDataDTO {

    @Column(name = "columnName", title = "Tên cột")
    private String columnName;

    @Column(name = "typeName", title = "Kiểu dữ liệu")
    private String typeName;

    @Column(name = "size", title = "Kích thước")
    private int size;

    @Column(name = "nullable", title = "Cho phép null")
    private boolean nullable;

    @Column(name = "remarks", title = "Mô tả")
    private String remarks;

    @Column(name = "defaultValue", title = "Giá trị mặc định")
    private String defaultValue;

    @Column(name = "primaryKey", title = "Khóa chính")
    private boolean primaryKey;

    @Column(name = "value", title = "Giá trị")
    private Object value; // giá trị runtime nếu cần

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
