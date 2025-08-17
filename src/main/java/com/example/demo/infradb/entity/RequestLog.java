package com.example.demo.infradb.entity;

import com.example.demo.core.annotation.Column;
import com.example.demo.core.annotation.DataSource;
import com.example.demo.core.annotation.Table;
import com.example.demo.core.entity.BaseEntity;

import java.time.LocalDateTime;

@DataSource(name = "postgres")
@Table(name = "bts_request_log")
public class RequestLog extends BaseEntity {

    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "curl_pmh")
    private String curlPmh;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "method")
    private String method;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "traceparent")
    private String traceparent;

    @Column(name = "url")
    private String url;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCurlPmh() {
        return curlPmh;
    }

    public void setCurlPmh(String curlPmh) {
        this.curlPmh = curlPmh;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getTraceparent() {
        return traceparent;
    }

    public void setTraceparent(String traceparent) {
        this.traceparent = traceparent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
