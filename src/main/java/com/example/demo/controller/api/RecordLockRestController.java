package com.example.demo.controller.api;

import com.example.demo.model.RecordLock;
import com.example.demo.model.RecordLockStatus;
import com.example.demo.service.RecordLockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/record")
public class RecordLockRestController extends BaseController {
    private final RecordLockService  recordLockService;

    public RecordLockRestController(RecordLockService recordLockService) {
        this.recordLockService = recordLockService;
    }

    @PostMapping("/{id}/heartbeat")
    public ResponseEntity heartbeat(@PathVariable Long id,
                                    @RequestParam String userId,
                                    @RequestParam String tabId
    ) {
        return ok(recordLockService.heartbeat(id, userId, tabId,  Duration.ofMinutes(5)));
    }

    @PostMapping("/{id}/lock")
    public ResponseEntity lock(@PathVariable Long id,
                                 @RequestParam String userId,
                                 @RequestParam String tabId) {
        RecordLockStatus recordLockStatus = recordLockService.lock(id, userId, tabId, Duration.ofMinutes(5));
        return recordLockStatus.getLocked() ? ok(recordLockStatus) : response(HttpStatus.CONFLICT, recordLockStatus, "Locked by another user");
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity unlock(@PathVariable Long id,
                          @RequestParam String userId,
                          @RequestParam String tabId) {
        recordLockService.unlock(id, userId, tabId);
        return ok(null);
    }

    @GetMapping
    public ResponseEntity list() {
        return ok(recordLockService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity detail(@PathVariable Long id) {
        return ok(recordLockService.findById(id).orElseThrow());
    }
}
