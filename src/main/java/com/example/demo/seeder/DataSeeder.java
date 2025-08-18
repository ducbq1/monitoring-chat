package com.example.demo.seeder;

import com.example.demo.model.*;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.ResourceRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.TaskService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskService taskService;
    private final UserService userService;
    private final RoleRepository roleRepo;
    private final PermissionRepository permissionRepo;
    private final ResourceRepository resourceRepo;

    public DataSeeder(TaskService taskService, UserService userService, RoleRepository roleRepo, PermissionRepository permissionRepo, ResourceRepository resourceRepo) {
        this.taskService = taskService;
        this.userService = userService;
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.resourceRepo = resourceRepo;
    }

    @Override
    public void run(String... args) {

        List<Resource> resources = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            resources.add(resourceRepo.save(new Resource("Page " + i, "PAGE", "/page/" + i)));
        }
        for (int i = 1; i <= 10; i++) {
            resources.add(resourceRepo.save(new Resource("API User " + i, "API", "/api/users/" + i)));
        }
        for (int i = 1; i <= 10; i++) {
            resources.add(resourceRepo.save(new Resource("API Task " + i, "API", "/api/tasks/" + i)));
        }

        Permission perm1 = new Permission("VIEW_DASHBOARD", "View dashboard page");
        perm1.getResources().add(resources.get(0));

        Permission perm2 = new Permission("MANAGE_REPORTS", "Full access to reports");
        perm2.getResources().add(resources.get(1));

        Permission perm3 = new Permission("MANAGE_USERS", "CRUD users");
        perm3.getResources().addAll(resources.subList(10, 15));

        Permission perm4 = new Permission("MANAGE_TASKS", "CRUD tasks");
        perm4.getResources().addAll(resources.subList(20, 30));

        Permission perm5 = new Permission("VIEW_ANALYTICS", "View analytics page");
        perm5.getResources().add(resources.get(2));

        Permission perm6 = new Permission("EXPORT_REPORTS", "Export report data");
        perm6.getResources().add(resources.get(3));

        Permission perm7 = new Permission("IMPORT_USERS", "Import users data");
        perm7.getResources().add(resources.get(11));

        Permission perm8 = new Permission("DELETE_TASKS", "Delete tasks");
        perm8.getResources().add(resources.get(25));

        Permission perm9 = new Permission("VIEW_SETTINGS", "View system settings");
        perm9.getResources().add(resources.get(4));

        Permission perm10 = new Permission("MANAGE_NOTIFICATIONS", "Manage notifications");
        perm10.getResources().add(resources.get(5));

        List<Permission> permissions = Arrays.asList(
                perm1, perm2, perm3, perm4, perm5, perm6, perm7, perm8, perm9, perm10
        );
        permissionRepo.saveAll(permissions);

        Role ceo = new Role("CEO", "Chief Executive Officer");
        Role admin = new Role("ADMIN", "Chief Executive Officer");
        ceo.getPermissions().addAll(permissions);
        roleRepo.save(admin);
        roleRepo.save(ceo);

        User ceoUser = new User("ducbq1", "Bùi Quang Đức", null);
        ceoUser.getRoles().add(admin);
        ceoUser.getRoles().add(ceo);
        ceoUser.setAccessKey("111111");
        ceoUser.setEmail("bq.duc@vietinbank.vn");

        User manager1 = new User("manager1", "Nguyễn Quản Lý 1", ceoUser);
        User manager2 = new User("manager2", "Phạm Quản Lý 2", ceoUser);

        User staff1 = new User("staff1", "Lê Nhân Viên 1", manager1);
        User staff2 = new User("staff2", "Trịnh Nhân Viên 2", manager1);
        User staff3 = new User("staff3", "Vũ Nhân Viên 3", manager2);
        User staff4 = new User("staff4", "Đỗ Nhân Viên 4", manager2);

        User intern1 = new User("intern1", "Bùi Thực Tập 1", staff1);
        User intern2 = new User("intern2", "Hoàng Thực Tập 2", staff2);
        User intern3 = new User("intern3", "Ngô Thực Tập 3", staff3);
        User intern4 = new User("intern3", "Ngô Thực Tập 3", staff3);
        User intern5 = new User("intern3", "Ngô Thực Tập 3", staff3);

        List<User> users = List.of(ceoUser, manager1, manager2, staff1, staff2, staff3, staff4, intern1, intern2, intern3, intern4, intern5);
        users.forEach(userService::save);

        taskService.save(new Task(
                "Fix login bug",
                "Null pointer khi login",
                "TODO",
                List.of(staff1),
                List.of(manager1, ceoUser)
        ));

        taskService.save(new Task(
                "Triển khai Kafka",
                "Triển khai cho hệ thống",
                "IN_PROGRESS",
                List.of(staff2, staff3),
                List.of(manager2, ceoUser)
        ));

        taskService.save(new Task(
                "Viết tài liệu",
                "Thêm hướng dẫn sử dụng",
                "DONE",
                List.of(intern1, intern2),
                List.of(staff1, manager1)
        ));
    }
}
