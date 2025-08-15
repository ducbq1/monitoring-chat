package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/admin/users")
public class UserPermissionController {

    // ====== Fake data in-memory ======
    static record UserDto(Long id, String name, String email) {}
    static record ResourceDto(Long id, String name, String type) {}
    enum Action { VIEW, EDIT, DELETE, APPROVE }

    // users
    private static final List<UserDto> USERS = List.of(
            new UserDto(1L, "Alice", "alice@example.com"),
            new UserDto(2L, "Bob", "bob@example.com"),
            new UserDto(3L, "Charlie", "charlie@example.com")
    );

    // resources
    private static final List<ResourceDto> RESOURCES = List.of(
            new ResourceDto(101L, "Dashboard", "PAGE"),
            new ResourceDto(102L, "Reports", "PAGE"),
            new ResourceDto(201L, "Orders API", "API"),
            new ResourceDto(202L, "Payments API", "API")
    );

    // granted map: userId -> set "resourceId:ACTION"
    private static final Map<Long, Set<String>> GRANTED = new HashMap<>();
    static {
        GRANTED.put(1L, new HashSet<>(Arrays.asList("101:VIEW", "102:VIEW", "102:EDIT", "201:VIEW")));
        GRANTED.put(2L, new HashSet<>(Arrays.asList("101:VIEW", "201:VIEW", "201:EDIT", "202:VIEW")));
        GRANTED.put(3L, new HashSet<>(Collections.singletonList("101:VIEW")));
    }

    @GetMapping
    public String page(@RequestParam(value = "userId", required = false) Long userId,
                       @RequestParam(value = "q", required = false) String q,
                       Model model) {
        // filter user (search)
        List<UserDto> filtered = USERS;
        if (q != null && !q.isBlank()) {
            String s = q.toLowerCase();
            filtered = USERS.stream()
                    .filter(u -> u.name().toLowerCase().contains(s) || u.email().toLowerCase().contains(s))
                    .toList();
        }

        // pick selected user
        UserDto selected = null;
        if (userId != null) {
            for (UserDto u : USERS) if (u.id().equals(userId)) { selected = u; break; }
        }
        if (selected == null && !filtered.isEmpty()) selected = filtered.get(0);

        Set<String> granted = (selected == null) ? Set.of() : GRANTED.getOrDefault(selected.id(), Set.of());

        model.addAttribute("users", filtered);
        model.addAttribute("selectedUser", selected);
        model.addAttribute("resources", RESOURCES);
        model.addAttribute("actions", Action.values());
        model.addAttribute("granted", granted);
        model.addAttribute("q", q == null ? "" : q);
        return "view/user-permissions";
    }

    @PostMapping("/permissions")
    public String save(@RequestParam("userId") Long userId,
                       @RequestParam(value = "grants", required = false) List<String> grants,
                       @RequestParam(value = "q", required = false) String q) {
        // cập nhật quyền
        Set<String> newSet = new HashSet<>();
        if (grants != null) newSet.addAll(grants);
        GRANTED.put(userId, newSet);
        // quay lại trang với userId đang chọn
        return "redirect:/admin/users?userId=" + userId + (q != null && !q.isBlank() ? "&q=" + q : "");
    }
}
