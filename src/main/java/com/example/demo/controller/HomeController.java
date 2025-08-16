package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.Note;
import com.example.demo.model.TreeNode;
import com.example.demo.model.UrlStatus;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.UrlStatusRepository;
import com.example.demo.service.HealthCheckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class HomeController {
    private final UrlStatusRepository repo;
    private final HealthCheckService healthService;
    private final NoteRepository noteRepo;

    public HomeController(UrlStatusRepository repo, HealthCheckService healthService,  NoteRepository noteRepo) {
        this.repo = repo;
        this.healthService = healthService;
        this.noteRepo = noteRepo;
    }

    @GetMapping("/tree")
    public String treeView(Model model) {
        List<TreeNode> treeData = List.of(
                new TreeNode("1", "🏛️ C-Level", List.of(
                        new TreeNode("1-1", "👨‍💼 CTO - Chief Technology Officer", List.of(
                                new TreeNode("1-1-1", "📡 VP of Engineering", List.of(
                                        new TreeNode("1-1-1-1", "🧠 Software Architect", List.of()),
                                        new TreeNode("1-1-1-2", "⚙️ Engineering Manager", List.of(
                                                new TreeNode("1-1-1-2-1", "💻 Senior Backend Engineer", List.of()),
                                                new TreeNode("1-1-1-2-2", "🌐 Senior Frontend Engineer", List.of())
                                        ))
                                )),
                                new TreeNode("1-1-2", "🔐 VP of Infrastructure & Security", List.of(
                                        new TreeNode("1-1-2-1", "☁️ Cloud Architect", List.of()),
                                        new TreeNode("1-1-2-2", "🛡️ DevSecOps Engineer", List.of())
                                ))
                        )),
                        new TreeNode("1-2", "📈 CIO - Chief Information Officer", List.of(
                                new TreeNode("1-2-1", "🧩 Enterprise Architect", List.of()),
                                new TreeNode("1-2-2", "🖥️ IT Operations Manager", List.of())
                        ))
                )),

                new TreeNode("2", "👨‍🔧 Technical Roles", List.of(
                        new TreeNode("2-1", "💻 Backend Developer", List.of(
                                new TreeNode("2-1-1", "Java Developer", List.of()),
                                new TreeNode("2-1-2", "Go Developer", List.of()),
                                new TreeNode("2-1-3", "Python Developer", List.of())
                        )),
                        new TreeNode("2-2", "🌐 Frontend Developer", List.of(
                                new TreeNode("2-2-1", "React Developer", List.of()),
                                new TreeNode("2-2-2", "Angular Developer", List.of())
                        )),
                        new TreeNode("2-3", "🧠 AI/ML Engineer", List.of(
                                new TreeNode("2-3-1", "NLP Engineer", List.of()),
                                new TreeNode("2-3-2", "Computer Vision Engineer", List.of())
                        )),
                        new TreeNode("2-4", "🛠️ QA & Tester", List.of(
                                new TreeNode("2-4-1", "Automation Tester", List.of()),
                                new TreeNode("2-4-2", "Manual Tester", List.of())
                        ))
                )),

                new TreeNode("3", "🗂️ Supporting Roles", List.of(
                        new TreeNode("3-1", "🧑‍💼 Project Manager", List.of()),
                        new TreeNode("3-2", "📋 Business Analyst", List.of()),
                        new TreeNode("3-3", "🎨 UI/UX Designer", List.of()),
                        new TreeNode("3-4", "🧑‍🏫 Technical Writer", List.of())
                ))
        );

        model.addAttribute("treeData", treeData);
        ViewHelper.setView(model, "view/tree", "Tree View with Details");
        return "layout"; // Load layout.html
    }


    @GetMapping("/architecture")
    public String showArchitecturePage(Model model) {
        ViewHelper.setView(model, "view/architecture", "System Architecture");
        return "layout"; // Load layout.html
    }

    @GetMapping("/users")
    public String showUsers(Model model) {
        ViewHelper.setView(model, "view/users", "Settings & Users");
        return "layout"; // Load layout.html
    }

    @GetMapping("/settings")
    public String showSettings(Model model) {
        ViewHelper.setView(model, "view/settings", "Users Management");
        return "layout"; // Load layout.html
    }

    @GetMapping("/note")
    public String showNotepad(@RequestParam(required = false) Long id, Model model) {
        Note currentNote = (id != null) ? noteRepo.findById(id).get() : noteRepo.findTopByOrderByLastUpdatedDesc().orElseGet(() -> new Note("", ""));
        List<Note> allNotes = noteRepo.findAll();
        model.addAttribute("note", currentNote);
        model.addAttribute("noteList", allNotes);
        model.addAttribute("activeId", currentNote.getId());
        ViewHelper.setView(model, "view/note", " Insight Notepad");
        return "layout"; // Load layout.html
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

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
//        healthService.checkAll();
        Page<UrlStatus> urlPage = repo.findAll(PageRequest.of(page, size));
        model.addAttribute("urlPage", urlPage);
        ViewHelper.setView(model, "view/dashboard", "Dashboard");
        return "layout"; // Load layout.html
    }

    @GetMapping("/raw")
    public String showRaw(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "6") int size) {
        Page<UrlStatus> urlPage = repo.findAll(PageRequest.of(page, size));
        model.addAttribute("urlPage", urlPage);
        ViewHelper.setView(model, "view/raw", " Raw URL Status");
        return "layout"; // Load layout.html
    }
}
