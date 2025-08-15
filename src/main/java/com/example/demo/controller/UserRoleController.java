package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/user-roles")
public class UserRoleController {

    @GetMapping
    public String listUsers(@RequestParam(value = "selected", required = false) String selectedUser, Model model) {
        // Fake Users
        List<String> users = Arrays.asList("Alice", "Bob", "Charlie");

        // Fake Role + Permission + Resource
        Map<String, Map<String, List<String>>> userRoles = new HashMap<>();

        userRoles.put("Alice", Map.of(
                "Admin", Arrays.asList("READ:Dashboard", "WRITE:Reports"),
                "Viewer", Arrays.asList("READ:Orders API")
        ));

        userRoles.put("Bob", Map.of(
                "Editor", Arrays.asList("READ:Articles", "WRITE:Articles")
        ));

        userRoles.put("Charlie", Map.of(
                "Viewer", Arrays.asList("READ:Dashboard", "READ:Reports")
        ));

        model.addAttribute("users", users);
        model.addAttribute("selectedUser", selectedUser);
        model.addAttribute("roles", selectedUser != null ? userRoles.get(selectedUser) : null);

        return "view/user-roles";
    }
}
