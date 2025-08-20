package com.example.demo.service;

import com.example.demo.model.RecordLock;
import com.example.demo.model.RecordLockStatus;
import com.example.demo.repository.RecordLockRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecordLockService {
    private final RecordLockRepository repo;

    public RecordLockService(RecordLockRepository repo) {
        this.repo = repo;
    }

    public Optional<RecordLock> findById(Long id) {
        return repo.findById(id);
    }

    public List<RecordLock> findAll() {
        return repo.findAll();
    }

    @Transactional
    public RecordLockStatus lock(Long recordId, String userId, String tabId, Duration ttl) {
        RecordLock lock = repo.findById(recordId).orElseGet(RecordLock::new);

        boolean canLock = lock.getId() == null
                || lock.getExpiresAt().isBefore(LocalDateTime.now())
                || (userId.equals(lock.getLockedBy()) && tabId.equals(lock.getLockedTab()));

        if (canLock) {
            lock.setId(recordId);
            lock.setLockedBy(userId);
            lock.setLockedTab(tabId);
            lock.setLockedAt(LocalDateTime.now());
            lock.setExpiresAt(LocalDateTime.now().plus(ttl));
            repo.saveAndFlush(lock);
        }

        boolean success = userId.equals(lock.getLockedBy()) && tabId.equals(lock.getLockedTab());
        return RecordLockStatus.of(lock, success);
    }

    @Transactional
    public void unlock(Long recordId, String userId, String tabId) {
        repo.findById(recordId).ifPresent(lock -> {
            if (lock.getLockedBy().equals(userId) && tabId.equals(lock.getLockedTab())) {
                repo.delete(lock);
            }
        });
    }

    @Transactional
    public boolean heartbeat(Long recordId, String userId, String tabId, Duration ttl) {
        return repo.findById(recordId).map(lock -> {
            if (lock.getLockedBy().equals(userId) && tabId.equals(lock.getLockedTab())) {
                lock.setExpiresAt(LocalDateTime.now().plus(ttl));
                repo.save(lock);
                return true;
            }
            return false;
        }).orElse(false);
    }
}
