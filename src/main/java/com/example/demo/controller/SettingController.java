package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingController {
    @GetMapping("/settings")
    public String showSettings(Model model) {
        ViewHelper.setView(model, "view/settings", "Users Management");
        return "layout";
    }
}
