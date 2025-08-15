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
    public ResponseEntity<List<RequestLog>> getAllLogs(
            @RequestParam String dbName
    ) {
        return ResponseEntity.ok(service.getAllLogs(dbName));
    }

    // Lấy log theo ID
    @GetMapping("/{id}")
    public ResponseEntity<RequestLog> getLogById(
            @RequestParam String dbName,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getLogById(dbName, id));
    }

    // Tạo log mới
    @PostMapping
    public ResponseEntity<Void> createLog(
            @RequestParam String dbName,
            @RequestBody RequestLog log
    ) {
        service.createLog(dbName, log);
        return ResponseEntity.ok().build();
    }

    // Cập nhật log
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLog(
            @RequestParam String dbName,
            @PathVariable Long id,
            @RequestBody RequestLog log
    ) {
        log.setId(id); // đảm bảo id đúng
        service.updateLog(dbName, log);
        return ResponseEntity.ok().build();
    }

    // Xóa log
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(
            @RequestParam String dbName,
            @PathVariable Long id
    ) {
        service.deleteLog(dbName, id);
        return ResponseEntity.ok().build();
    }
}
