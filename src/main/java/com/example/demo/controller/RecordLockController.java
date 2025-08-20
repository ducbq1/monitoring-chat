package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.RecordLock;
import com.example.demo.model.RecordLockStatus;
import com.example.demo.service.RecordLockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/records")
public class RecordLockController {
    private final RecordLockService lockService;

    public RecordLockController(RecordLockService lockService) {
        this.lockService = lockService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("records", lockService.findAll());
        ViewHelper.setView(model, "view/record-lock/records", "Record Lock");
        return "layout";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        RecordLock recordLock = lockService.findById(id).orElseThrow();
        model.addAttribute("record", recordLock);
        ViewHelper.setView(model, "view/record-lock/record-detail", "Record Lock");
        return "layout";
    }
}
