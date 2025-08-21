package com.example.demo.core.model;

import com.example.demo.core.annotation.Column;

import java.util.Objects;

public class ColumnData {

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

    private Object value; // giá trị runtime nếu cần

    private boolean autoIncrement; // cột tự tăng
    private boolean unique;        // cột unique
    private String foreignKeyTable; // tên bảng FK nếu có
    private String foreignKeyColumn; // tên cột FK nếu có

    // Constructor mặc định
    public ColumnData() {}

    // Constructor tiện lợi
    public ColumnData(String columnName, String typeName, int size, boolean nullable,
                      String remarks, String defaultValue, boolean primaryKey) {
        this.columnName = columnName;
        this.typeName = typeName;
        this.size = size;
        this.nullable = nullable;
        this.remarks = remarks;
        this.defaultValue = defaultValue;
        this.primaryKey = primaryKey;
    }

    // Getter/Setter
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

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getForeignKeyTable() {
        return foreignKeyTable;
    }

    public void setForeignKeyTable(String foreignKeyTable) {
        this.foreignKeyTable = foreignKeyTable;
    }

    public String getForeignKeyColumn() {
        return foreignKeyColumn;
    }

    public void setForeignKeyColumn(String foreignKeyColumn) {
        this.foreignKeyColumn = foreignKeyColumn;
    }

    @Override
    public String toString() {
        return "ColumnData{" +
                "columnName='" + columnName + '\'' +
                ", typeName='" + typeName + '\'' +
                ", size=" + size +
                ", nullable=" + nullable +
                ", remarks='" + remarks + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", primaryKey=" + primaryKey +
                ", autoIncrement=" + autoIncrement +
                ", unique=" + unique +
                ", foreignKeyTable='" + foreignKeyTable + '\'' +
                ", foreignKeyColumn='" + foreignKeyColumn + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColumnData)) return false;
        ColumnData that = (ColumnData) o;
        return Objects.equals(columnName, that.columnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName);
    }
}
