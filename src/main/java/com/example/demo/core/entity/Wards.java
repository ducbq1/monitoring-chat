package com.example.demo.core.entity;

import com.example.demo.core.annotation.*;

@Metadata(menu = "Xã", title = "Xã")
@Table(datasource = "authentication", name = "wards")
public class Wards extends BaseEntity {

    @PrimaryKey
    @Column(name = "code", title = "Mã")
    private String code;

    @Column(name = "name", title = "Tên")
    private String name;

    @Column(name = "district_code", title = "Mã xã")
    private String districtCode;

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

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }
}
