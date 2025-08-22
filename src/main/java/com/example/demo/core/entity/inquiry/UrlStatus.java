package com.example.demo.core.entity.inquiry;

import com.example.demo.core.annotation.*;
import com.example.demo.core.entity.BaseEntity;

import java.time.LocalDateTime;

@Metadata(menu = "HealthCheck", title = "HealthCheck", type = "inquiry")
@Table(datasource = "h2", name = "url_status", primaryKey = "ID")
public class UrlStatus extends BaseEntity {
}
