package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "redirect", required = false) String redirect, Model model, HttpSession session) {
        if (session != null && session.getAttribute("user") != null) {
            String target = safeRedirect(redirect);
            return "redirect:" + (target != null ? target : "/dashboard");
        }
        model.addAttribute("redirect", redirect);
        return "view/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String accessKey, @RequestParam(required = false) String redirect, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = authService.authenticate(accessKey);
        if (userOpt.isPresent()) {
            session.setAttribute("user", userOpt.get()); // Lưu user vào session
            String target = safeRedirect(redirect);
            return "redirect:" + (target != null ? target : "/dashboard");
        } else {
            redirectAttributes.addFlashAttribute("redirect", redirect);
            redirectAttributes.addFlashAttribute("error", "Invalid access key");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String safeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank()) return null;
        String dec = URLDecoder.decode(redirect, StandardCharsets.UTF_8);
        if (dec.startsWith("http://") || dec.startsWith("https://")) return null;
        if (!dec.startsWith("/")) return null;
        return dec;
    }
}
