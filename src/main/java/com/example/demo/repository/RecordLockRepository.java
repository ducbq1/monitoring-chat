package com.example.demo.repository;

import com.example.demo.model.RecordLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordLockRepository extends JpaRepository<RecordLock, Long> {
}
