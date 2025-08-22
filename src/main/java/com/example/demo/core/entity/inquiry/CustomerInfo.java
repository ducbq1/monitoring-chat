package com.example.demo.core.entity.inquiry;

import com.example.demo.core.annotation.*;
import com.example.demo.core.entity.BaseEntity;

@Metadata(menu = "Hồ sơ thông tin khách hàng", title = "Hồ sơ thông tin khách hàng", type = "inquiry")
@Table(datasource = "profile", name = "CIF", primaryKey = "ACN")
public class CustomerInfo extends BaseEntity {
}
