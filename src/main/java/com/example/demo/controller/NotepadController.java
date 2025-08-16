package com.example.demo.controller;

import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notepad")
public class NotepadController {

    private final NoteRepository noteRepo;

    public NotepadController(NoteRepository noteRepo) {
        this.noteRepo = noteRepo;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveNote(@RequestBody Note note) {
        note.setLastUpdated(LocalDateTime.now());
        return ResponseEntity.ok(noteRepo.save(note));
    }

    @GetMapping("/all")
    public List<Note> getAllNotes() {
        return noteRepo.findAll(Sort.by(Sort.Direction.DESC, "lastUpdated"));
    }
}
