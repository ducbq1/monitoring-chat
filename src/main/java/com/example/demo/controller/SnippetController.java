package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.Snippet;
import com.example.demo.repository.SnippetRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/snippets")
public class SnippetController {

    private final SnippetRepo repo;

    public SnippetController(SnippetRepo repo) {
        this.repo = repo;
    }

    @GetMapping
    public String listSnippets(Model model) {
        List<Snippet> snippets = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        snippets.forEach(Snippet::prepareTagList); // Tách tagList
        model.addAttribute("snippetList", snippets);
        ViewHelper.setView(model, "view/snippet", "Code Snippet Manager");
        return "layout";
    }

    @PostMapping("/save")
    public String saveSnippet(@ModelAttribute Snippet snippet) {
        repo.save(snippet);
        return "redirect:/snippets";
    }

    @GetMapping("/delete/{id}")
    public String deleteSnippet(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/snippets";
    }
}
