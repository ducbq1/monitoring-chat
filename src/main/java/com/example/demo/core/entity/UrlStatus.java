package com.example.demo.core.entity;

import com.example.demo.core.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@DataSource(name = "h2")
@Table(name = "url_status")
public class UrlStatus extends BaseEntity {

    @PrimaryKey
    @GeneratedValue
    @Column(name = "id", title = "Khóa chính")
    private Long id;

    @Column(name = "url", title = "Địa chỉ URL")
    private String url;

    @Column(name = "reachable", title = "Khả dụng")
    private boolean reachable;

    @Column(name = "type", title = "Loại")
    private String type;

    @Column(name = "description", title = "Mô tả")
    private String description;

    @Column(name = "last_checked", title = "Lần kiểm tra cuối")
    private LocalDateTime lastChecked;

    @Column(name = "response_time", title = "Thời gian phản hồi (ms)")
    private String responseTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(LocalDateTime lastChecked) {
        this.lastChecked = lastChecked;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }
}
