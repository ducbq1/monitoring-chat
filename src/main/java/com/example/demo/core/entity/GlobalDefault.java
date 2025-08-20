package com.example.demo.core.entity;

import com.example.demo.core.annotation.*;

@Metadata(menu = "Cấu hình tham số", title = "Cấu hình tham số")
@Table(datasource = "touchpoint", name = "global_defaults")
public class GlobalDefault extends BaseEntity {

    @PrimaryKey
    @Column(name = "name", title = "Tên khóa")
    private String name;

    @Column(name = "value", title = "Giá trị")
    private String value;

    public GlobalDefault() {}

    public GlobalDefault(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
