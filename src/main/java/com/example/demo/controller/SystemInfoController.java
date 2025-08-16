package com.example.demo.controller;

import com.example.demo.config.SystemInfoService;
import com.example.demo.helper.ViewHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SystemInfoController {

    private final SystemInfoService systemInfoService;

    public SystemInfoController(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    @GetMapping("/system-info")
    public String showSystemInfo(Model model) {
        model.addAttribute("systemInfo", systemInfoService.getSystemInfo());
        ViewHelper.setView(model, "view/system-info", "System Info");
        return "layout";
    }
}
