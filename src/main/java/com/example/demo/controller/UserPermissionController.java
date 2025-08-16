package com.example.demo.controller;

import com.example.demo.helper.ViewHelper;
import com.example.demo.model.Permission;
import com.example.demo.model.Resource;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/users")
public class UserPermissionController {

    private final UserRepository userRepo;
    private final ResourceRepository resourceRepo;
    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;

    public UserPermissionController(UserRepository userRepo,
                                    ResourceRepository resourceRepo,
                                    RoleRepository roleRepo,
                                    PermissionRepository permissionRepo) {
        this.userRepo = userRepo;
        this.resourceRepo = resourceRepo;
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
    }

    @GetMapping
    public String page(@RequestParam(value = "userId", required = false) Long userId,
                       @RequestParam(value = "q", required = false) String q,
                       Model model) {
        // load all users
        List<User> allUsers = userRepo.findAll();

        // search
        List<User> filtered = allUsers;
        if (q != null && !q.isBlank()) {
            String s = q.toLowerCase();
            filtered = allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(s))
                    .toList();
        }

        // pick selected user
        User selected = null;
        if (userId != null) {
            selected = userRepo.findById(userId).orElse(null);
        }
        if (selected == null && !filtered.isEmpty()) selected = filtered.get(0);

        // Tập hợp permission name mà user hiện tại có
        Set<String> userPermissions = new LinkedHashSet<>();
        if (selected != null) {
            for (Role role : selected.getRoles()) {
                for (Permission p : role.getPermissions()) {
                    userPermissions.add(p.getName());
                }
            }
        }

        // lấy danh sách quyền hiện tại của user
        Set<String> granted = new HashSet<>();
        if (selected != null) {
            for (Role r : selected.getRoles()) {
                for (Permission p : r.getPermissions()) {
                    for (Resource res : p.getResources()) {
                        granted.add(res.getId() + ":" + p.getName());
                        // Ví dụ: "101:VIEW_DASHBOARD"
                    }
                }
            }
        }

        model.addAttribute("users", filtered);
        model.addAttribute("selectedUser", selected);
        model.addAttribute("resources", resourceRepo.findAll());
        model.addAttribute("actions", userPermissions);
        model.addAttribute("granted", granted);
        model.addAttribute("q", q == null ? "" : q);
        ViewHelper.setView(model, "view/user-permissions", "Permission Matrix");
        return "layout";
    }

    @PostMapping("/permissions")
    @Transactional
    public String save(@RequestParam("userId") Long userId,
                       @RequestParam(value = "grants", required = false) List<String> grants,
                       @RequestParam(value = "q", required = false) String q) {
        User user = userRepo.findById(userId).orElseThrow();

        // clear hết roles -> perms -> resources
        user.getRoles().clear();

        // tạo Role tạm (ví dụ "CUSTOM")
        Role customRole = roleRepo.findByName("CUSTOM")
                .orElseGet(() -> roleRepo.save(new Role("CUSTOM", "Custom permissions")));

        // clear quyền cũ trong role
        customRole.getPermissions().clear();

        if (grants != null) {
            for (String g : grants) {
                String[] parts = g.split(":");
                Long resId = Long.valueOf(parts[0]);
                String permName = parts[1];

                Resource res = resourceRepo.findById(resId).orElseThrow();

                Permission perm = permissionRepo.findByName(permName)
                        .orElseGet(() -> permissionRepo.save(new Permission(permName, permName)));

                perm.getResources().add(res);
                permissionRepo.save(perm);

                customRole.getPermissions().add(perm);
            }
        }

        roleRepo.save(customRole);
        user.getRoles().add(customRole);
        userRepo.save(user);

        return "redirect:/admin/users?userId=" + userId + (q != null && !q.isBlank() ? "&q=" + q : "");
    }
}
