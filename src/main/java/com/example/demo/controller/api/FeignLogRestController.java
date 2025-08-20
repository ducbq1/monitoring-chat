package com.example.demo.controller.api;

import com.example.demo.dto.request.LogRequestDTO;
import com.example.demo.model.FeignLog;
import com.example.demo.repository.FeignLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class FeignLogRestController extends BaseController {
    private final FeignLogRepository repository;

    public FeignLogRestController(FeignLogRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/api/feign-logs")
    public ResponseEntity<String> addLog(@RequestBody LogRequestDTO logRequest) {
        if (logRequest == null || logRequest.content() == null || logRequest.content().isBlank()) {
            return ResponseEntity.badRequest().body("Content is required");
        }

        FeignLog log = new FeignLog();
        log.setContent(logRequest.content());
        log.setTime(LocalDateTime.now());
        repository.save(log);

        return ResponseEntity.ok("Log saved successfully");
    }
}
