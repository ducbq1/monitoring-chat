package com.example.demo.core.entity;

import com.example.demo.core.annotation.*;

@Metadata(menu = "Hồ sơ thông tin khách hàng", title = "Hồ sơ thông tin khách hàng", type = "inquiry")
@Table(datasource = "profile", name = "CIF")
public class CustomerInfo extends BaseEntity {

    @PrimaryKey
    @Column(name = "name", title = "Tên khóa")
    private String name;

    @Column(name = "value", title = "Giá trị")
    private String value;

    public CustomerInfo() {}

    public CustomerInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
