package com.example.demo.controller;

import com.example.demo.model.FeignLog;
import com.example.demo.repository.FeignLogRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class FeignLogController {

    private final FeignLogRepository repository;

    public FeignLogController(FeignLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/feign-logs")
    public String showLogs(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size,
                           @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("time").descending());
        Page<FeignLog> logPage;

        if (keyword != null && !keyword.isBlank()) {
            logPage = repository.searchByContent(keyword, pageable);
        } else {
            logPage = repository.findAll(pageable);
        }

        model.addAttribute("logPage", logPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("view", "view/feign-logs");
        return "layout"; // Load layout.html
    }

    @GetMapping("/feign-logs/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=feign-logs.csv");

        List<FeignLog> logs = repository.findAll();
        PrintWriter writer = response.getWriter();
        writer.println("ID,Content,Time");

        for (FeignLog log : logs) {
            writer.printf("%d,\"%s\",%s%n",
                    log.getId(),
                    log.getContent().replace("\"", "\"\""),
                    log.getTime().toString()
            );
        }
        writer.flush();
    }

}
