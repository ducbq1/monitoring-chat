package com.example.demo.infradb.controller;

import com.example.demo.infradb.entity.RequestLog;
import com.example.demo.infradb.service.RequestLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request-logs")
public class RequestLogController {

    private final RequestLogService service;

    public RequestLogController(RequestLogService service) {
        this.service = service;
    }

    // Lấy tất cả logs
    @GetMapping
    public ResponseEntity<List<RequestLog>> getAllLogs() {
        return ResponseEntity.ok(service.findAll());
    }

    // Lấy log theo ID
    @GetMapping("/{id}")
    public ResponseEntity<RequestLog> getLogById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Tạo log mới
    @PostMapping
    public ResponseEntity<Void> createLog(
            @RequestParam String dbName,
            @RequestBody RequestLog log
    ) {
        service.insert(log);
        return ResponseEntity.ok().build();
    }

    // Cập nhật log
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLog(
            @PathVariable Long id,
            @RequestBody RequestLog log
    ) {
        log.setId(id); // đảm bảo id đúng
        service.update(log);
        return ResponseEntity.ok().build();
    }

    // Xóa log
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(
            @PathVariable Long id
    ) {
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
