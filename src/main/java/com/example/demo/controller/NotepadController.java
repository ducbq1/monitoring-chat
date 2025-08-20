package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class NotepadController {

    private final NoteRepository noteRepo;

    public NotepadController(NoteRepository noteRepo) {
        this.noteRepo = noteRepo;
    }

    @GetMapping("/note")
    public String showNotepad(@RequestParam(required = false) Long id, Model model) {
        Note currentNote = (id != null) ? noteRepo.findById(id).get() : noteRepo.findTopByOrderByLastUpdatedDesc().orElseGet(() -> new Note("", ""));
        List<Note> allNotes = noteRepo.findAll();
        model.addAttribute("note", currentNote);
        model.addAttribute("noteList", allNotes);
        model.addAttribute("activeId", currentNote.getId());
        ViewHelper.setView(model, "view/note", " Insight Notepad");
        return "layout";
    }

    @GetMapping("/note/new")
    public String createNewNote(RedirectAttributes redirect) {
        Note note = new Note();
        note.setTitle("Ghi chú mới");
        note.setContent("");
        note = noteRepo.save(note);
        return "redirect:/note?id=" + note.getId();
    }

    @GetMapping("/note/delete/{id}")
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirect) {
        noteRepo.deleteById(id);
        return "redirect:/note";
    }
}
