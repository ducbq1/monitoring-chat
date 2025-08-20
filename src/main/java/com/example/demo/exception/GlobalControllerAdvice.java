package com.example.demo.exception;

import com.example.demo.core.repository.GlobalSidebar;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final GlobalSidebar globalSidebar;

    public GlobalControllerAdvice(GlobalSidebar globalSidebar) {
        this.globalSidebar = globalSidebar;
    }

    @ModelAttribute
    public void addRequestUriToModel(HttpServletRequest request, Model model) {
        model.addAttribute("requestUri", request.getRequestURI());
    }

    @ModelAttribute("sidebarMenu")
    public List<Map<String, Object>> sidebarMenu() {
        return globalSidebar.getSidebarMenu();
    }
}
