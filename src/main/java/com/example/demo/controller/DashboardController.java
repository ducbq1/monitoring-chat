package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.UrlStatus;
import com.example.demo.repository.UrlStatusRepository;
import com.example.demo.service.HealthCheckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {
    private final HealthCheckService healthService;
    private final UrlStatusRepository urlStatusRepository;

    public DashboardController(HealthCheckService healthService, UrlStatusRepository urlStatusRepository) {
        this.healthService = healthService;
        this.urlStatusRepository = urlStatusRepository;
    }

    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        Page<UrlStatus> urlPage = urlStatusRepository.findAll(PageRequest.of(page, size));
        model.addAttribute("urlPage", urlPage);
        ViewHelper.setView(model, "view/dashboard", "Dashboard");
        return "layout";
    }

    @GetMapping("/raw")
    public String showRaw(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "6") int size) {
        Page<UrlStatus> urlPage = urlStatusRepository.findAll(PageRequest.of(page, size));
        model.addAttribute("urlPage", urlPage);
        ViewHelper.setView(model, "view/raw", " Raw URL Status");
        return "layout";
    }
}
