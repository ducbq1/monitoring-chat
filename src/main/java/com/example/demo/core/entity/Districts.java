package com.example.demo.core.entity;

import com.example.demo.core.annotation.*;

@Metadata(menu = "Quận huyện", title = "Quận huyện")
@Table(datasource = "authentication", name = "Districts")
public class Districts extends BaseEntity {

    @PrimaryKey
    @Column(name = "code", title = "Mã")
    private String code;

    @Column(name = "name", title = "Giá trị")
    private String name;

    @Column(name = "full_name", title = "Tên đầy đủ")
    private String fullName;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
