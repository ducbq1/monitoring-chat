package com.example.demo.controller.api;

import com.example.demo.dto.request.NoteRequestDTO;
import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notepad")
public class NotepadRestController extends BaseController {

    private final NoteRepository noteRepo;

    public NotepadRestController(NoteRepository noteRepo) {
        this.noteRepo = noteRepo;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveNote(@RequestBody NoteRequestDTO note) {
        Note noteEntity = note.toEntity();
        noteEntity.setLastUpdated(LocalDateTime.now());
        return ResponseEntity.ok(noteRepo.save(noteEntity));
    }

    @GetMapping("/all")
    public List<Note> getAllNotes() {
        return noteRepo.findAll(Sort.by(Sort.Direction.DESC, "lastUpdated"));
    }
}
