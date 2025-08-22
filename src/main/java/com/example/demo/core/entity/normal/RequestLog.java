package com.example.demo.core.entity.normal;

import com.example.demo.core.annotation.*;
import com.example.demo.core.entity.BaseEntity;

import java.time.LocalDateTime;

@Metadata(menu = "Nhật ký thao tác", title = "Nhật ký thao tác")
@Table(datasource = "h2", name = "bts_request_log")
public class RequestLog extends BaseEntity {

    @PrimaryKey
    @Column(name = "id", title = "ID", type = ColumnType.TEXT)
    private Long id;

    @Column(name = "created_at", title = "Ngày tạo")
    private LocalDateTime createdAt;

    @Column(name = "created_by", title = "Người tạo")
    private String createdBy;

    @Column(name = "curl_pmh", title = "Curl PMH")
    private String curlPmh;

    @Column(name = "duration_ms", title = "Thời gian (ms)")
    private Long durationMs;

    @Column(name = "method", title = "Phương thức")
    private String method;

    @Column(name = "request_body", title = "Nội dung yêu cầu")
    private String requestBody;

    @Column(name = "request_id", title = "ID yêu cầu")
    private String requestId;

    @Column(name = "response_body", title = "Nội dung phản hồi")
    private String responseBody;

    @Column(name = "traceparent", title = "Traceparent")
    private String traceparent;

    @Column(name = "url", title = "URL")
    private String url;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCurlPmh() { return curlPmh; }
    public void setCurlPmh(String curlPmh) { this.curlPmh = curlPmh; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }

    public String getTraceparent() { return traceparent; }
    public void setTraceparent(String traceparent) { this.traceparent = traceparent; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
