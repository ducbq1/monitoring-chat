package com.example.demo.controller;

import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.exception.AppException;
import com.example.demo.helper.ViewHelper;
import com.example.demo.model.Permission;
import com.example.demo.model.Resource;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.ResourceRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/authorization")
public class AuthorizationController {

    private final DynamicDataSourceConfig dynamicDataSourceConfig;

    public AuthorizationController(DynamicDataSourceConfig dynamicDataSourceConfig) {
        this.dynamicDataSourceConfig = dynamicDataSourceConfig;
    }


    @GetMapping
    public String page(@RequestParam(value = "userId", required = false) Long userId,
                       @RequestParam(value = "q", required = false) String q,
                       Model model) {

        List<User> allUsers = jdbcTemplate().query("select user_id, user_name, location from bts_user", (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setUsername(rs.getString("user_name"));
            user.setFullName(rs.getString("user_name"));
            user.setEmail(rs.getString("location"));
            return user;
        });

        List<Resource> resources = jdbcTemplate().query("select resource_id, resource_name, resource_type from bts_resource order by resource_id", (rs, rowNum) -> {
            Resource resource = new Resource();
            resource.setId(rs.getLong("resource_id"));
            resource.setName(rs.getString("resource_name"));
            resource.setType(rs.getString("resource_type"));
            return resource;
        });

        List<User> filtered = allUsers;
        if (q != null && !q.isBlank()) {
            String s = q.toLowerCase();
            filtered = allUsers.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(s))
                    .toList();
        }

        Set<String> userPermissions = new LinkedHashSet<>();
        Set<String> granted = new HashSet<>();
        User selected = null;

        if (userId != null) {
            try {
                selected = jdbcTemplate().queryForObject("select user_id, user_name, location from bts_user where user_id = ?", new Long[] {userId},(rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("user_id"));
                    user.setUsername(rs.getString("user_name"));
                    user.setFullName(rs.getString("user_name"));
                    user.setEmail(rs.getString("location"));
                    return user;
                });
            } catch (DataAccessException e) {
                throw new AppException(e.getMessage(), e);
            }

            List<AuthContext> authContexts = jdbcTemplate().query("""
                    select distinct br.role_name, bp.name permission_name, brpn.resource_id from bts_user bu
                    join bts_user_role bur on bur.user_id = bu.user_id
                    join bts_role br on br.role_id = bur.role_id
                    join bts_role_permission brp on brp.role_id = br.role_id
                    join bts_permisison bp on bp.permission_id = brp.permission_id
                    left join bts_resource_permission brpn on brpn.permission_id = bp.permission_id
                    """, (rs, rowNum) -> new AuthContext(rs.getString("role_name"), rs.getString("permission_name"), rs.getLong("resource_id")));

            if (!authContexts.isEmpty()) {
                userPermissions = authContexts.stream().map(authContext -> authContext.permissionName).collect(Collectors.toSet());
                granted = authContexts.stream().map(authContext -> authContext.resourceId + ":" + authContext.permissionName).collect(Collectors.toSet());
            }

        }

        if (selected == null && !filtered.isEmpty()) selected = filtered.get(0);


        model.addAttribute("users", filtered);
        model.addAttribute("selectedUser", selected);
        model.addAttribute("resources", resources);
        model.addAttribute("actions", userPermissions);
        model.addAttribute("granted", granted);
        model.addAttribute("q", q == null ? "" : q);
        ViewHelper.setView(model, "view/user-permissions", "Permission Matrix");
        return "layout";
    }

    private JdbcTemplate jdbcTemplate() {
        return dynamicDataSourceConfig.getJdbcTemplate("bts");
    }

    private static record AuthContext(String roleName, String permissionName, Long resourceId) {};
}
