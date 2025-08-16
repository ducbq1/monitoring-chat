package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String userInfo(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        User freshUser = userRepository.findById(user.getId()).orElse(user);
        model.addAttribute("user", freshUser);
        ViewHelper.setView(model, "view/user-info", "User Information");
        return "layout";
    }
}
