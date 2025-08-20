package com.example.demo.core.entity;

import com.example.demo.core.annotation.*;

import java.math.BigDecimal;

@Metadata(menu = "Khu vực", title = "Khu vực")
@Table(datasource = "authentication", name = "ADMINISTRATIVE_REGIONS")
public class Regions extends BaseEntity {

    @PrimaryKey
    @Column(name = "id", title = "Khóa")
    private BigDecimal id;

    @Column(name = "name", title = "Giá trị")
    private String name;

    @Column(name = "code_name", title = "Tên mã")
    private String codeName;

    @Column(name = "name_en", title = "Tên mã EN", required = true)
    private String nameEN;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }
}
