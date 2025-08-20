package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchitectureController {
    @GetMapping("/architecture")
    public String showArchitecturePage(Model model) {
        ViewHelper.setView(model, "view/architecture", "System Architecture");
        return "layout";
    }
}
