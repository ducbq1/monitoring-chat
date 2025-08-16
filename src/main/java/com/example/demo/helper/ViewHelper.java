package com.example.demo.helper;

import org.springframework.ui.Model;

public class ViewHelper {
    public static void setView(Model model, String view, String pageTitle) {
        model.addAttribute("view", view);
        model.addAttribute("pageTitle", pageTitle);
    }
}
