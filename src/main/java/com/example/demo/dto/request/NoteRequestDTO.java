package com.example.demo.dto.request;

import com.example.demo.model.Note;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;

public record NoteRequestDTO(Long id, String title, String content, LocalDateTime lastUpdated) {
    public Note toEntity() {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);
        return note;
    }
}
