package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        if (session != null && session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "view/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String accessKey, HttpSession session, Model model) {
        Optional<User> userOpt = authService.authenticate(accessKey);
        if (userOpt.isPresent()) {
            session.setAttribute("user", userOpt.get()); // Lưu user vào session
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "❌ Invalid access key");
            return "view/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
