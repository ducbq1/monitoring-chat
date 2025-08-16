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

        Resource dashboard = resourceRepo.save(new Resource("Dashboard", "PAGE", "/dashboard"));
        Resource report = resourceRepo.save(new Resource("Report", "PAGE", "/reports"));
        Resource apiUser = resourceRepo.save(new Resource("User API", "API", "/api/users"));
        Resource apiTask = resourceRepo.save(new Resource("Task API", "API", "/api/tasks"));
        Resource apiTask1 = resourceRepo.save(new Resource("Task API", "API", "/api/tasks"));
        Resource apiTask2 = resourceRepo.save(new Resource("Task API", "API", "/api/tasks"));
        Resource apiTask3 = resourceRepo.save(new Resource("Task API", "API", "/api/tasks"));
        Resource apiTask4 = resourceRepo.save(new Resource("Task API", "API", "/api/tasks"));
        Resource apiTask5 = resourceRepo.save(new Resource("Task API", "API", "/api/tasks"));
        Resource apiTask6 = resourceRepo.save(new Resource("Task API", "API", "/api/tasks"));
        Resource apiTask7 = resourceRepo.save(new Resource("Task API", "API", "/api/tasks"));

        Permission viewDashboard = new Permission("VIEW_DASHBOARD", "View dashboard page");
        viewDashboard.getResources().add(dashboard);

        Permission manageReports = new Permission("MANAGE_REPORTS", "Full access to reports");
        manageReports.getResources().add(report);

        Permission manageUsers = new Permission("MANAGE_USERS", "CRUD users");
        manageUsers.getResources().add(apiUser);

        Permission manageTasks = new Permission("MANAGE_TASKS", "CRUD tasks");
        manageTasks.getResources().add(apiTask);
        manageTasks.getResources().add(apiTask1);
        manageTasks.getResources().add(apiTask2);
        manageTasks.getResources().add(apiTask3);
        manageTasks.getResources().add(apiTask4);
        manageTasks.getResources().add(apiTask5);
        manageTasks.getResources().add(apiTask6);
        manageTasks.getResources().add(apiTask7);

        permissionRepo.save(viewDashboard);
        permissionRepo.save(manageReports);
        permissionRepo.save(manageUsers);
        permissionRepo.save(manageTasks);

        Role ceo = new Role("CEO", "Chief Executive Officer");
        ceo.getPermissions().add(viewDashboard);
        ceo.getPermissions().add(manageReports);
        ceo.getPermissions().add(manageUsers);
        ceo.getPermissions().add(manageTasks);
        roleRepo.save(ceo);

        User ceoUser = new User("ceo", "Trần CEO", null);
        ceoUser.getRoles().add(ceo);
        ceoUser.setAccessKey("1111");

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
