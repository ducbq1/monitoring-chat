package com.example.demo.repository;

import com.example.demo.model.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByRoomOrderByTimestampAsc(String room);
}
