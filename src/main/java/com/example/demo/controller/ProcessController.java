package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.JavaProcessInfo;
import com.example.demo.model.Note;
import com.example.demo.model.UrlStatus;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.UrlStatusRepository;
import com.example.demo.service.HealthCheckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/process")
public class ProcessController {

    @GetMapping
    public String showProcesses(Model model) {
        List<JavaProcessInfo> processes = ProcessHandle.allProcesses()
                .filter(ProcessHandle::isAlive)
                .filter(p -> p.info().command().orElse("").contains("java"))
                .map(JavaProcessInfo::from)
                .collect(Collectors.toList());
        model.addAttribute("javaProcesses", processes);
        ViewHelper.setView(model, "view/process", "Java Process Manager");
        return "layout"; // Load layout.html
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCSV() {
        List<ProcessHandle> processes = ProcessHandle.allProcesses()
                .filter(p -> p.info().command().orElse("").contains("java"))
                .collect(Collectors.toList());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        writer.println("PID,Command,User,Start Time,Total CPU Duration");

        for (ProcessHandle ph : processes) {
            String pid = String.valueOf(ph.pid());
            String command = ph.info().command().orElse("Unknown");
            String user = ph.info().user().orElse("-");
            String start = ph.info().startInstant().map(Instant::toString).orElse("-");
            String cpu = ph.info().totalCpuDuration().map(d -> d.toMillis() + " ms").orElse("-");
            writer.printf("%s,\"%s\",%s,%s,%s%n", pid, command, user, start, cpu);
        }

        writer.flush();
        byte[] csvBytes = outputStream.toByteArray();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=java-processes.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvBytes);
    }

    @PostMapping("/kill")
    public String killProcess(@RequestParam long pid, RedirectAttributes redirect) {
        Optional<ProcessHandle> ph = ProcessHandle.of(pid);
        if (ph.isPresent() && ph.get().destroy()) {
            redirect.addFlashAttribute("msg", "✅ Đã kill PID: " + pid);
        } else {
            redirect.addFlashAttribute("msg", "❌ Không thể kill PID: " + pid);
        }
        return "redirect:/process";
    }
}
